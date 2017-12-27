package io.github.wulkanowy.services;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import java.io.File;

import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;

public class Updater {

    private static final String DEBUG_TAG = "WulkanowyUpdater";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private Activity activity;

    private Update update;

    private DownloadManager downloadManager;

    private BroadcastReceiver onComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                if (uriString.substring(0, 7).matches("file://")) {
                    uriString = uriString.substring(7);
                }

                File file = new File(uriString);

                Intent install;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setData(FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".fileprovider", file));
                } else {
                    install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()),
                            "application/vnd.android.package-archive");
                }

                context.startActivity(install);
            }
        }
    };

    public Updater(Activity activity) {
        this.activity = activity;

        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void downloadUpdate() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startDownload();
        } else {
            requestWriteStoragePermission();
        }
    }

    private void requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void startDownload() {
        Snackbar.make(activity.findViewById(R.id.fragment_container), "Downloading started.", Snackbar.LENGTH_SHORT).show();

        String path = Environment.getExternalStorageDirectory().toString() + File.separator +
                Environment.DIRECTORY_DOWNLOADS + File.separator + "wulkanowy";

        File dir = new File(path);
        if(!dir.mkdirs()) {
            for (String aChildren : dir.list()) {
                new File(dir, aChildren).delete();
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(update.getUrlToDownload().toString()))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Wulkanowy v" + update.getLatestVersionCode())
                .setDescription(update.getLatestVersion())
                .setVisibleInDownloadsUi(true)
                .setMimeType("application/vnd.android.package-archive")
                .setDestinationUri(Uri.fromFile(new File(path + File.separator + update.getLatestVersion() + ".apk")));

        downloadManager.enqueue(request);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadUpdate();
            } else {
                Snackbar.make(activity.findViewById(R.id.fragment_container),
                        "Write storage permission request was denied.",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    public Updater checkForUpdates() {
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(activity)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(BuildConfig.UPDATE_URL)
                .withListener(new AppUpdaterUtils.UpdateListener() {

                    @Override
                    public void onSuccess(final Update currentUpdate, Boolean isUpdateAvailable) {
                        Log.d(DEBUG_TAG, "Latest Version: " + currentUpdate.getLatestVersion());
                        Log.d(DEBUG_TAG, "Latest Version Code: " + currentUpdate.getLatestVersionCode().toString());
                        Log.d(DEBUG_TAG, "URL: " + currentUpdate.getUrlToDownload().toString());
                        Log.d(DEBUG_TAG, "Is update available?: " + Boolean.toString(isUpdateAvailable));

                        update = currentUpdate;
                        showDialog(isUpdateAvailable);
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.e(DEBUG_TAG, "Something went wrong");
                        Log.e(DEBUG_TAG, error.toString());
                    }
                });
        appUpdaterUtils.start();

        return this;
    }

    private void showDialog(boolean isUpdateAvailable) {
        if (isUpdateAvailable) {
            new AlertDialog.Builder(activity)
                    .setTitle("Update is available")
                    .setMessage("Update to version " + update.getLatestVersionCode().toString() +
                            " is available. Your version is " + BuildConfig.VERSION_CODE + ". Update?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            downloadUpdate();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void onDestroy(Activity activity) {
        activity.unregisterReceiver(onComplete);
    }
}
