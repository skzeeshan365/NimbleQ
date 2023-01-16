package com.reiserx.nimbleq.Repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.remoteFileModel;
import com.reiserx.nimbleq.Utils.fileSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.List;

public class FirebaseStorageRepository {
    private final FirebaseStorageRepository.OnFileUploaded onFileUploaded;

    private final StorageReference reference;

    String displayName;

    public FirebaseStorageRepository(FirebaseStorageRepository.OnFileUploaded onFileUploaded) {
        this.onFileUploaded = onFileUploaded;
        reference = FirebaseStorage.getInstance().getReference().child("Data").child("Main");
    }

    public void uploadMultipleImages(Context context, String userID, List<Uri> list) {
        for (Uri uri : list) {

            if (filePermitted(context, 50, uri)) {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();

                Log.d(CONSTANTS.TAG2, getFileName(context, uri));

                reference.child(userID).child(String.valueOf(currentTime)).putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    reference.child(userID).child(String.valueOf(currentTime)).getDownloadUrl().addOnSuccessListener(uri1 -> {
                        remoteFileModel remoteFileModel = new remoteFileModel(getFileName(context, uri), uri1.toString());
                        onFileUploaded.onSuccess(remoteFileModel);
                        Log.d(CONSTANTS.TAG2, remoteFileModel.getFilename());
                    }).addOnFailureListener(e -> onFileUploaded.onFailure(e.toString()));
                }).addOnFailureListener(e -> {
                    onFileUploaded.onFailure(e.toString());
                });
            } else
                onFileUploaded.onFailure("Failed to upload file ".concat(getFileName(context, uri)));
        }
    }

    @SuppressLint("Range")
    public String getFileName(Context context, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = String.valueOf(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            } finally {
                cursor.close();
            }
        } else if (uri.toString().startsWith("file://")) {
            File myFile = new File(uri.toString());
            displayName = myFile.getName();
        }
        return displayName;
    }

    private boolean filePermitted(Context context, int max, Uri uri) {
        fileSize fileSize = new fileSize();
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        long fileSizes = 0;
        if (fileDescriptor != null) {
            fileSizes = fileDescriptor.getLength();
        } else
            return false;
        String calFileSize = fileSize.getFileSize(fileSizes);
        String number = calFileSize.replaceAll("[^0-9]", "");

        Log.d(CONSTANTS.TAG2, calFileSize);
        Log.d(CONSTANTS.TAG2, String.valueOf(fileSizes));
        if (calFileSize.contains("MB"))
            return Integer.parseInt(number) < max;
        else return calFileSize.contains("KB");
    }

    public interface OnFileUploaded {
        void onSuccess(remoteFileModel remoteFileModel);

        void onFailure(String error);
    }
}
