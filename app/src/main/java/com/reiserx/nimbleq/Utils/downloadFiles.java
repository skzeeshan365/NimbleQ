package com.reiserx.nimbleq.Utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class downloadFiles {
    File outputFile;
    String Path, UserID, dbPath;


    public downloadFiles(String Path, String dbPath, String UserID) {
        this.Path = Path;
        this.UserID = UserID;
        this.dbPath = dbPath;
        String path = Environment.getExternalStorageDirectory().toString() + "/".concat(Path);
        outputFile = new File(path);
    }

    public void download(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
                try {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                } catch(FileNotFoundException e) {
                    return; // swallow a 404
            } catch (IOException e) {
                    e.printStackTrace();
                }
            handler.post(() -> {
                if (outputFile.exists()) {

                }
            });
        });
    }
}