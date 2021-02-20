package com.hadenwatne.shmames.http;

import org.json.JSONObject;

public class ShmamesHTTPResponse {
    private int responseCode;
    private JSONObject responseData;
    private boolean hasBeenPackaged;

    public ShmamesHTTPResponse() {
        this.responseCode = 100;
        this.responseData = new JSONObject();
        this.hasBeenPackaged = false;
    }

    public ShmamesHTTPResponse(int code, JSONObject data) {
        this.responseCode = code;
        this.responseData = data;
        this.hasBeenPackaged = false;

        ready();
    }

    public ShmamesHTTPResponse(int code, String data) {
        this.responseCode = code;
        this.responseData = new JSONObject();
        this.hasBeenPackaged = false;

        this.responseData.put("text", data);

        ready();
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    public void setResponseData(String data) {
        this.responseData = new JSONObject();
        this.hasBeenPackaged = false;

        this.responseData.put("text", data);
    }

    public void setResponseData(JSONObject data) {
        this.responseData = data;
        this.hasBeenPackaged = false;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public JSONObject getResponseData() {
        return this.responseData;
    }

    public void ready() {
        this.packageDataToJSON();
    }

    private void packageDataToJSON() {
        if(!this.hasBeenPackaged) {
            JSONObject json = new JSONObject();

            json.put("code", this.responseCode);
            json.put("response", this.responseData);

            this.responseData = json;
            this.hasBeenPackaged = true;
        }
    }
}
