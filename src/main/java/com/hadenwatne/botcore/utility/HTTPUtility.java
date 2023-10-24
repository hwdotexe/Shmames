package com.hadenwatne.botcore.utility;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.utility.models.HTTPResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HTTPUtility {
    public static HTTPResponse get(String uri) {
        try {
            URL url = URI.create(uri).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            return getResponse(conn);
        } catch (IOException e) {
            App.getLogger().LogException(e);
            return null;
        }
    }

    public static HTTPResponse post(String uri, JSONObject body) {
        return requestWithOutput("POST", uri, body);
    }

    public static HTTPResponse put(String uri, JSONObject body) {
        return requestWithOutput("PUT", uri, body);
    }

    public static HTTPResponse patch(String uri, JSONObject body) {
        return requestWithOutput("PATCH", uri, body);
    }

    public static HTTPResponse delete(String uri, JSONObject body) {
        return requestWithOutput("DELETE", uri, body);
    }

    private static HTTPResponse requestWithOutput(String verb, String uri, JSONObject body) {
        try {
            URL url = URI.create(uri).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(verb);

            if (body != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("content-type", "application/json");
                conn.getOutputStream().write(body.toString().getBytes());
            }

            return getResponse(conn);
        } catch (IOException e) {
            App.getLogger().LogException(e);
            return null;
        }
    }

    private static HTTPResponse getResponse(HttpURLConnection connection) {
        try {
            // Retrieve data
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();
            connection.disconnect();

            JSONObject responseObject = new JSONObject(result.toString());

            return new HTTPResponse(connection.getResponseCode(), responseObject);
        } catch (IOException e) {
            return null;
        }
    }
}