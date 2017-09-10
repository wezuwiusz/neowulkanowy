package io.github.wulkanowy.services.jobs;


import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

public abstract class VulcanSync {

    public static final String DEBUG_TAG = "SynchronizationService";

    public void scheduledJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.mustSchedule(createJob(dispatcher));
    }

    protected abstract Job createJob(FirebaseJobDispatcher dispatcher);

}
