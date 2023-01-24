package com.reiserx.nimbleq.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
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
    SharedPreferenceClass sharedPreferenceClass;

    public static int NORMAL_SMALL_TEXT_NOTIFICATION = 1;
    public static int NORMAL_BIG_TEXT_NOTIFICATION = 3;
    public static int NORMAL_ANSWER_UPDATE_NOTIFICATION = 2;
    public static int NORMAL_CLASS_REQUEST_NOTIFICATION = 4;
    public static int NORMAL_CREATE_CLASS_NOTIFICATION = 5;

    public static int TOPIC_ANNOUNCEMENT_UPDATE_NOTIFICATION = 1;
    public static int TOPIC_CREATE_CLASS_NOTIFICATION = 2;

    public Notify(Context context) {
        this.context = context;
        json = new JSONObject();
        sharedPreferenceClass = new SharedPreferenceClass(context);
    }

    String TAG = "jkfhsb";

    public void announcementsPayload(String title, String msg, String to) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", TOPIC_ANNOUNCEMENT_UPDATE_NOTIFICATION);
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
                        .header("Authorization", sharedPreferenceClass.getFCMKey().getKEY())
                        .url(context.getString(R.string.fcm))
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String res = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, res);
                json = null;
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

    public void classJoinPayload(String name, String msg, String to) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", name);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", NORMAL_SMALL_TEXT_NOTIFICATION);
            dataJson.put("isTopic", false);
            json.put("data", dataJson);
            json.put("to", to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void submitAnswerPayload(String title, String msg, String to, DoubtsModel doubtsModel) {
        JSONObject dataJson = new JSONObject();
        try {
            Gson gson = new Gson();
            String payload = gson.toJson(doubtsModel);

            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", NORMAL_ANSWER_UPDATE_NOTIFICATION);
            dataJson.put("isTopic", false);
            dataJson.put("payload", payload);
            json.put("data", dataJson);
            json.put("to", to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void classReviewPayload(String title, String msg, String to) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", NORMAL_BIG_TEXT_NOTIFICATION);
            dataJson.put("isTopic", false);
            json.put("data", dataJson);
            json.put("to", to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createClassPayload(String title, String msg, String to, String classID) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", TOPIC_CREATE_CLASS_NOTIFICATION);
            dataJson.put("isTopic", true);
            dataJson.put("classID", classID);
            json.put("data", dataJson);
            json.put("to", "/topics/".concat(to));

            Log.d(CONSTANTS.TAG2, to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createClassPayloadForSingleUser(String title, String msg, String to, String classID) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", NORMAL_CREATE_CLASS_NOTIFICATION);
            dataJson.put("isTopic", false);
            dataJson.put("classID", classID);
            json.put("data", dataJson);
            json.put("to", to);

            Log.d(CONSTANTS.TAG2, to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void classRequestPayload(String title, String msg, String to) {
        JSONObject dataJson = new JSONObject();
        try {
            json = new JSONObject();
            dataJson.put("title", title);
            dataJson.put("content", msg);
            dataJson.put("id", String.valueOf(getRandom(0, 100)));
            dataJson.put("requestCode", NORMAL_CLASS_REQUEST_NOTIFICATION);
            dataJson.put("isTopic", false);
            json.put("data", dataJson);
            json.put("to", to);

            postNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
