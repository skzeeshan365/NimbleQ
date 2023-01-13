package com.reiserx.nimbleq.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.nimbleq.Adapters.fileListAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Announcements.announcementsModel;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.FileUtil;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.databinding.ActivityAnnouncementsBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AnnouncementsActivity extends AppCompatActivity {

    ActivityAnnouncementsBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference reference;
    DatabaseReference db_reference;

    ArrayList<linkModel> links;

    int type = 0;

    String[] dataTypes;

    boolean isUploaded;

    ArrayList<fileTypeModel> data;
    fileListAdapter adapter;

    LinearLayoutManager layoutManager;

    Uri importedFile;

    SnackbarTop snackbarTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String classID = getIntent().getExtras().getString("id");

        ButtonDesign buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button9);

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dataTypes = new String[]{"Select File", "Image", "PDF", "Audio"};

        data = new ArrayList<>();
        links = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.recyclerView2.setLayoutManager(layoutManager);
        adapter = new fileListAdapter(this, data, adapter);
        binding.recyclerView2.setAdapter(adapter);

        binding.attachHolder.setVisibility(View.GONE);

        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataTypes);
        gradesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(gradesAdapter);
        binding.spinner.setOnItemSelectedListener(new dataTypeClass());

        binding.button9.setOnClickListener(view -> {
            db_reference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("Announcements").child(classID);
            buttonDesign.buttonFill(binding.button9);

            if (binding.titile.getText().toString().trim().equals(""))
                binding.titile.setError("Field required");
            else if (binding.message.getText().toString().trim().equals(""))
                binding.message.setError("Field required");
            else if (links.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();

                announcementsModel announcementsModel = new announcementsModel(binding.titile.getText().toString(), binding.message.getText().toString(), classID, currentTime);

                db_reference.push().setValue(announcementsModel).addOnSuccessListener(unused -> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AnnouncementsActivity.this);
                    alert.setTitle("Success");
                    alert.setMessage("Announce has been uploaded");
                    alert.setPositiveButton("close", (dialogInterface, i) -> finish());
                    alert.setCancelable(false);
                    alert.show();
                });
            } else {
                for (int i = 0; i < data.size(); i++) {
                    fileTypeModel fileTypeModel = data.get(i);
                    if (!fileTypeModel.isUploaded()) {
                        isUploaded = false;
                        break;
                    } else isUploaded = true;
                }
                if (isUploaded) {

                    Calendar cal = Calendar.getInstance();
                    long currentTime = cal.getTimeInMillis();

                    announcementsModel announcementsModel = new announcementsModel(binding.titile.getText().toString(), binding.message.getText().toString(), classID, currentTime);
                    String key = db_reference.push().getKey();
                    db_reference.child(key).setValue(announcementsModel).addOnSuccessListener(unused -> {
                        for (int i = 0; i < links.size(); i++) {
                            linkModel linkModel = links.get(i);
                            linkModel.setId(key);
                            FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("Announcements").child(classID).child(key).child("AnnouncementLinks").push().setValue(linkModel);
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(AnnouncementsActivity.this);
                        alert.setTitle("Success");
                        alert.setMessage("Announce has been uploaded");
                        alert.setPositiveButton("close", (dialogInterface, i) -> finish());
                        alert.setCancelable(false);
                        alert.show();
                    });
                } else snackbarTop.showSnackBar("Files not uploaded yet", false);
            }
        });
    }

    private class dataTypeClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i < 1)
                binding.attachHolder.setVisibility(View.GONE);
            else {
                binding.attachHolder.setVisibility(View.VISIBLE);
                binding.attachHolder.setOnClickListener(view1 -> {
                    if (i == 1) {
                        Intent photoPic = new Intent(Intent.ACTION_PICK);
                        photoPic.setType("image/*");
                        startActivityForResult(photoPic, 12);
                        type = 1;
                    } else if (i == 2) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent, 13);
                    } else if (i == 3) {
                        Intent photoPic = new Intent(Intent.ACTION_GET_CONTENT);
                        photoPic.setType("audio/*");
                        startActivityForResult(photoPic, 12);
                        type = 2;
                    }
                });
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent datas) {
        super.onActivityResult(requestCode, resultCode, datas);

        switch (requestCode) {
            case 12:
                if (data != null) {
                    if (datas.getData() != null) {
                        ArrayList<String> filepath = new ArrayList<>();
                        importedFile = datas.getData();
                        filepath.add(FileUtil.convertUriToFilePath(this, importedFile));
                        String name = String.valueOf(data.size()).concat(Uri.parse(filepath.get(0)).getLastPathSegment());
                        fileTypeModel model = new fileTypeModel(name, false);
                        model.setFilePath(FileUtil.convertUriToFilePath(this, importedFile));
                        data.add(model);
                        adapter.notifyDataSetChanged();
                        binding.attachHolder.setVisibility(View.GONE);
                        binding.spinner.setSelection(0);

                        if (type == 1) {
                            uploadFiles(CONSTANTS.fileType_image, importedFile, name);
                            type = 0;
                        } else if (type == 2) {
                            uploadFiles(CONSTANTS.fileType_audio, importedFile, name);
                            type = 0;
                        }
                    }
                }
                break;
            case 13:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = datas.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = String.valueOf(data.size()).concat(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                                fileTypeModel model = new fileTypeModel(displayName, false);
                                model.setFilePath(path);
                                data.add(model);
                                adapter.notifyDataSetChanged();
                                binding.attachHolder.setVisibility(View.GONE);
                                binding.spinner.setSelection(0);
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = String.valueOf(data.size()).concat(myFile.getName());
                        fileTypeModel model = new fileTypeModel(displayName, false);
                        model.setFilePath(path);
                        data.add(model);
                        adapter.notifyDataSetChanged();
                        binding.attachHolder.setVisibility(View.GONE);
                        binding.spinner.setSelection(0);
                        uploadFiles(CONSTANTS.fileType_PDF, uri, displayName);
                    }
                }
                break;
        }
    }

    void uploadFiles(int fileType, Uri file, String fileName) {

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (fileType == CONSTANTS.fileType_image)
            reference = storage.getReference().child("Data").child("Main").child(user.getUid()).child("Images").child(String.valueOf(currentTime));
        else if (fileType == CONSTANTS.fileType_PDF)
            reference = storage.getReference().child("Data").child("Main").child(user.getUid()).child("PDF").child(String.valueOf(currentTime));
        else if (fileType == CONSTANTS.fileType_audio)
            reference = storage.getReference().child("Data").child("Main").child(user.getUid()).child("Audios").child(String.valueOf(currentTime));

        binding.button9.setEnabled(false);
        reference.putFile(file).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            int pos = adapter.getTargetPosition(fileName);
            adapter.updateProg(pos, (int) progress);
            adapter.notifyItemChanged(pos);
        }).addOnSuccessListener(taskSnapshot -> {
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                if (uri != null) {
                    linkModel linkModel = new linkModel(uri.toString(), fileName);
                    links.add(linkModel);
                    adapter.uploadDone(adapter.getTargetPosition(fileName));
                    adapter.notifyDataSetChanged();
                    binding.button9.setEnabled(true);
                }
            });
        });
    }
}