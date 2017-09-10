package io.github.wulkanowy.services.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.database.accounts.Account;
import io.github.wulkanowy.database.accounts.AccountsDatabase;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.jobs.VulcanSync;

public class VulcanSynchronisation {

    private StudentAndParent studentAndParent;

    public void loginCurrentUser(Context context) throws CryptoException, BadCredentialsException, LoginErrorException, AccountPermissionException, IOException {

        long userId = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("isLogin", 0);

        if (userId != 0) {
            AccountsDatabase accountsDatabase = new AccountsDatabase(context);
            accountsDatabase.open();
            Account account = accountsDatabase.getAccount(userId);
            accountsDatabase.close();
            Safety safety = new Safety(context);

            Login login = loginUser(
                    account.getEmail(),
                    safety.decrypt(account.getEmail(), account.getPassword()),
                    account.getSymbol());

            getAndSetStudentAndParentFromApi(account.getSymbol(), login.getCookies());
        } else {
            Log.wtf(VulcanSync.DEBUG_TAG, "loginCurrentUser - USERID IS EMPTY");
        }
    }

    public void loginNewUser(String email, String password, String symbol, Context context) throws BadCredentialsException, LoginErrorException, AccountPermissionException, IOException, CryptoException {

        Login login = loginUser(email, password, symbol);

        Safety safety = new Safety(context);
        AccountsDatabase accountsDatabase = new AccountsDatabase(context);
        BasicInformation basicInformation = new BasicInformation(getAndSetStudentAndParentFromApi(symbol, login.getCookies()));
        PersonalData personalData = basicInformation.getPersonalData();

        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password))
                .setSymbol(symbol);

        accountsDatabase.open();
        long idNewUser = accountsDatabase.put(account);
        accountsDatabase.close();

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("isLogin", idNewUser);
        editor.apply();
    }

    public StudentAndParent getStudentAndParent() {
        return studentAndParent;
    }

    private void setStudentAndParent(StudentAndParent studentAndParent) {
        this.studentAndParent = studentAndParent;
    }

    private Login loginUser(String email, String password, String symbol) throws BadCredentialsException, LoginErrorException, AccountPermissionException {

        Cookies cookies = new Cookies();
        Login login = new Login(cookies);
        login.login(email, password, symbol);
        return login;

    }

    private StudentAndParent getAndSetStudentAndParentFromApi(String symbol, Map<String, String> cookiesMap) throws IOException, LoginErrorException {

        if (studentAndParent == null) {
            Cookies cookies = new Cookies();
            cookies.setItems(cookiesMap);

            StudentAndParent snp = new StudentAndParent(cookies, symbol);

            setStudentAndParent(snp);
            return snp;
        } else {
            return studentAndParent;
        }
    }
}
