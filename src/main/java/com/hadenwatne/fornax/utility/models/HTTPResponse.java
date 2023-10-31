package com.hadenwatne.fornax.utility.models;

import org.json.JSONObject;

public record HTTPResponse(int responseCode, JSONObject responseObject) {
}
