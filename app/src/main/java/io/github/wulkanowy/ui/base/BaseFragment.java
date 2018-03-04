package io.github.wulkanowy.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.Unbinder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.di.component.DaggerFragmentComponent;
import io.github.wulkanowy.di.component.FragmentComponent;
import io.github.wulkanowy.di.modules.FragmentModule;

public abstract class BaseFragment extends Fragment implements BaseContract.View {

    private BaseActivity activity;

    private Unbinder unbinder;

    private FragmentComponent fragmentComponent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }

        fragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .applicationComponent(((WulkanowyApp) activity.getApplication()).getApplicationComponent())
                .build();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpOnViewCreated(view);
    }

    @Override
    public void onDetach() {
        activity = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onError(int resId) {
        onError(getString(resId));
    }

    @Override
    public void onError(String message) {
        if (activity != null) {
            activity.onError(message);
        }
    }

    @Override
    public void onNoNetworkError() {
        onError(R.string.noInternet_text);
    }

    @Override
    public boolean isNetworkConnected() {
        return activity != null && activity.isNetworkConnected();
    }

    public void setButterKnife(Unbinder unbinder) {
        this.unbinder = unbinder;
    }

    public void setTitle(String title) {
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    public FragmentComponent getFragmentComponent() {
        return fragmentComponent;
    }


    protected void setUpOnViewCreated(View fragmentView) {
        // do something on view created
    }
}
