package io.github.wulkanowy.ui.main.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;
import io.github.wulkanowy.utils.AppConstant;

public class AboutScreen extends SettingsFragment {

    public static final String SHARED_KEY_ABOUT_VERSION = "about_version";

    public static final String SHARED_KEY_ABOUT_LICENSES = "about_osl";

    public static final String SHARED_KEY_ABOUT_REPO = "about_repo";

    private Preference.OnPreferenceClickListener onProgrammerListener = new Preference.OnPreferenceClickListener() {
        private int clicks = 0;

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Toast.makeText(getActivity(), getVersionToast(clicks++), Toast.LENGTH_SHORT).show();
            return true;
        }

        private int getVersionToast(int click) {
            if (0 == click) {
                return R.string.about_programmer_step1;
            }

            if (1 == click) {
                return R.string.about_programmer_step2;
            }

            if (9 > click) {
                return R.string.about_programmer_step3;
            }

            return R.string.about_programmer;
        }
    };

    public AboutScreen() {
        // silence is golden
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findPreference(SHARED_KEY_ABOUT_VERSION).setSummary(BuildConfig.VERSION_NAME);
        findPreference(SHARED_KEY_ABOUT_VERSION).setOnPreferenceClickListener(onProgrammerListener);
        findPreference(SHARED_KEY_ABOUT_REPO).setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstant.REPO_URL)));
        findPreference(SHARED_KEY_ABOUT_LICENSES).setIntent(new Intent(getActivity(), OssLicensesMenuActivity.class)
                .putExtra("title", getString(R.string.pref_about_osl)));
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
