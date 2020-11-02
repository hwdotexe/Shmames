package com.hadenwatne.discordbot.http;

import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.dv8tion.jda.api.entities.Activity;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShmamesHTTPHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String URI = httpExchange.getRequestURI().toString();
        Utils.HTTPVerb v;
        HashMap<String, String> queryStrings = getQueryStrings(URI);
        String route = getRoute(URI);

        System.out.println("[HTTP/"+httpExchange.getRequestMethod()+"] to \""+route+"\" from "+httpExchange.getRemoteAddress().getAddress().getHostAddress());

        if(isAuthenticated(queryStrings)) {
            if ("POST".equals(httpExchange.getRequestMethod())) {
                v = Utils.HTTPVerb.POST;
            } else {
                v = Utils.HTTPVerb.GET;
            }

            ShmamesHTTPResponse result = processRequest(route, v, queryStrings);

            sendResponse(httpExchange, result);
        } else {
            ShmamesHTTPResponse notAuth = new ShmamesHTTPResponse(401, "Not authorized.");

            sendResponse( httpExchange, notAuth);
        }
    }

    private ShmamesHTTPResponse processRequest(String route, Utils.HTTPVerb verb, HashMap<String, String> queryStrings)  {
        ShmamesHTTPResponse response = new ShmamesHTTPResponse();

        switch (route.toLowerCase()) {
            case "":
                response.setResponseCode(200);
                response.setResponseData("Pong!");

                break;
            case "status":
                if(verb == Utils.HTTPVerb.POST) {
                    String type = "DEFAULT";

                    if (queryStrings.containsKey("type")) {
                        type = queryStrings.get("type").toUpperCase();

                        if(!type.equalsIgnoreCase("Listening") && !type.equalsIgnoreCase("Watching") && !type.equalsIgnoreCase("Default")) {
                            type = "DEFAULT";
                        }
                    }

                    if (queryStrings.containsKey("text")) {
                        ShmamesActions.SetTempStatus(Activity.ActivityType.valueOf(type), queryStrings.get("text"));

                        response.setResponseCode(200);
                        response.setResponseData("Status changed successfully.");
                    }
                } else if(verb == Utils.HTTPVerb.GET) {
                    JSONObject status = new JSONObject();
                    JSONObject data = new JSONObject();

                    data.put("type", ShmamesActions.GetStatus().getType().toString());
                    data.put("text", ShmamesActions.GetStatus().getName());
                    status.put("status", data);

                    response.setResponseCode(200);
                    response.setResponseData(status);
                } else {
                    response.setResponseCode(405);
                    response.setResponseData("Method not allowed.");
                }

                break;
            default:
                response.setResponseCode(404);
                response.setResponseData("The requested route could not be found.");
                break;
        }

        response.ready();

        return response;
    }

    private void sendResponse(HttpExchange httpExchange, ShmamesHTTPResponse result) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String data = result.getResponseData().toString();

        httpExchange.sendResponseHeaders(result.getResponseCode(), data.length());
        outputStream.write(data.getBytes());
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
