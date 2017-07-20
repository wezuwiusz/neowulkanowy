package io.github.wulkanowy.activity.started;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.wulkanowy.R;

public class StartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);
        Task();
    }

    public void Task(){

        new LoadingTask(this).execute();
    }
}
