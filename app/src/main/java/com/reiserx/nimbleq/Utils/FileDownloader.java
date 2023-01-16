package com.reiserx.nimbleq.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.reiserx.nimbleq.BuildConfig;
import com.reiserx.nimbleq.Constants.CONSTANTS;

import java.io.File;
import java.util.Objects;

public class FileDownloader {
    Context context;
    ProgressDialog prog;

    public FileDownloader(Context context) {
        this.context = context;
        prog = new ProgressDialog(context);
        prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public void download(String url, String path, String fileName) {
        Log.d(CONSTANTS.TAG2, fileName);
        prog.setTitle(fileName);
        PRDownloader.download(url, path, fileName).build()
                .setOnStartOrResumeListener(() -> {
                    prog.show();
                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {

                })
                .setOnProgressListener(progress -> {
                    long progressPer = progress.currentBytes * 100 / progress.totalBytes;
                    prog.setProgress((int) progressPer);
                    prog.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        prog.dismiss();
                        openFile(context, path.concat("/".concat(fileName)));
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(CONSTANTS.TAG2, error.toString());
                    }
                });
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
