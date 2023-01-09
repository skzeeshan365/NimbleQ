package com.reiserx.nimbleq.Repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;

import java.util.ArrayList;
import java.util.Objects;

public class SlotListRepository {
    private final DatabaseReference slotReference;
    private final SlotListRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;

    public SlotListRepository(SlotListRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete) {
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        slotReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("SubjectList").child("subjectForStudents");
    }

    public void getSlotList(Context context) {
        SharedPreferences save = context.getSharedPreferences("subjectSlots", MODE_PRIVATE);
        ArrayList<subjectAndTimeSlot> data = new ArrayList<>();
        slotReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        slotReference.child(Objects.requireNonNull(snapshot1.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data.clear();
                                if (snapshot.exists()) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                                        if (subjectAndTimeSlot != null) {
                                            if (subjectAndTimeSlot.getSubject().equals(save.getString("subject", null)))
                                                data.add(subjectAndTimeSlot);
                                        }
                                    }
                                    onRealtimeDbTaskComplete.onSuccess(data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else
                    onRealtimeDbTaskComplete.onFailure(context.getString(R.string.slot_not_avail));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnRealtimeDbTaskComplete {
        void onSuccess(ArrayList<subjectAndTimeSlot> slotList);

        void onFailure(String error);
    }
}
