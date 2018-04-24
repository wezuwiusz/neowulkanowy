package io.github.wulkanowy.data.sync.account;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.Student;
import io.github.wulkanowy.data.db.dao.entities.StudentDao;
import io.github.wulkanowy.data.db.dao.entities.Symbol;
import io.github.wulkanowy.data.db.dao.entities.SymbolDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.security.CryptoException;
import io.github.wulkanowy.utils.security.Scrambler;

@Singleton
public class AccountSync implements AccountSyncContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private final Context context;

    @Inject
    AccountSync(DaoSession daoSession, SharedPrefContract sharedPref,
                Vulcan vulcan, @ApplicationContext Context context) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
        this.context = context;
    }

    @Override
    public void registerUser(String email, String password, String symbol)
            throws VulcanException, IOException, CryptoException {

        vulcan.setCredentials(email, password, symbol, null, null, null);

        daoSession.getDatabase().beginTransaction();

        try {
            Account account = insertAccount(email, password);
            Symbol symbolEntity = insertSymbol(account);
            insertStudents(symbolEntity);
            insertDiaries(symbolEntity);
            insertSemesters();

            sharedPref.setCurrentUserId(account.getId());

            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }

    private Account insertAccount(String email, String password) throws CryptoException {
        LogUtils.debug("Register account: " + email);
        Account account = new Account()
                .setEmail(email)
                .setPassword(Scrambler.encrypt(email, password, context));
        daoSession.getAccountDao().insert(account);
        return account;
    }

    private Symbol insertSymbol(Account account) throws VulcanException, IOException {
        LogUtils.debug("Register symbol: " + vulcan.getSymbol());
        Symbol symbol = new Symbol()
                .setUserId(account.getId())
                .setSchoolId(vulcan.getStudentAndParent().getSchoolID())
                .setSymbol(vulcan.getSymbol());
        daoSession.getSymbolDao().insert(symbol);

        return symbol;
    }

    private void insertStudents(Symbol symbol) throws VulcanException, IOException {
        List<Student> studentList = DataObjectConverter.studentsToStudentEntities(
                vulcan.getStudentAndParent().getStudents(),
                symbol.getId()
        );
        LogUtils.debug("Register students: " + studentList.size());
        daoSession.getStudentDao().insertInTx(studentList);
    }

    private void insertDiaries(Symbol symbolEntity) throws VulcanException, IOException {
        List<Diary> diaryList = DataObjectConverter.diariesToDiaryEntities(
                vulcan.getStudentAndParent().getDiaries(),
                daoSession.getStudentDao().queryBuilder().where(
                        StudentDao.Properties.SymbolId.eq(symbolEntity.getId()),
                        StudentDao.Properties.Current.eq(true)
                ).unique().getId());
        LogUtils.debug("Register diaries: " + diaryList.size());
        daoSession.getDiaryDao().insertInTx(diaryList);
    }

    private void insertSemesters() throws VulcanException, IOException {
        List<Semester> semesterList = DataObjectConverter.semestersToSemesterEntities(
                vulcan.getStudentAndParent().getSemesters(),
                daoSession.getDiaryDao().queryBuilder().where(
                        DiaryDao.Properties.Current.eq(true)
                ).unique().getId());
        LogUtils.debug("Register semesters: " + semesterList.size());
        daoSession.getSemesterDao().insertInTx(semesterList);
    }

    @Override
    public void initLastUser() throws IOException, CryptoException {

        long userId = sharedPref.getCurrentUserId();

        if (userId == 0) {
            throw new IOException("Can't find saved user");
        }

        LogUtils.debug("Initialization current user id=" + userId);

        Account account = daoSession.getAccountDao().load(userId);
        String email = account.getEmail();
        String pass = Scrambler.decrypt(account.getEmail(), account.getPassword());

        Symbol symbolE = daoSession.getSymbolDao().queryBuilder().where(
                SymbolDao.Properties.UserId.eq(account.getId())).unique();
        String symbol = symbolE.getSymbol();
        String schoolId = symbolE.getSchoolId();

        Student studentE = daoSession.getStudentDao().queryBuilder().where(
                StudentDao.Properties.SymbolId.eq(symbolE.getId()),
                StudentDao.Properties.Current.eq(true)
        ).unique();
        String studentId = studentE.getRealId();

        Diary diaryE = daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(studentE.getId()),
                DiaryDao.Properties.Current.eq(true)
        ).unique();
        String diaryId = diaryE.getValue();

        vulcan.setCredentials(email, pass, symbol, schoolId, studentId, diaryId);
    }
}
