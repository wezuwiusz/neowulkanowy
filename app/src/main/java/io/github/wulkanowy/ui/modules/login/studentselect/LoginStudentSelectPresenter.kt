package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.enums.MessageType
import io.github.wulkanowy.data.flatResourceFlow
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.mappers.mapToStudentWithSemesters
import io.github.wulkanowy.data.onResourceData
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.pojos.RegisterStudent
import io.github.wulkanowy.data.pojos.RegisterSymbol
import io.github.wulkanowy.data.pojos.RegisterUnit
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SchoolsRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.domain.adminmessage.GetAppropriateAdminMessageUseCase
import io.github.wulkanowy.sdk.scrapper.exception.StudentGraduateException
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginData
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.ui.modules.login.support.LoginSupportInfo
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.isCurrent
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val schoolsRepository: SchoolsRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val syncManager: SyncManager,
    private val analytics: AnalyticsHelper,
    private val appInfo: AppInfo,
    private val preferencesRepository: PreferencesRepository,
    private val getAppropriateAdminMessageUseCase: GetAppropriateAdminMessageUseCase
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    private lateinit var registerUser: RegisterUser
    private lateinit var loginData: LoginData

    private lateinit var students: List<StudentWithSemesters>
    private var isEmptySymbolsExpanded = false
    private var expandedSymbolError: RegisterSymbol? = null
    private var expandedSchoolError: RegisterUnit? = null

    private val selectedStudents = mutableListOf<LoginStudentSelectItem.Student>()

    fun onAttachView(
        view: LoginStudentSelectView,
        loginData: LoginData,
        registerUser: RegisterUser,
    ) {
        super.onAttachView(view)
        with(view) {
            initView()
            enableSignIn(false)
            loginErrorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        this.loginData = loginData
        this.registerUser = registerUser
        loadData()
        loadAdminMessage()
    }

    private fun loadData() {
        resetSelectedState()

        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            students = it.dataOrNull.orEmpty()
            when (it) {
                is Resource.Loading -> Timber.d("Login student select students load started")
                is Resource.Success -> {
                    getStudentsWithCurrentlyActiveSemesters()
                    selectedStudents.clear()
                    selectedStudents.addAll(getStudentsWithCurrentlyActiveSemesters())
                    view?.enableSignIn(selectedStudents.isNotEmpty())
                    refreshItems()
                }

                is Resource.Error -> {
                    errorHandler.dispatch(it.error)
                    lastError = it.error
                    refreshItems()
                }
            }
        }.launch("load_data")
    }

    private fun loadAdminMessage() {
        flatResourceFlow {
            getAppropriateAdminMessageUseCase(
                scrapperBaseUrl = registerUser.scrapperBaseUrl.orEmpty(),
                type = MessageType.LOGIN_STUDENT_SELECT_MESSAGE,
            )
        }
            .logResourceStatus("load login admin message")
            .onResourceData { view?.showAdminMessage(it) }
            .onResourceError { view?.showAdminMessage(null) }
            .launch("load_admin_message")
    }

    private fun getStudentsWithCurrentlyActiveSemesters(): List<LoginStudentSelectItem.Student> {
        val students = registerUser.symbols.flatMap { symbol ->
            symbol.schools.flatMap { unit ->
                unit.students.map {
                    createStudentItem(it, symbol, unit, students)
                }
            }
        }
        return students
            .filter { it.isEnabled }
            .filter { student ->
                student.student.semesters.any { semester ->
                    semester.isCurrent()
                }
            }
    }

    private fun createItems(): List<LoginStudentSelectItem> = buildList {
        val notEmptySymbols = registerUser.symbols.filter { it.shouldShowOnTop() }
        val emptySymbols = registerUser.symbols.filter { !it.shouldShowOnTop() }

        if (emptySymbols.isNotEmpty() && notEmptySymbols.isNotEmpty() && emptySymbols.any { it.symbol == loginData.userEnteredSymbol }) {
            add(createEmptySymbolItem(emptySymbols.first { it.symbol == loginData.userEnteredSymbol }))
        }

        addAll(createNotEmptySymbolItems(notEmptySymbols, students))
        addAll(createEmptySymbolItems(emptySymbols, notEmptySymbols.isNotEmpty()))

        val helpItem = LoginStudentSelectItem.Help(
            onEnterSymbolClick = ::onEnterSymbol,
            onContactUsClick = ::onEmailClick,
            onDiscordClick = ::onDiscordClick,
            isSymbolButtonVisible = "login" !in loginData.baseUrl,
        )
        add(helpItem)
    }

    private fun RegisterSymbol.shouldShowOnTop(): Boolean {
        return schools.isNotEmpty() || error is StudentGraduateException
    }

    private fun createNotEmptySymbolItems(
        notEmptySymbols: List<RegisterSymbol>,
        students: List<StudentWithSemesters>,
    ) = buildList {
        notEmptySymbols.forEach { registerSymbol ->
            val symbolHeader = LoginStudentSelectItem.SymbolHeader(
                symbol = registerSymbol,
                humanReadableName = view?.symbols?.get(registerSymbol.symbol),
                isErrorExpanded = expandedSymbolError == registerSymbol,
                onClick = ::onSymbolItemClick,
            )
            add(symbolHeader)

            registerSymbol.schools.forEach { registerUnit ->
                val schoolHeader = LoginStudentSelectItem.SchoolHeader(
                    unit = registerUnit,
                    isErrorExpanded = expandedSchoolError == registerUnit,
                    onClick = ::onUnitItemClick,
                )
                add(schoolHeader)

                registerUnit.students.forEach {
                    add(createStudentItem(it, registerSymbol, registerUnit, students))
                }
            }
        }
    }

    private fun createStudentItem(
        student: RegisterStudent,
        symbol: RegisterSymbol,
        school: RegisterUnit,
        students: List<StudentWithSemesters>,
    ) = LoginStudentSelectItem.Student(
        symbol = symbol,
        unit = school,
        student = student,
        onClick = ::onItemSelected,
        isEnabled = students.none {
            it.student.email == registerUser.login
                && it.student.symbol == symbol.symbol
                && it.student.studentId == student.studentId
                && it.student.schoolSymbol == school.schoolId
                && it.student.classId == student.classId
        },
        isSelected = selectedStudents
            .filter { it.symbol.symbol == symbol.symbol }
            .filter { it.unit.schoolId == school.schoolId }
            .filter { it.student.studentId == student.studentId }
            .filter { it.student.classId == student.classId }
            .size == 1,
    )

    private fun createEmptySymbolItems(
        emptySymbols: List<RegisterSymbol>,
        isNotEmptySymbolsExist: Boolean,
    ) = buildList {
        val filteredEmptySymbols = emptySymbols.filter {
            it.error !is InvalidSymbolException
        }.ifEmpty { emptySymbols.takeIf { !isNotEmptySymbolsExist }.orEmpty() }

        if (filteredEmptySymbols.isNotEmpty() && isNotEmptySymbolsExist) {
            val emptyHeader = LoginStudentSelectItem.EmptySymbolsHeader(
                isExpanded = isEmptySymbolsExpanded,
                onClick = ::onEmptySymbolsToggle,
            )
            add(emptyHeader)
            if (isEmptySymbolsExpanded) {
                filteredEmptySymbols.forEach {
                    add(createEmptySymbolItem(it))
                }
            }
        }

        if (filteredEmptySymbols.isNotEmpty() && !isNotEmptySymbolsExist) {
            filteredEmptySymbols.forEach {
                add(createEmptySymbolItem(it))
            }
        }
    }

    private fun createEmptySymbolItem(registerSymbol: RegisterSymbol) =
        LoginStudentSelectItem.SymbolHeader(
            symbol = registerSymbol,
            humanReadableName = view?.symbols?.get(registerSymbol.symbol),
            isErrorExpanded = expandedSymbolError == registerSymbol,
            onClick = ::onSymbolItemClick,
        )

    fun onSignIn() {
        registerStudents(selectedStudents)
    }

    private fun onEmptySymbolsToggle() {
        isEmptySymbolsExpanded = !isEmptySymbolsExpanded

        refreshItems()
    }

    private fun onItemSelected(item: LoginStudentSelectItem.Student) {
        if (!item.isEnabled) return

        selectedStudents
            .removeAll {
                it.student.studentId == item.student.studentId &&
                    it.student.classId == item.student.classId &&
                    it.unit.schoolId == item.unit.schoolId &&
                    it.symbol.symbol == item.symbol.symbol
            }
            .let { if (!it) selectedStudents.add(item) }

        view?.enableSignIn(selectedStudents.isNotEmpty())
        refreshItems()
    }

    private fun onSymbolItemClick(symbol: RegisterSymbol) {
        expandedSymbolError = if (symbol != expandedSymbolError) symbol else null
        refreshItems()
    }

    private fun onUnitItemClick(unit: RegisterUnit) {
        expandedSchoolError = if (unit != expandedSchoolError) unit else null
        refreshItems()
    }

    private fun resetSelectedState() {
        selectedStudents.clear()
        view?.enableSignIn(false)
    }

    private fun refreshItems() {
        view?.updateData(createItems())
    }

    private fun registerStudents(students: List<LoginStudentSelectItem>) {
        val filteredStudents = students.filterIsInstance<LoginStudentSelectItem.Student>()
        val studentsWithSemesters = filteredStudents.map { item ->
            item.student.mapToStudentWithSemesters(
                user = registerUser,
                symbol = item.symbol,
                scrapperDomainSuffix = loginData.domainSuffix,
                unit = item.unit,
                colors = appInfo.defaultColorsForAvatar,
            )
        }
        resourceFlow {
            studentRepository.saveStudents(studentsWithSemesters)
            schoolsRepository.logSchoolLogin(loginData, studentsWithSemesters)
        }
            .logResourceStatus("registration")
            .onEach {
                when (it) {
                    is Resource.Loading -> view?.run {
                        showProgress(true)
                        showContent(false)
                    }

                    is Resource.Success -> {
                        syncManager.startOneTimeSyncWorker(quiet = true)
                        view?.navigateToNext()
                        logRegisterEvent(studentsWithSemesters)
                    }

                    is Resource.Error -> {
                        view?.apply {
                            showProgress(false)
                            showContent(true)
                        }
                        lastError = it.error
                        loginErrorHandler.dispatch(it.error)
                        logRegisterEvent(studentsWithSemesters, it.error)
                    }
                }
            }.launch("register")
    }

    private fun onEnterSymbol() {
        view?.navigateToSymbol(loginData)
    }

    private fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    private fun onEmailClick() {
        view?.openEmail(
            LoginSupportInfo(
                loginData = loginData,
                registerUser = registerUser,
                lastErrorMessage = lastError?.message,
                enteredSymbol = loginData.userEnteredSymbol,
            )
        )
    }

    private fun logRegisterEvent(
        studentsWithSemesters: List<StudentWithSemesters>,
        error: Throwable? = null
    ) {
        studentsWithSemesters.forEach { student ->
            analytics.logEvent(
                "registration_student_select",
                "success" to (error != null),
                "scrapperBaseUrl" to student.student.scrapperBaseUrl,
                "symbol" to student.student.symbol,
                "error" to (error?.message?.ifBlank { "No message" } ?: "No error")
            )
        }
    }

    fun onAdminMessageSelected(url: String?) {
        url?.let { view?.openInternetBrowser(it) }
    }

    fun onAdminMessageDismissed(adminMessage: AdminMessage) {
        preferencesRepository.dismissedAdminMessageIds += adminMessage.id

        view?.showAdminMessage(null)
    }
}
