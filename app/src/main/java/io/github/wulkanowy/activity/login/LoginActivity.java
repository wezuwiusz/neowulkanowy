package io.github.wulkanowy.activity.login;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedHashMap;

import io.github.wulkanowy.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private float touchPosition;

    private EditText emailView;

    private EditText passwordView;

    private AutoCompleteTextView symbolView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        symbolView = findViewById(R.id.symbol);

        passwordView.setOnEditorActionListener(getTextViewSignInListener());
        symbolView.setOnEditorActionListener(getTextViewSignInListener());

        populateAutoComplete();

        Button signInButton = findViewById(R.id.action_sign_in);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.action_create_account).setOnClickListener(getButtonLinkListener(
                "https://cufs.vulcan.net.pl/Default/AccountManage/CreateAccount"
        ));

        findViewById(R.id.action_forgot_password).setOnClickListener(getButtonLinkListener(
                "https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount"
        ));
    }

    private TextView.OnEditorActionListener getTextViewSignInListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        };
    }

    private OnClickListener getButtonLinkListener(final String url) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        };
    }

    private void populateAutoComplete() {
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.symbols);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                countries);
        symbolView.setAdapter(adapter);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     */
    private void attemptLogin() {
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String symbol = symbolView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        // Check for a valid symbol.
        if (TextUtils.isEmpty(symbol)) {
            symbol = "Default";
        }

        String[] keys = getResources().getStringArray(R.array.symbols);
        String[] values = getResources().getStringArray(R.array.symbols_values);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if (map.containsKey(symbol)) {
            symbol = map.get(symbol);
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner and kick off a background task to
            // perform the user login attempt.
            LoginTask authTask = new LoginTask(this, email, password, symbol);
            authTask.showProgress(true);
            authTask.execute();
            hideSoftKeyboard();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    private void hideSoftKeyboard() {
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(getWindow()
                    .getDecorView().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touchPosition = ev.getY();
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            float releasePosition = ev.getY();

            if (touchPosition - releasePosition == 0) {
                View view = getCurrentFocus();
                if (view != null && (ev.getAction() == MotionEvent.ACTION_UP
                        || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText
                        && !view.getClass().getName().startsWith("android.webkit.")) {

                    int[] coordinators = new int[2];
                    view.getLocationOnScreen(coordinators);
                    float x = ev.getRawX() + view.getLeft() - coordinators[0];
                    float y = ev.getRawY() + view.getTop() - coordinators[1];
                    if (x < view.getLeft() || x > view.getRight() || y < view.getTop()
                            || y > view.getBottom()) {
                        hideSoftKeyboard();
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
