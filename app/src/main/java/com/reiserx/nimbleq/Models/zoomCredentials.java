package com.reiserx.nimbleq.Models;

public class zoomCredentials {
    String SDK_KEY, SDK_SECRET;

    public zoomCredentials(String SDK_KEY, String SDK_SECRET) {
        this.SDK_KEY = SDK_KEY;
        this.SDK_SECRET = SDK_SECRET;
    }

    public zoomCredentials() {
    }

    public String getSDK_KEY() {
        return SDK_KEY;
    }

    public void setSDK_KEY(String SDK_KEY) {
        this.SDK_KEY = SDK_KEY;
    }

    public String getSDK_SECRET() {
        return SDK_SECRET;
    }

    public void setSDK_SECRET(String SDK_SECRET) {
        this.SDK_SECRET = SDK_SECRET;
    }
}
