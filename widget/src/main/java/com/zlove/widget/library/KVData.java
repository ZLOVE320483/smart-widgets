package com.zlove.widget.library;

public class KVData {

    private String key;
    private Object data;

    public KVData(String key, Object data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public <T> T getData() {
        return data == null ? null : (T) data;
    }
}
