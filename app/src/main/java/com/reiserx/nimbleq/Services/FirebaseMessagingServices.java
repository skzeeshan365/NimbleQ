package com.reiserx.nimbleq.Services;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reiserx.nimbleq.Utils.NotificationUtils;
import com.reiserx.nimbleq.Utils.Notify;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseMessagingServices extends FirebaseMessagingService {

    String TAG = "FCMMessage.logs";

    NotificationUtils notificationUtils;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
         auth = FirebaseAuth.getInstance();
         user = auth.getCurrentUser();
        if (user != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("FCM_TOKEN", s);
            FirebaseDatabase.getInstance().getReference().child("Data").child("UserData").child(user.getUid()).updateChildren(map);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String content = data.get("content");
        String id = data.get("id");

        notificationUtils = new NotificationUtils();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (Boolean.parseBoolean(data.get("isTopic"))) {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");
            Query query = databaseReference.orderByChild(user.getUid()).equalTo(user.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.exists()) {
                                if (remoteMessage.getFrom().equals("/topics/".concat(snapshot1.getKey()))) {
                                    if (Integer.parseInt(data.get("requestCode")) == Notify.TOPIC_ANNOUNCEMENT_UPDATE_NOTIFICATION)
                                        notificationUtils.sendClassUpdates(FirebaseMessagingServices.this, "Class announcement", title, content, Integer.parseInt(Objects.requireNonNull(id)), snapshot1.getKey());
                                }
                            }
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            if (Integer.parseInt(data.get("requestCode")) == Notify.NORMAL_SMALL_TEXT_NOTIFICATION) {
                notificationUtils.smallTextNotification(FirebaseMessagingServices.this, title, content, Integer.parseInt(id));
            } else if (Integer.parseInt(data.get("requestCode")) == Notify.NORMAL_ANSWER_UPDATE_NOTIFICATION) {
                notificationUtils.sendAnswerUpdates(FirebaseMessagingServices.this, title, content, Integer.parseInt(id), data.get("payload"));
            } else if (Integer.parseInt(data.get("requestCode")) == Notify.NORMAL_BIG_TEXT_NOTIFICATION) {
                notificationUtils.bigTextNotification(FirebaseMessagingServices.this, title, content, Integer.parseInt(id), data.get("payload"));
            }
        }
    }
}
