package io.github.wulkanowy.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.utils.AppConstant;
import io.github.wulkanowy.utils.CommonUtils;
import io.github.wulkanowy.utils.KeyboardUtils;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.login_activity_email_edit)
    EditText emailView;

    @BindView(R.id.login_activity_pass_edit)
    EditText passwordView;

    @BindView(R.id.login_activity_symbol_edit)
    AutoCompleteTextView symbolView;

    @BindView(R.id.login_activity_form_scroll)
    View loginFormView;

    @BindView(R.id.login_activity_progress_container)
    View loadingBarView;

    @BindView(R.id.login_activity_progress_text)
    TextView loginProgressText;

    @BindView(R.id.login_activity_symbol_text_input)
    TextInputLayout symbolLayout;

    @Inject
    LoginContract.Presenter presenter;

    private EditText requestedView;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        injectViews();

        presenter.attachView(this);
        setUpOnCreate();
    }

    protected void setUpOnCreate() {
        symbolView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.symbols)));
    }

    @OnClick(R.id.login_activity_sign_button)
    void onLoginButtonClick() {
        presenter.attemptLogin(
                emailView.getText().toString(),
                passwordView.getText().toString(),
                symbolView.getText().toString());
    }

    @OnEditorAction(value = {R.id.login_activity_symbol_edit, R.id.login_activity_pass_edit})
    boolean onEditorAction(int id) {
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
            onLoginButtonClick();
            return true;
        }
        return false;
    }

    @OnClick(R.id.login_activity_create_text)
    void onCreateAccountButtonClick() {
        CommonUtils.openInternalBrowserViewer(this, AppConstant.VULCAN_CREATE_ACCOUNT_URL);
    }

    @OnClick(R.id.login_activity_forgot_text)
    void onForgotPasswordButtonClick() {
        CommonUtils.openInternalBrowserViewer(this, AppConstant.VULCAN_FORGOT_PASS_URL);
    }

    @Override
    public void setErrorEmailRequired() {
        emailView.requestFocus();
        emailView.setError(getString(R.string.error_field_required));
        requestedView = emailView;
    }

    @Override
    public void setErrorEmailInvalid() {
        emailView.requestFocus();
        emailView.setError(getString(R.string.error_invalid_email));
        requestedView = emailView;
    }

    @Override
    public void setErrorPassRequired() {
        passwordView.requestFocus();
        passwordView.setError(getString(R.string.error_field_required));
        requestedView = passwordView;
    }

    @Override
    public void setErrorPassInvalid() {
        passwordView.requestFocus();
        passwordView.setError(getString(R.string.error_invalid_password));
        requestedView = passwordView;
    }

    @Override
    public void setErrorPassIncorrect() {
        passwordView.requestFocus();
        passwordView.setError(getString(R.string.error_incorrect_password));
        requestedView = passwordView;
    }

    @Override
    public void setErrorSymbolRequired() {
        symbolLayout.setVisibility(View.VISIBLE);
        symbolView.setError(getString(R.string.error_bad_account_permission));
        symbolView.requestFocus();
        requestedView = symbolView;
    }

    @Override
    public void resetViewErrors() {
        emailView.setError(null);
        passwordView.setError(null);
    }

    @Override
    public void showSoftInput() {
        KeyboardUtils.showSoftInput(requestedView, this);
    }

    @Override
    public void hideSoftInput() {
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    public void setStepOneLoginProgress() {
        onLoginProgressUpdate("1", getString(R.string.step_login));
    }

    @Override
    public void setStepTwoLoginProgress() {
        onLoginProgressUpdate("2", getString(R.string.step_synchronization));
    }

    @Override
    public void openMainActivity() {
        startActivity(MainActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void showLoginProgress(final boolean show) {
        int animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(animTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        loadingBarView.setVisibility(show ? View.VISIBLE : View.GONE);
        loadingBarView.animate().setDuration(animTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadingBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void showActionBar(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (show) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    @NonNull
    @Override
    protected View getMessageView() {
        return findViewById(R.id.login_activity_container);
    }


    @Override
    public void onSyncFailed() {
        Toast.makeText(getApplicationContext(), R.string.login_sync_error, Toast.LENGTH_LONG).show();
    }

    private void onLoginProgressUpdate(String step, String message) {
        loginProgressText.setText(String.format("%1$s/2 - %2$s...", step, message));
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
