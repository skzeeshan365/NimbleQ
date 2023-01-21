package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.mimeTypesModel;

import java.util.ArrayList;
import java.util.List;

public class AdministrationRepository {
    private final AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted;
    private final AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete;

    DatabaseReference reference;

    public AdministrationRepository(AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted, AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete) {
        this.onGetMimetypesCompleted = onGetMimetypesCompleted;
        this.onGetFileEnabledComplete = onGetFileEnabledComplete;
        reference = FirebaseDatabase.getInstance().getReference().child("Data").child("Administration");
    }

    public void getMimeTypesForGroupChats() {
        List<String> mimeTypes = new ArrayList<>();
        reference.child("Filetypes").child("GroupChats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot mimetype : snapshot.getChildren()) {
                        mimeTypesModel value = mimetype.getValue(mimeTypesModel.class);
                        if (value != null)
                            mimeTypes.add(value.getMimetype());
                    }
                    onGetMimetypesCompleted.onSuccess(mimeTypes);
                } else onGetMimetypesCompleted.onFailure("MimeTypes not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetMimetypesCompleted.onFailure(error.toString());
            }
        });
    }

    public void getFilesEnabled() {
        reference.child("Filetypes").child("ImagesOnly").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                    onGetFileEnabledComplete.onSuccess(value);
                } else onGetFileEnabledComplete.onFailure("MimeTypes not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetFileEnabledComplete.onFailure(error.toString());
            }
        });
    }

    public interface OnGetMimetypesCompleted {
        void onSuccess(List<String> mimetypes);

        void onFailure(String error);
    }

    public interface OnGetFileEnabledComplete {
        void onSuccess(Boolean enabled);

        void onFailure(String error);
    }
}
