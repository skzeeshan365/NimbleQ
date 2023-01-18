package com.reiserx.nimbleq.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reiserx.nimbleq.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notify {

    Context context;
    JSONObject json;
    public Notify(Context context) {
        this.context = context;
        json = new JSONObject();
    }

    String TAG = "jkfhsb";

    public void announcementsPayload(String title, String msg, String to, int i) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", i);
            dataJson.put("isTopic", true);
            json.put("data", dataJson);
            json.put("to", "/topics/".concat(to));

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void postNotification() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .header("Authorization", context.getString(R.string.serverKey))
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String res = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, res);

            } catch (Exception e) {
                Log.d(TAG, e.toString());
                Log.d(TAG, e.getMessage());
            }
            handler.post(() -> {
        });
    });
}

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public void classJoinPayload(String name, String msg, String to, int i) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("title", name);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", 1);
            dataJson.put("isTopic", false);
            json.put("data", dataJson);
            json.put("to", to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
