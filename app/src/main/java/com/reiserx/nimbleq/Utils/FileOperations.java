package com.reiserx.nimbleq.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.reiserx.nimbleq.BuildConfig;

import java.io.File;
import java.util.Objects;

public class FileOperations {
    Context context;

    public FileOperations(Context context) {
        this.context = context;
    }

    public void checkFile(String url, String filename, boolean downloadWithoutProg) {
        if (downloadWithoutProg) {
            File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Padhai Madad/".concat(filename));
            if (filePath.exists()) {
                openFile(context, filePath.getPath());
            } else {
                FileDownloader fileDownloader = new FileDownloader(context);
                fileDownloader.downloadWithoutProgress(url, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Padhai Madad", filename);
            }
        }
    }

    private void openFile(Context context, String filePath) {
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file);
        String mime = context.getContentResolver().getType(uri);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
