package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.pojos.RegisterStudent
import io.github.wulkanowy.data.pojos.RegisterSymbol
import io.github.wulkanowy.data.pojos.RegisterUnit

sealed class LoginStudentSelectItem(val type: LoginStudentSelectItemType) {

    data class EmptySymbolsHeader(
        val isExpanded: Boolean,
        val onClick: () -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.EMPTY_SYMBOLS_HEADER)

    data class SymbolHeader(
        val symbol: RegisterSymbol,
        val humanReadableName: String?,
        val isErrorExpanded: Boolean,
        val onClick: (RegisterSymbol) -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.SYMBOL_HEADER)

    data class SchoolHeader(
        val unit: RegisterUnit,
        val isErrorExpanded: Boolean,
        val onClick: (RegisterUnit) -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.SCHOOL_HEADER)

    data class Student(
        val symbol: RegisterSymbol,
        val unit: RegisterUnit,
        val student: RegisterStudent,
        val isEnabled: Boolean,
        val isSelected: Boolean,
        val onClick: (Student) -> Unit,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.STUDENT)

    data class Help(
        val onEnterSymbolClick: () -> Unit,
        val onContactUsClick: () -> Unit,
        val onDiscordClick: () -> Unit,
        val isSymbolButtonVisible: Boolean,
    ) : LoginStudentSelectItem(LoginStudentSelectItemType.HELP)
}

enum class LoginStudentSelectItemType {
    EMPTY_SYMBOLS_HEADER,
    SYMBOL_HEADER,
    SCHOOL_HEADER,
    STUDENT,
    HELP,
}
