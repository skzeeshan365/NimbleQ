package com.reiserx.nimbleq.Models;

public class fileTypeModel {
    String filename, filePath;
    int prog;
    boolean uploaded;

    public fileTypeModel(String filename, boolean isUploaded) {
        this.filename = filename;
        this.uploaded = isUploaded;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getProg() {
        return prog;
    }

    public void setProg(int prog) {
        this.prog = prog;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
