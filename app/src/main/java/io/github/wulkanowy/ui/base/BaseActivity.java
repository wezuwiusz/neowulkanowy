package io.github.wulkanowy.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;
import io.github.wulkanowy.R;
import io.github.wulkanowy.utils.NetworkUtils;

public abstract class BaseActivity extends DaggerAppCompatActivity implements BaseContract.View {

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected void injectViews() {
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void showMessage(@NonNull String text) {
        if (getMessageView() != null) {
            Snackbar.make(getMessageView(), text, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showNoNetworkMessage() {
        showMessage(getString(R.string.noInternet_text));
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isOnline(getApplicationContext());
    }

    protected View getMessageView() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        invalidateOptionsMenu();
    }
}
