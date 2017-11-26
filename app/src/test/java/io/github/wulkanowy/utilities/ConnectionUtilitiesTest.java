package io.github.wulkanowy.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionUtilitiesTest {

    @Test
    public void isOnlineTrueTest() {
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);

        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);

        Context context = mock(Context.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);

        Assert.assertTrue(ConnectionUtilities.isOnline(context));
    }

    @Test
    public void isOnlineFalseTest() {
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);

        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);

        Context context = mock(Context.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);

        Assert.assertFalse(ConnectionUtilities.isOnline(context));
    }
}
