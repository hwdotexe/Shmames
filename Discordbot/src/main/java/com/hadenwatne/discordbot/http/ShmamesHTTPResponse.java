package com.hadenwatne.discordbot.http;

import org.json.JSONObject;

public class ShmamesHTTPResponse {
    private int responseCode;
    private String responseData;
    private boolean hasBeenPackaged;

    public ShmamesHTTPResponse() {
        this.responseCode = 100;
        this.responseData = "";
        this.hasBeenPackaged = false;
    }

    public ShmamesHTTPResponse(int code, String data) {
        this.responseCode = code;
        this.responseData = data;
        this.hasBeenPackaged = false;

        ready();
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    public void setResponseData(String data) {
        this.responseData = data;
        this.hasBeenPackaged = false;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponseData() {
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

            this.responseData = json.toString();
            this.hasBeenPackaged = true;
        }
    }
}
