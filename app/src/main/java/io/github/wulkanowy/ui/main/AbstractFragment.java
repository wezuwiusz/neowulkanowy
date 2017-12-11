package io.github.wulkanowy.ui.main;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.ui.WulkanowyApp;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public abstract class AbstractFragment<T extends AbstractExpandableHeaderItem> extends Fragment
        implements AsyncResponse<T> {

    private FlexibleAdapter<T> flexibleAdapter;

    private List<T> itemList = new ArrayList<>();

    private WeakReference<Activity> activityWeakReference;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerViewLayout;

    private DaoSession daoSession;

    private long userId;

    public AbstractFragment() {
        //empty constructor for fragments
    }

    public long getUserId() {
        return userId;
    }

    public SwipeRefreshLayout getRefreshLayoutView() {
        return swipeRefreshLayout;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Activity getActivityWeakReference() {
        return activityWeakReference.get();
    }

    public abstract int getLayoutId();

    public abstract int getRecyclerViewId();

    public abstract int getLoadingBarId();

    public abstract int getRefreshLayoutId();

    public abstract List<T> getItems() throws Exception;

    public abstract void onRefresh() throws Exception;

    public abstract void onPostRefresh(int stringResult);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        recyclerViewLayout = view.findViewById(getRecyclerViewId());
        swipeRefreshLayout = view.findViewById(getRefreshLayoutId());
        setUpRefreshLayout(swipeRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null && getView() != null) {
            activityWeakReference = new WeakReference<Activity>(getActivity());
            daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
            userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    .getLong("userId", 0);

            if (itemList != null)
                if (itemList.isEmpty()) {
                    flexibleAdapter = getFlexibleAdapter(itemList);
                    setAdapterOnRecyclerView(recyclerViewLayout);
                    if (getUserVisibleHint()) {
                        new DatabaseQueryTask(this).execute();
                    }
                } else {
                    setAdapterOnRecyclerView(recyclerViewLayout);
                    setLoadingBarInvisible(getView());
                }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && isVisibleToUser && flexibleAdapter.getItemCount() == 0) {
            new DatabaseQueryTask(this).execute();
        }
    }

    @Override
    public void onQuarryProcessFinish(@NonNull List<T> resultItemList) {
        itemList = resultItemList;
        flexibleAdapter = getFlexibleAdapter(itemList);
        setAdapterOnRecyclerView(recyclerViewLayout);
        if (getView() != null) {
            setLoadingBarInvisible(getView());
        }
    }

    @Override
    public void onRefreshProcessFinish(@Nullable List<T> resultItemList, int stringEventId) {
        if (resultItemList != null) {
            itemList = resultItemList;
            updateDataInRecyclerView();
        }
        onPostRefresh(stringEventId);
        getRefreshLayoutView().setRefreshing(false);
    }

    @NonNull
    protected FlexibleAdapter<T> getFlexibleAdapter(@NonNull List<T> itemList) {
        return new FlexibleAdapter<>(itemList)
                .setAutoCollapseOnExpand(true)
                .setAutoScrollOnExpand(true)
                .expandItemsAtStartUp();
    }

    @NonNull
    private SwipeRefreshLayout.OnRefreshListener getDefaultRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionUtilities.isOnline(getContext())) {
                    new RefreshTask(AbstractFragment.this).execute();
                } else {
                    Toast.makeText(getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

    private void updateDataInRecyclerView() {
        flexibleAdapter.updateDataSet(itemList);
        setAdapterOnRecyclerView(recyclerViewLayout);
    }

    protected void setUpRefreshLayout(@NonNull SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        swipeRefreshLayout.setOnRefreshListener(getDefaultRefreshListener());
    }

    protected final void setLoadingBarInvisible(@NonNull View mainView) {
        mainView.findViewById(getLoadingBarId()).setVisibility(View.INVISIBLE);
    }

    protected void setAdapterOnRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getActivityWeakReference()));
        recyclerView.setAdapter(flexibleAdapter);
    }
}
