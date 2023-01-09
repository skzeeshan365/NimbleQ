package com.reiserx.nimbleq.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class UserTypeClass {
    Context context;
    boolean flag = false;

    public UserTypeClass(Context context) {
        this.context = context;
    }

    public Boolean isUserLearner() {
        SharedPreferences save = context.getSharedPreferences("Utils", MODE_PRIVATE);
        if (save.getInt("userType", 0) == 1)
            flag = true;
        if (save.getInt("userType", 0) == 2)
            flag = false;
        return flag;
    }
}
