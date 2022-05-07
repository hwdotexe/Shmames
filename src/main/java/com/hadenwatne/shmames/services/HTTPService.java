package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.HTTPVerb;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.Lang;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HTTPService {
    /**
     * Creates and performs a synchronous HTTP request, and provides the response.
     * @param v The HTTP Verb to use with this request.
     * @param fqurl The fully-qualified URL for this request.
     * @param body The JSON body of this request, or null if none.
     * @return A string response, if received.
     */
    public static String SendHTTPReq(HTTPVerb v, String fqurl, JSONObject body) {
        try {
            URL url = new URL(fqurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            switch (v) {
                case GET:
                    conn.setRequestMethod("GET");

                    break;
                case POST:
                    conn.setRequestMethod("POST");

                    if (body != null) {
                        conn.setDoOutput(true);
                        conn.setRequestProperty("content-type", "application/json");
                        conn.getOutputStream().write(body.toString().getBytes());
                    }

                    break;
            }

            // Retrieve data
            if (conn.getResponseCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder result = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                rd.close();
                conn.disconnect();

                return result.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            LoggingService.LogException(e);
            return null;
        }
    }

    /**
     * Makes a request to Wolfram Alpha and returns the response.
     * @param search The query to ask the API.
     * @return The API's response.
     */
    public static String GetWolfram(String search) {
        Lang lang = App.Shmames.getLanguageService().getDefaultLang();

        try {
            String searchFormatted = URLEncoder.encode(search, "UTF-8");
            String result = SendHTTPReq(HTTPVerb.GET, "http://api.wolframalpha.com/v1/result?appid=" + App.Shmames.getStorageService().getMotherBrain().getWolframAPIKey() + "&i=" + searchFormatted, null);

            if (result != null) {
                return result.trim();
            } else {
                return lang.getError(Errors.ITEMS_NOT_FOUND);
            }
        } catch (Exception e) {
            return lang.getError(Errors.BOT_ERROR);
        }
    }

    /**
     * Makes a request to Tenor and returns a random result.
     * @param search The query to search for.
     * @return A random GIF URL from the results of the search.
     */
    public static String GetGIF(String search, String filter) {
        search = search.trim().replaceAll(" ", "%20");
        String result = SendHTTPReq(HTTPVerb.GET, "https://g.tenor.com/v1/search?q=" + search + "&key=" + App.Shmames.getStorageService().getMotherBrain().getTenorAPIKey() + "&contentfilter=" + filter + "&limit=25", null);

        Random r = RandomService.GetRandomObj();
        JSONObject json = new JSONObject(result);
        JSONArray jsonArray = json.getJSONArray("results");
        List<String> gifURLs = new ArrayList<>();
        List<JSONArray> gifMedia = new ArrayList<>();

        // Add the media array of each result.
        for (int i = 0; i < jsonArray.length(); i++) {
            gifMedia.add(jsonArray.getJSONObject(i).getJSONArray("media"));
        }

        // For each media array, add the gif value.
        for (int i=0; i < gifMedia.size(); i++) {
            JSONArray media = gifMedia.get(i);

            for (int o=0; o < media.length(); o++) {
                JSONObject resultObject = media.getJSONObject(o);
                JSONObject gif = resultObject.getJSONObject("gif");
                JSONObject mediumgif = resultObject.getJSONObject("mediumgif");
                JSONObject tinygif = resultObject.getJSONObject("tinygif");
                String url;

                if(gif.getInt("size") <= 8000000) {
                    url = gif.getString("url");
                } else if(mediumgif.getInt("size") <= 8000000) {
                    url = mediumgif.getString("url");
                } else {
                    url = tinygif.getString("url");
                }

                gifURLs.add(url);
            }
        }

        if (gifURLs.size() > 0) {
            String url = gifURLs.get(r.nextInt(gifURLs.size()));

            LoggingService.Log(LogType.NETWORK, "[GIF Search: " + search + " @ " + filter + "]");

            return url;

        } else {
            String[] keyword = new String[]{"lost", "crash", "404", "anime", "cat", "doggo", "explode", "dragon", "deal with it"};
            String[] message = new String[]{"Aw shoot, this is the best I can do", "All I found was this", "The bad news is I didn't find that. The good news is",
                    "I think you'd like this instead", "Nah, how 'bout", "I would prefer not to"};

            return message[r.nextInt(message.length)] + ": " + GetGIF(keyword[r.nextInt(keyword.length)], filter);
        }
    }
}
