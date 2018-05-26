package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import io.github.wulkanowy.utils.NetworkUtils;

public abstract class BaseFragment extends DaggerFragment implements BaseContract.View {

    private Unbinder unbinder;

    protected void injectViews(@NonNull View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    public void setTitle(String title) {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    @Override
    public void showMessage(@NonNull String text) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showMessage(text);
        }
    }

    public void showMessage(@StringRes int stringId) {
        showMessage(getString(stringId));
    }

    @Override
    public void showNoNetworkMessage() {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showNoNetworkMessage();
        }
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isOnline(getContext());
    }
}
