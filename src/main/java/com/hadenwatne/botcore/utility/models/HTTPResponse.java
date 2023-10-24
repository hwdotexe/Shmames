package com.hadenwatne.botcore.utility.models;

import org.json.JSONObject;

public class HTTPResponse {
    private final int responseCode;
    private final JSONObject responseObject;

    public HTTPResponse(int responseCode, JSONObject responseObject) {
        this.responseCode = responseCode;
        this.responseObject = responseObject;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public JSONObject getResponseObject() {
        return responseObject;
    }
}
