package io.github.wulkanowy.data.sync.account;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
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

        LogUtils.debug("Register new user email=" + email);

        vulcan.setCredentials(email, password, symbol, null, null, null);

        Account account = new Account()
                .setName(vulcan.getBasicInformation().getPersonalData().getFirstAndLastName())
                .setEmail(email)
                .setPassword(Scrambler.encrypt(email, password, context))
                .setSymbol(vulcan.getSymbol())
                .setSchoolId(vulcan.getStudentAndParent().getSchoolID())
                .setRealId(vulcan.getStudentAndParent().getStudentID());

        List<Diary> diaryList = DataObjectConverter.diariesToDiaryEntities(
                vulcan.getStudentAndParent().getDiaries());

        daoSession.getAccountDao().insert(account);
        daoSession.getDiaryDao().insertInTx(diaryList);

        sharedPref.setCurrentUserId(account.getId());
    }

    @Override
    public void initLastUser() throws IOException, CryptoException {

        long userId = sharedPref.getCurrentUserId();

        if (userId == 0) {
            throw new IOException("Can't find saved user");
        }

        LogUtils.debug("Initialization current user id=" + userId);

        Account account = daoSession.getAccountDao().load(userId);

        vulcan.setCredentials(account.getEmail(),
                Scrambler.decrypt(account.getEmail(), account.getPassword()),
                account.getSymbol(),
                account.getSchoolId(),
                account.getRealId(),
                daoSession.getDiaryDao().queryBuilder()
                        .where(DiaryDao.Properties.IsCurrent.eq(true)).unique().getValue()
        );
    }
}
