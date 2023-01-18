package com.reiserx.nimbleq.Models;

public class uploadProgressModel {
    int progress;
    String filename;

    public uploadProgressModel(int progress, String filename) {
        this.progress = progress;
        this.filename = filename;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
