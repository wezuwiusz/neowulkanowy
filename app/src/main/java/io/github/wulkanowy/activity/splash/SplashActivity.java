package io.github.wulkanowy.activity.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView versionName = findViewById(R.id.rawText);
        versionName.setText(getText(R.string.version_text) + BuildConfig.VERSION_NAME);

        new LoadingTask(this).execute();
    }
}
