package com.reiserx.nimbleq.Models;

public class AboutModelList {
    String name, data;
    boolean header;

    public AboutModelList(String name, String data, boolean header) {
        this.name = name;
        this.data = data;
        this.header = header;
    }

    public AboutModelList(String name, boolean header) {
        this.name = name;
        this.header = header;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
