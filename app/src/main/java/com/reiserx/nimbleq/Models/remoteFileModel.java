package com.reiserx.nimbleq.Models;

public class remoteFileModel {
    String Filename, url;

    public remoteFileModel(String filename, String url) {
        Filename = filename;
        this.url = url;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
