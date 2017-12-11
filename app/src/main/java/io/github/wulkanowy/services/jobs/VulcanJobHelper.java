package io.github.wulkanowy.services.jobs;


import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

public abstract class VulcanJobHelper<T extends VulcanJobHelper> {

    public static final String DEBUG_TAG = "SynchronizationService";

    @SuppressWarnings("unchecked")
    public final T scheduledJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.mustSchedule(createJob(dispatcher));
        Log.i(DEBUG_TAG, "Wulkanowy Job is initiation: " + this.toString());
        return (T) this;
    }

    protected abstract Job createJob(FirebaseJobDispatcher dispatcher);
}
