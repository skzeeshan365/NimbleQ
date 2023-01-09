package com.reiserx.nimbleq.Utils;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

public class SnackbarTop {
    View view;

    public SnackbarTop(View view) {
        this.view = view;
    }

    public void showSnackBar(String message, boolean isSuccess) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        if (isSuccess) {
            snackbarView.setBackgroundColor(Color.parseColor("#00B308"));
        } else snackbarView.setBackgroundColor(Color.parseColor("#F44336"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        snackbar.show();
    }
}
