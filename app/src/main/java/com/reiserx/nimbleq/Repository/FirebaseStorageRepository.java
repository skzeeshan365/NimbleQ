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
import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.Models.remoteFileModel;
import com.reiserx.nimbleq.Models.uploadProgressModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.fileSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.List;

public class FirebaseStorageRepository {
    private final FirebaseStorageRepository.OnFileUploaded onFileUploaded;
    private final FirebaseStorageRepository.OnSingleFileUploaded onFileSingleUploaded;

    private final StorageReference reference;

    String displayName;

    public FirebaseStorageRepository(FirebaseStorageRepository.OnFileUploaded onFileUploaded, FirebaseStorageRepository.OnSingleFileUploaded onFileSingleUploaded) {
        this.onFileUploaded = onFileUploaded;
        this.onFileSingleUploaded = onFileSingleUploaded;

        reference = FirebaseStorage.getInstance().getReference().child("Data").child("Main");
    }

    public void uploadMultipleImages(Context context, String userID, List<Uri> list) {
        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
        for (Uri uri : list) {

            if (filePermitted(context, sharedPreferenceClass.getFileSizeLimit(), uri)) {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();

                reference.child(userID).child(String.valueOf(currentTime)).putFile(uri).addOnSuccessListener(taskSnapshot -> reference.child(userID).child(String.valueOf(currentTime)).getDownloadUrl().addOnSuccessListener(uri1 -> {
                    remoteFileModel remoteFileModel = new remoteFileModel(getFileName(context, uri), uri1.toString());
                    onFileUploaded.onSuccess(remoteFileModel);
                    Log.d(CONSTANTS.TAG2, remoteFileModel.getFilename());
                }).addOnFailureListener(e -> onFileUploaded.onFailure(e.toString()))).addOnFailureListener(e -> onFileUploaded.onFailure(e.toString()));
            } else
                onFileUploaded.onFailure(context.getString(R.string.failed_to_upload_file).concat(getFileName(context, uri)));
        }
    }

    public void uploadSingleFile(Context context, String userID, Uri uri) {
        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
        if (filePermitted(context, sharedPreferenceClass.getFileSizeLimit(), uri)) {
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();

            String fileName = getFileName(context, uri);

            fileTypeModel model = new fileTypeModel(fileName, false);
            onFileSingleUploaded.onPreUpload(model);

            Log.d(CONSTANTS.TAG2, fileName);

            reference.child(userID).child(String.valueOf(currentTime)).putFile(uri).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                uploadProgressModel uploadProgressModel = new uploadProgressModel((int) progress, fileName);
                onFileSingleUploaded.onProgress(uploadProgressModel);
            }).addOnSuccessListener(taskSnapshot -> reference.child(userID).child(String.valueOf(currentTime)).getDownloadUrl().addOnSuccessListener(uri1 -> {
                remoteFileModel remoteFileModel = new remoteFileModel(fileName, uri1.toString());
                onFileSingleUploaded.onUploadSuccess(remoteFileModel);
            }).addOnFailureListener(e -> onFileSingleUploaded.onFailure(e.toString()))).addOnFailureListener(e -> onFileSingleUploaded.onFailure(e.toString()));
        } else
            onFileSingleUploaded.onFailure(context.getString(R.string.failed_to_upload_file).concat(getFileName(context, uri)));
    }

    @SuppressLint("Range")
    public String getFileName(Context context, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = String.valueOf(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            }
        } else if (uri.toString().startsWith("file://")) {
            File myFile = new File(uri.toString());
            displayName = myFile.getName();
        }
        return displayName;
    }

    private boolean filePermitted(Context context, Long max, Uri uri) {
        fileSize fileSize = new fileSize();
        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        long fileSizes;
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

    public interface OnSingleFileUploaded {
        void onUploadSuccess(remoteFileModel remoteFileModel);

        void onPreUpload(fileTypeModel fileTypeModel);

        void onProgress(uploadProgressModel uploadProgressModel);

        void onFailure(String error);
    }
}
