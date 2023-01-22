package com.reiserx.nimbleq.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.UserData;

import java.util.HashMap;
import java.util.Map;

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
}
