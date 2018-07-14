package io.github.wulkanowy.data.sync;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
import io.github.wulkanowy.data.db.dao.entities.School;
import io.github.wulkanowy.data.db.dao.entities.SchoolDao;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.Student;
import io.github.wulkanowy.data.db.dao.entities.StudentDao;
import io.github.wulkanowy.data.db.dao.entities.Symbol;
import io.github.wulkanowy.data.db.dao.entities.SymbolDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.security.CryptoException;
import io.github.wulkanowy.utils.security.Scrambler;
import timber.log.Timber;

@Singleton
public class AccountSync {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private final Context context;

    @Inject
    AccountSync(DaoSession daoSession, SharedPrefContract sharedPref,
                Vulcan vulcan, Context context) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
        this.context = context;
    }

    public void registerUser(String email, String password, String symbol)
            throws VulcanException, IOException, CryptoException {

        clearUserData();

        vulcan.setCredentials(email, password, symbol, null, null, null);

        daoSession.getDatabase().beginTransaction();

        Timber.i("Register start");

        try {
            Account account = insertAccount(email, password);
            Symbol symbolEntity = insertSymbol(account);
            School schoolEntity = insertSchools(symbolEntity);
            Student student = insertStudents(schoolEntity);
            Diary diary = insertDiaries(student);
            insertSemesters(diary);

            sharedPref.setCurrentUserId(account.getId());

            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }

        Timber.i("Register end");
    }

    private Account insertAccount(String email, String password) throws CryptoException {
        Timber.d("Register account");
        Account account = new Account()
                .setEmail(email)
                .setPassword(Scrambler.encrypt(email, password, context));
        daoSession.getAccountDao().insert(account);
        return account;
    }

    private Symbol insertSymbol(Account account) throws VulcanException, IOException {
        vulcan.getSchools();
        Timber.d("Register symbol (%s)", vulcan.getSymbol());
        Symbol symbol = new Symbol()
                .setUserId(account.getId())
                .setSymbol(vulcan.getSymbol());
        daoSession.getSymbolDao().insert(symbol);

        return symbol;
    }

    private School insertSchools(Symbol symbol) throws VulcanException, IOException {
        List<School> schoolList = DataObjectConverter.schoolsToSchoolsEntities(
                vulcan.getSchools(),
                symbol.getId()
        );
        Timber.d("Register schools (%s)", schoolList.size());
        daoSession.getSchoolDao().insertInTx(schoolList);

        return daoSession.getSchoolDao().queryBuilder().where(
                SchoolDao.Properties.SymbolId.eq(symbol.getId()),
                SchoolDao.Properties.Current.eq(true)
        ).unique();
    }

    private Student insertStudents(School school) throws VulcanException, IOException {
        List<Student> studentList = DataObjectConverter.studentsToStudentEntities(
                vulcan.getStudentAndParent().getStudents(),
                school.getId()
        );
        Timber.d("Register students (%s)", studentList.size());
        daoSession.getStudentDao().insertInTx(studentList);

        return daoSession.getStudentDao().queryBuilder().where(
                StudentDao.Properties.SchoolId.eq(school.getId()),
                StudentDao.Properties.Current.eq(true)
        ).unique();
    }

    private Diary insertDiaries(Student student) throws VulcanException, IOException {
        List<Diary> diaryList = DataObjectConverter.diariesToDiaryEntities(
                vulcan.getStudentAndParent().getDiaries(),
                student.getId()
        );
        Timber.d("Register diaries (%s)", diaryList.size());
        daoSession.getDiaryDao().insertInTx(diaryList);

        return daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(student.getId()),
                DiaryDao.Properties.Current.eq(true)
        ).unique();
    }

    private void insertSemesters(Diary diary) throws VulcanException, IOException {
        List<Semester> semesterList = DataObjectConverter.semestersToSemesterEntities(
                vulcan.getStudentAndParent().getSemesters(),
                diary.getId()
        );
        Timber.d("Register semesters (%s)", semesterList.size());
        daoSession.getSemesterDao().insertInTx(semesterList);
    }

    public void initLastUser() throws CryptoException {

        long userId = sharedPref.getCurrentUserId();

        if (userId == 0) {
            throw new NotRegisteredUserException("Can't find user id in SharedPreferences");
        }

        Timber.d("Init current user (%s)", userId);

        Account account = daoSession.getAccountDao().load(userId);

        Symbol symbol = daoSession.getSymbolDao().queryBuilder().where(
                SymbolDao.Properties.UserId.eq(account.getId())).unique();

        School school = daoSession.getSchoolDao().queryBuilder().where(
                SchoolDao.Properties.SymbolId.eq(symbol.getId())).unique();

        Student student = daoSession.getStudentDao().queryBuilder().where(
                StudentDao.Properties.SchoolId.eq(school.getId()),
                StudentDao.Properties.Current.eq(true)
        ).unique();

        Diary diary = daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(student.getId()),
                DiaryDao.Properties.Current.eq(true)
        ).unique();

        vulcan.setCredentials(
                account.getEmail(),
                Scrambler.decrypt(account.getEmail(), account.getPassword()),
                symbol.getSymbol(),
                school.getRealId(),
                student.getRealId(),
                diary.getValue()
        );
    }

    private void clearUserData() {
        Database database = daoSession.getDatabase();
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, true);
        sharedPref.setCurrentUserId(0);
    }
}
