package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;

import java.util.ArrayList;

public class TeacherListSpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> teacherList;
    ArrayList<UserData> userData;
    LayoutInflater inflter;

    public TeacherListSpinnerAdapter(Context applicationContext, ArrayList<String> teacherList, ArrayList<UserData> userData) {
        this.context = applicationContext;
        this.teacherList = teacherList;
        this.userData = userData;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return teacherList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_layout, null);
        TextView names = view.findViewById(R.id.name_txt);
        TextView rating = view.findViewById(R.id.rating_rxt);
        SimpleRatingBar ratingBar = view.findViewById(R.id.ratingBar);

        names.setText(teacherList.get(i));
        UserData userDatas = userData.get(i);
        Log.d(CONSTANTS.TAG2, String.valueOf(userData.size()));

        if (i == 0) {
            rating.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }

        if (userDatas != null) {
            if (userDatas.getRating() > 0) {
                @SuppressLint("DefaultLocale") String ratings = String.format("%.1f", userDatas.getRating());
                rating.setText(ratings);
                ratingBar.setRating(Float.parseFloat(ratings) / 5);
            } else {
                rating.setText("0");
                ratingBar.setRating(0);
            }
        }
        return view;
    }
}