package com.reiserx.nimbleq.Utils;

import java.text.DecimalFormat;

public class fileSize {

    public String getFileSize(long size) {

        DecimalFormat df = new DecimalFormat("0");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " GB";
        return "";
    }
}
