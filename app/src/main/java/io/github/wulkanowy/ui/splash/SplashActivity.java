package io.github.wulkanowy.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;
import io.github.wulkanowy.services.jobs.FullSyncJob;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.main.DashboardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView versionName = findViewById(R.id.rawText);
        versionName.setText(getString(R.string.version_text, BuildConfig.VERSION_NAME));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                executeOnRunApp();
            }
        }, 500);
    }

    private void executeOnRunApp() {
        if (getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0) == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            new FullSyncJob().scheduledJob(getApplicationContext());

            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }
}
