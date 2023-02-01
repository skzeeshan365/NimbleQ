package com.reiserx.nimbleq.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.FCMCREDENTIALS;
import com.reiserx.nimbleq.Models.UserData;

public class SharedPreferenceClass {
    Context context;
    SharedPreferences save;
    Gson gson;

    public SharedPreferenceClass(Context context) {
        this.context = context;
        save = context.getSharedPreferences("Utils", MODE_PRIVATE);
        gson = new Gson();
    }

    public void setDoubtInfo(DoubtsModel model) {
        SharedPreferences.Editor myEdit = save.edit();

        String json = gson.toJson(model);

        myEdit.putString("DOUBT_INFO", json);
        myEdit.apply();
    }

    public DoubtsModel getDoubtInfo() {
        return gson.fromJson(save.getString("DOUBT_INFO", null), DoubtsModel.class);
    }

    public void setUserInfo(UserData model) {
        SharedPreferences.Editor myEdit = save.edit();

        String json = gson.toJson(model);

        myEdit.putString("USER_INFO", json);
        myEdit.apply();
    }

    public UserData getUserInfo() {
        return gson.fromJson(save.getString("USER_INFO", null), UserData.class);
    }

    public void setClassID(String classID) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString("CLASS_ID", classID);
        myEdit.apply();
    }

    public String getClassID() {
        return save.getString("CLASS_ID", null);
    }

    public void setUserID(String classID) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString("USER_ID", classID);
        myEdit.apply();
    }

    public String getUserID() {
        return save.getString("USER_ID", null);
    }

    public void setFCMKey(FCMCREDENTIALS model) {
        SharedPreferences.Editor myEdit = save.edit();

        String json = gson.toJson(model);

        myEdit.putString("FCM_KEY", json);
        myEdit.apply();
    }

    public FCMCREDENTIALS getFCMKey() {
        return gson.fromJson(save.getString("FCM_KEY", null), FCMCREDENTIALS.class);
    }

    public void setSlotLimit(Long limit) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putLong("SLOT_LIMIT", limit);
        myEdit.apply();
    }

    public Long getSlotLimit() {
        return save.getLong("SLOT_LIMIT", 3);
    }

    public void setFileSizeLimit(Long limit) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putLong("FILE_SIZE_LIMIT", limit);
        myEdit.apply();
    }

    public Long getFileSizeLimit() {
        return save.getLong("FILE_SIZE_LIMIT", 50);
    }

    public void setTeacherID(String teacherID) {
        SharedPreferences.Editor myEdit = save.edit();
        myEdit.putString("TEACHER_ID", teacherID);
        myEdit.apply();
    }

    public String getTeacherID() {
        return save.getString("TEACHER_ID", null);
    }
}
