package com.hadenwatne.discordbot.http;

import com.hadenwatne.discordbot.Shmames;
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
        String result = null;

        System.out.println("[HTTP/"+httpExchange.getRequestMethod()+"] to \""+httpExchange.getRequestURI().toString()+"\" from "+httpExchange.getRemoteAddress().getAddress().getHostAddress());

        if(isAuthenticated(queryStrings)) {
            if ("POST".equals(httpExchange.getRequestMethod())) {
                v = Utils.HTTPVerb.POST;
            } else {
                v = Utils.HTTPVerb.GET;
            }

            result = processRequest(route, queryStrings);
        }

        sendResponse(httpExchange, result);
    }

    private String processRequest(String route, HashMap<String, String> queryStrings)  {
        try {
            switch (route) {
                case "":
                    return "Pong!";
                case "setstatus":
                    if (queryStrings.size() > 0) {
                        if (queryStrings.get("text") != null) {
                            ShmamesActions.SetTempStatus(Activity.ActivityType.DEFAULT, queryStrings.get("text"));
                            return "Success";
                        }
                    }
                default:
                    return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // TODO this should be adapted to send accurate HTTP codes (401, 404, 50x, etc.)
    private void sendResponse(HttpExchange httpExchange, String result) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        int responseCode = 200;

        if(result == null) {
            responseCode = 400;
            result = "Bad request / Not successful";
        }

        httpExchange.sendResponseHeaders(responseCode, result.length());
        outputStream.write(result.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String getRoute(String URI) {
        String r = URI.split("shmames")[1];

        if(r.startsWith("/"))
            r = r.substring(1);

        if(r.contains("?"))
            r = r.split("\\?")[0];

        return r;
    }

    private HashMap<String, String> getQueryStrings(String URI) {
        HashMap<String, String> strings = new HashMap<>();

        if(URI.contains("?")) {
            Matcher m = Pattern.compile("([\\w\\d]+)=([\\w\\d%]+)", Pattern.CASE_INSENSITIVE).matcher(URI);

            while(m.find()) {
                strings.put(m.group(1).toLowerCase(), m.group(2).replaceAll("%20", " "));
            }
        }

        return strings;
    }

    private boolean isAuthenticated(HashMap<String, String> queryStrings) {
        if(queryStrings.size() > 0){
            if(queryStrings.containsKey("api_key")){
                return Shmames.getBrains().getMotherBrain().getShmamesAPIKeys().contains(queryStrings.get("api_key"));
            }
        }

        return false;
    }
}
