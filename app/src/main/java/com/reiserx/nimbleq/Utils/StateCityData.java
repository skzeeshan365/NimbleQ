package com.reiserx.nimbleq.Utils;

import android.content.Context;
import android.util.Log;

import com.reiserx.nimbleq.Constants.CONSTANTS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StateCityData {

    Context context;

    public StateCityData(Context context) {
        this.context = context;
    }

    public String loadJSONFile() {
        String json;
        try {
            InputStream inputStream = context.getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] byteArray = new byte[size];
            inputStream.read(byteArray);
            inputStream.close();
            json = new String(byteArray, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(CONSTANTS.TAG, e.toString());
            return null;
        }
        return json;
    }

    public List<String> getStates(String jsonString) {
        List<String> stateList = new ArrayList<>();
        stateList.add("Select state");
        JSONObject jsonArray = null;
        try {
            jsonArray = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator iterator = jsonArray.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            stateList.add(key);
        }
        return stateList;
    }

    public List<String> getCities(String jsonString, String state) {
        List<String> cityList = new ArrayList<>();
        try {
            cityList.add("Select city");
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = null;
            jsonArray = jsonObject.getJSONArray(state);
            for (int i = 0; i < jsonArray.length(); i++) {
                cityList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(CONSTANTS.TAG, e.toString());
        }
        return cityList;
    }
}
