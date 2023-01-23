package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_layout, null);
        TextView names = view.findViewById(R.id.name_txt);
        TextView rating = view.findViewById(R.id.rating_rxt);
        SimpleRatingBar ratingBar = view.findViewById(R.id.ratingBar);

        names.setText(teacherList.get(i));
        UserData userData = new UserData();

        if (i == 0) {
            rating.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }

        if (userData.getRating() > 0) {
            @SuppressLint("DefaultLocale") String ratings = String.format("%.1f", userData.getRating());
            rating.setText(ratings);
            ratingBar.setRating(Float.parseFloat(ratings) / 5);
        } else  {
            rating.setText("0");
            ratingBar.setRating(0);
        }
        return view;
    }
}