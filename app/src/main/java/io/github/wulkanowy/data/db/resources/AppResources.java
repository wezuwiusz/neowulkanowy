package io.github.wulkanowy.data.db.resources;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.utils.AppConstant;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.security.CryptoException;

@Singleton
public class AppResources implements ResourcesContract {

    private Resources resources;

    @Inject
    AppResources(@ApplicationContext Context context) {
        resources = context.getResources();
    }

    @Override
    public String[] getSymbolsKeysArray() {
        return resources.getStringArray(R.array.symbols);
    }

    @Override
    public String[] getSymbolsValuesArray() {
        return resources.getStringArray(R.array.symbols_values);
    }

    @Override
    public String getErrorLoginMessage(Exception exception) {
        LogUtils.error(AppConstant.APP_NAME + " encountered a error", exception);

        if (exception instanceof CryptoException) {
            return resources.getString(R.string.encrypt_failed_text);
        } else if (exception instanceof UnknownHostException) {
            return resources.getString(R.string.noInternet_text);
        } else if (exception instanceof SocketTimeoutException) {
            return resources.getString(R.string.generic_timeout_error);
        } else if (exception instanceof NotLoggedInErrorException || exception instanceof IOException) {
            return resources.getString(R.string.login_denied_text);
        } else {
            return exception.getMessage();
        }
    }

    @Override
    public String getAttendanceLessonDescription(AttendanceLesson lesson) {
        int id = R.string.attendance_present;

        if (lesson.getIsAbsenceForSchoolReasons()) {
            id = R.string.attendance_absence_for_school_reasons;
        }

        if (lesson.getIsAbsenceExcused()) {
            id =  R.string.attendance_absence_excused;
        }

        if (lesson.getIsAbsenceUnexcused()) {
            id =  R.string.attendance_absence_unexcused;
        }

        if (lesson.getIsExemption()) {
            id =  R.string.attendance_exemption;
        }

        if (lesson.getIsExcusedLateness()) {
            id =  R.string.attendance_excused_lateness;
        }

        if (lesson.getIsUnexcusedLateness()) {
            id =  R.string.attendance_unexcused_lateness;
        }

        return resources.getString(id);
    }
}
