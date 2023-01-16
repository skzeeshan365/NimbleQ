package com.reiserx.nimbleq.Repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.Announcements.announcementsModel;
import com.reiserx.nimbleq.Models.Announcements.linkModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnnouncementsRepository {
    private final DatabaseReference databaseReference;
    private final OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;

    public AnnouncementsRepository(OnRealtimeDbTaskComplete onRealtimeDbTaskComplete) {
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("Announcements");
    }

    public void getAllData(String classID) {
        databaseReference.child(classID).orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<announcementsModel> parentItemList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        announcementsModel announcementsModel = new announcementsModel();
                        announcementsModel.setName(ds.child("name").getValue(String.class));
                        announcementsModel.setInfo(ds.child("info").getValue(String.class));
                        try {
                            announcementsModel.setTimeStamp(ds.child("timeStamp").getValue(long.class));
                        } catch (Exception e) {
                            onRealtimeDbTaskComplete.onFailure("failed to get timestamp");
                        }


                        GenericTypeIndicator<Map<String, linkModel>> mapType = new GenericTypeIndicator<Map<String, linkModel>>() {
                        };
                        Map<String, linkModel> namelist = ds.child("AnnouncementLinks").getValue(mapType);

                        if (namelist != null) {
                            List<linkModel> list = new ArrayList<>(namelist.values());
                            announcementsModel.setLinkModels(list);
                        }
                        parentItemList.add(announcementsModel);
                    }
                    Collections.reverse(parentItemList);
                    onRealtimeDbTaskComplete.onSuccess(parentItemList);
                } else onRealtimeDbTaskComplete.onFailure("Announcements not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error.toString());
            }
        });
    }

    public interface OnRealtimeDbTaskComplete {
        void onSuccess(List<announcementsModel> parentItemList);

        void onFailure(String error);
    }
}
