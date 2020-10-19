package com.hadenwatne.discordbot.http;

import com.hadenwatne.discordbot.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShmamesHTTPHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String URI = httpExchange.getRequestURI().toString();
        Utils.HTTPVerb v = null;
        HashMap<String, String> queryStrings = getQueryStrings(URI);
        String route = getRoute(URI);

        if ("POST".equals(httpExchange.getRequestMethod())) {
            v = Utils.HTTPVerb.POST;
        } else {
            v = Utils.HTTPVerb.GET;
        }

        String result = processRequest(route, queryStrings);
        sendResponse(httpExchange, result);
    }

    private String processRequest(String route, HashMap<String, String> queryStrings)  {
        switch(route) {
            case "":
            case "/":
                return "Pong!";
            case "setstatus":
                if(queryStrings.size() > 0) {
                    if(queryStrings.get("text") != null) {
                        ShmamesActions.SetTempStatus(Activity.ActivityType.CUSTOM_STATUS, queryStrings.get("text"));
                        return "Success";
                    }
                }
            default:
                return null;
        }
    }

    private void sendResponse(HttpExchange httpExchange, String result) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String response = "";
        int responseCode = 200;

        if(result == null) {
            responseCode = 400;
            response = "Bad request / Not successful";
        }

        httpExchange.sendResponseHeaders(responseCode, response.length());
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String getRoute(String URI) {
        return URI.split("shmames")[1];
    }

    private HashMap<String, String> getQueryStrings(String URI) {
        HashMap<String, String> strings = new HashMap<>();

        if(URI.contains("?")) {
            Matcher m = Pattern.compile("([\\w\\d]+)=([\\w\\d]+)", Pattern.CASE_INSENSITIVE).matcher(URI);

            while(m.find()) {
                strings.put(m.group(1).toLowerCase(), m.group(2));
            }
        }

        return strings;
    }
}
