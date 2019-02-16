package tech.hadenw.shmamesbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	private static Random r;
	
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
		int minute = c.get(Calendar.MINUTE);
		
		return (c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR)+":"+(minute < 10 ? "0"+minute : minute)+(c.get(Calendar.AM_PM) == 1 ? "PM" : "AM");
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
	
	public static String getGIF(String search) {
		search = search.replaceAll(" ", "%20");
		String result = sendGET("https://api.tenor.com/v1/search?q="+search+"&key=1CI2O5Y3VUY1&safesearch=moderate&limit=20");
		
		JSONObject json = new JSONObject(result);
	    JSONArray jsonArray = json.getJSONArray("results");
	    List<String> gifURLs = new ArrayList<String>();
	    
	    for(int i=0; i<jsonArray.length(); i++) {
	    	gifURLs.add(jsonArray.getJSONObject(i).getString("url"));
	    }
	    
	    if(gifURLs.size() > 0)
	    	return gifURLs.get(r.nextInt(gifURLs.size()));
	    else
	    	return "Uhh, all I could find was this: https://tenor.com/w3sJ.gif";
	}
}
