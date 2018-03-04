package io.github.wulkanowy.utils.async;

public interface AsyncListeners {

    interface OnRefreshListener {

        void onDoInBackgroundRefresh() throws Exception;

        void onCanceledRefreshAsync();

        void onEndRefreshAsync(boolean result, Exception exception);

    }

    interface OnFirstLoadingListener {

        void onDoInBackgroundLoading() throws Exception;

        void onCanceledLoadingAsync();

        void onEndLoadingAsync(boolean result, Exception exception);
    }
}
