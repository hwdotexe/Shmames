package tech.hadenw.shmamesbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	private static Random r;
	private static String[] gifBlacklist = new String[] {"https://tenor.com/4xEs.gif", "https://tenor.com/4xBX.gif"};
	
	public static void Init() {
		r = new Random();
	}
	
	public static int getRandom(int bound) {
		return r.nextInt(bound);
	}
	
	public static <T> T randomItem(Set<T> set) {
	    int num = getRandom(set.size());
	    for(T t: set)
	    	if (--num < 0)
	    		return t;
	    throw new AssertionError();
	}
	
	public static String getFriendlyDate(Calendar c) {
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);
		
		return (c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+" at "+ (hour == 0 ? "12" : hour) +":"+(minute < 10 ? "0"+minute : minute)+(c.get(Calendar.AM_PM) == 1 ? "PM" : "AM");
	}
	
	public static String sendGET(String u) {
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		    conn.setRequestMethod("GET");
		    
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String result = "";
		    
		    while ((line = rd.readLine()) != null) {
		       result += line;
		    }
		    
		    rd.close();
		    
		    return result;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static String sendPOST(String u, JSONObject body) {
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		    conn.setRequestMethod("POST");
		    
		    if(body != null) {
		    	conn.setDoOutput(true);
		    	conn.setRequestProperty("content-type", "application/json");
		    	conn.getOutputStream().write(body.toString().getBytes());
		    }
		    
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String result = "";
		    
		    while ((line = rd.readLine()) != null) {
		       result += line;
		    }
		    
		    rd.close();
		    
		    return result;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static String getWA(String search) {
		try {
			String searchFormatted = URLEncoder.encode(search, "UTF-8");
			String result = sendGET("http://api.wolframalpha.com/v1/result?appid=7YX496-E2479K2AE6&i="+searchFormatted);
			
			if(result != null) {
				return result.trim();
			}else
				return "I'm not too sure on that one!";
		}catch(Exception e) {
			return "There may have been a tiny problem...";
		}
	}
	
	public static String getReverseImage(String imageURL) {
		try {
			String searchImage = URLEncoder.encode(imageURL, "UTF-8");
			String result = sendGET("https://app.zenserp.com/api/v2/search?hl=en&gl=US&search_engine=google.com&apikey=bf076150-2029-11ea-9962-3b4bab6b129c&image_url="+searchImage);
			
			JSONObject r = new JSONObject(result);
			JSONArray a = r.getJSONObject("reverse_image_results").getJSONArray("pages_with_matching_images");
			
			String results = "";
			if(!a.isEmpty()) {
				// Grab the first 5 links
				for(int i=0; i<5; i++) {
					if(!a.isNull(i)) {
						if(results.length()>0)
							results += "\n";
						
						results += "**"+(i+1)+"**: "+a.getJSONObject(i).getString("url");
					} else {
						break;
					}
				}
			}else {
				// Nothing found, send the search query
				results = "I couldn't find anything. Maybe you give it a shot:\n"+r.getJSONObject("query").getString("url");
			}
			
			return "I found these:\n"+results;
		}catch(Exception e) {
			return "My mommy said I can't look that one up :(";
		}
	}
	
	@SuppressWarnings("unused")
	public static String getGIF(String search) {
		search = search.trim().replaceAll(" ", "%20");
		String result = sendGET("https://api.tenor.com/v1/search?q="+search+"&key=1CI2O5Y3VUY1&contentfilter=low&limit=20");
		
		JSONObject json = new JSONObject(result);
	    JSONArray jsonArray = json.getJSONArray("results");
	    List<String> gifURLs = new ArrayList<String>();
	    
	    for(int i=0; i<jsonArray.length(); i++) {
	    	gifURLs.add(jsonArray.getJSONObject(i).getString("url"));
	    }
	    
	    if(gifURLs.size() > 0) {
	    	String url = "";	    	
	    	// Check if the GIF is blacklisted, and if so, pick a different one.
	    	for(int x=0; x<50; x++) {
	    		url = gifURLs.get(r.nextInt(gifURLs.size()));
	    		
	    		for(int i=0; i<gifBlacklist.length; i++) {
	    			if(gifBlacklist[i].equals(url)) {
	    				url = "I couldn't find a GIF that _wasn't_ blacklisted...";
	    				continue;
	    			}
	    		}
	    		
	    		break;
	    	}
	    	
	    	return url;
	    
	    } else {
	    	String[] keyword = new String[] {"lost", "crash", "404", "anime", "cat", "doggo", "explode", "dragon", "deal with it"};
	    	String[] message = new String[] {"Aw shoot, this is the best I can do", "All I found was this", "The bad news is I didn't find that. The good news is",
	    			"I think you'd like this instead", "Nah, how 'bout", "I would prefer not to"};
	    	
	    	return message[r.nextInt(message.length)]+": "+getGIF(keyword[r.nextInt(keyword.length)]);
	    }
	}
	
	public static Random GetRandomObj() {
		return r;
	}
}
