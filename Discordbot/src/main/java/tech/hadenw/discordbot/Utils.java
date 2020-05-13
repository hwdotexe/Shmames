package tech.hadenw.discordbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.hadenw.discordbot.storage.BotSetting;
import tech.hadenw.discordbot.storage.BotSettingType;
import tech.hadenw.discordbot.storage.Brain;

public class Utils {
	private static Random r;
	private static String[] gifBlacklist = new String[] {"https://tenor.com/4xEs.gif", "https://tenor.com/4xBX.gif", "https://tenor.com/3RW4.gif"};
	
	/**
	 * Prepare variables for use.
	 */
	public static void Init() {
		r = new Random();
	}
	
	/**
	 * Creates a random integer based on a possible maximum (exclusive).
	 * @param bound The exclusive maximum value.
	 * @return A random integer.
	 */
	public static int getRandom(int bound) {
		return r.nextInt(bound);
	}
	
	/**
	 * Returns a random value from a Set input.
	 * @param set The unordered list to use.
	 * @return A random item from the Set.
	 */
	public static <T> T getRandomHashMap(Set<T> set) {
	    int num = getRandom(set.size());
	    for(T t: set)
	    	if (--num < 0)
	    		return t;
	    throw new AssertionError();
	}
	
	/**
	 * Creates a random, 5-character ID.
	 * @return A string ID.
	 */
	public static String createID() {
		final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String newID = "";
		
		for(int i=0; i<5; i++) {
			newID += alpha.charAt(getRandom(alpha.length()));
		}
		
		return newID;
	}
	
	/**
	 * Creates a friendly time readout from a given Calendar object.
	 * @param c The Calendar to use.
	 * @return A string representing this calendar.
	 */
	public static String getFriendlyDate(Calendar c) {
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);
		
		return (c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH)+" at "+ (hour == 0 ? "12" : hour) +":"+(minute < 10 ? "0"+minute : minute)+(c.get(Calendar.AM_PM) == 1 ? "PM" : "AM");
	}
	
	/**
	 * A basic enumeration to control values passed into an HTTP request.
	 */
	public enum HTTPVerb {
		GET,
		POST
	}
	
	/**
	 * Creates and performs a synchronous HTTP request, and provides the response.
	 * @param v The HTTP Verb to use with this request.
	 * @param fqurl The fully-qualified URL for this request.
	 * @param body The JSON body of this request, or null if none.
	 * @return A string response, if received.
	 */
	public static String sendHTTPReq(HTTPVerb v, String fqurl, JSONObject body) {
		try {
			URL url = new URL(fqurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			switch(v) {
			case GET:
				conn.setRequestMethod("GET");
				
				break;
			case POST:
				conn.setRequestMethod("POST");
				
				if(body != null) {
			    	conn.setDoOutput(true);
			    	conn.setRequestProperty("content-type", "application/json");
			    	conn.getOutputStream().write(body.toString().getBytes());
			    }
				
				break;
			}
			
			// Retrieve data
			if(conn.getResponseCode() == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    String line;
			    String result = "";
			    
			    while ((line = rd.readLine()) != null) {
			       result += line;
			    }
			    
			    rd.close();
			    conn.disconnect();
			    
			    return result;
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sorts a HashMap based on the Integer value of a String key.
	 * From https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	 * @param passedMap The HashMap to sort.
	 * @return A sorted HashMap.
	 */
	public static LinkedHashMap<String, Integer> sortHashMap(HashMap<String, Integer> passedMap) {
	    List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
	    List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        int val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            int comp1 = passedMap.get(key);
	            int comp2 = val;

	            if (comp1 == comp2) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	/**
	 * Makes a request to Wolfram Alpha and returns the response.
	 * @param search The query to ask the API.
	 * @return The API's response.
	 */
	public static String getWolfram(String search) {
		try {
			String searchFormatted = URLEncoder.encode(search, "UTF-8");
			String result = sendHTTPReq(HTTPVerb.GET, "http://api.wolframalpha.com/v1/result?appid=7YX496-E2479K2AE6&i="+searchFormatted, null);
			
			if(result != null) {
				return result.trim();
			} else {
				return "I'm not too sure on that one!";
			}
		}catch(Exception e) {
			return "There may have been a tiny problem...";
		}
	}
	
	/**
	 * Makes a request to Zenserp to search for an image, and returns possible sources.
	 * @param imageURL The URL of the image to search for.
	 * @return A list of 5 maximum results.
	 */
	public static String getReverseImage(String imageURL) {
		try {
			String searchImage = URLEncoder.encode(imageURL, "UTF-8");
			String result = sendHTTPReq(HTTPVerb.GET, "https://app.zenserp.com/api/v2/search?hl=en&gl=US&search_engine=google.com&apikey=bf076150-2029-11ea-9962-3b4bab6b129c&image_url="+searchImage, null);
			
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
	/**
	 * Makes a request to Tenor and returns a random result.
	 * @param search The query to search for.
	 * @return A random GIF URL from the results of the search. 
	 */
	public static String getGIF(String search, String filter) {
		search = search.trim().replaceAll(" ", "%20");
		String result = sendHTTPReq(HTTPVerb.GET, "https://api.tenor.com/v1/search?q="+search+"&key=CLEMV01ZTSAP&contentfilter="+filter+"&limit=25", null);
		
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

	    	System.out.println("[GIF Search: "+search+" @ "+filter+"]");
	    	
	    	return url;
	    
	    } else {
	    	String[] keyword = new String[] {"lost", "crash", "404", "anime", "cat", "doggo", "explode", "dragon", "deal with it"};
	    	String[] message = new String[] {"Aw shoot, this is the best I can do", "All I found was this", "The bad news is I didn't find that. The good news is",
	    			"I think you'd like this instead", "Nah, how 'bout", "I would prefer not to"};
	    	
	    	return message[r.nextInt(message.length)]+": "+getGIF(keyword[r.nextInt(keyword.length)], filter);
	    }
	}
	
	/**
	 * Retrieves the basic Random instance currently being used.
	 * @return A Random object.
	 */
	public static Random GetRandomObj() {
		return r;
	}

	/**
	 * Increments the tally for a given emote.
	 * @param b The Brain to tally the emote in.
	 * @param id The Emote ID.
	 */
	public static void IncrementEmoteTally(Brain b, String id){
		if(b.getEmoteStats().containsKey(id)) {
			b.getEmoteStats().put(id, b.getEmoteStats().get(id)+1);
		}else {
			b.getEmoteStats().put(id, 1);
		}
	}

	/**
	 * Checks whether the member complies with the setting's permission
	 * requirements, if applicable.
	 * @param setting The setting to check.
	 * @param member The user to check.
	 * @return A boolean representing whether the user complies.
	 */
	public static boolean CheckUserPermission(BotSetting setting, Member member){
		if(setting.getType() == BotSettingType.ROLE) {
			String sv = setting.getValue();
			Role r = !sv.equals("administrator") && !sv.equals("everyone") ? member.getGuild().getRolesByName(sv, true).get(0) : null;

			if(Shmames.isDebug)
				return true;

			if(sv.equals("everyone"))
				return true;

			if(sv.equals("administrator"))
				return member.hasPermission(Permission.ADMINISTRATOR);

			return member.getRoles().contains(r);
		}

		return false;
	}

	/**
	 * Creates a list of items in a standardized, visually-appealing way.
	 * @param items The items to list out.
	 * @param perRow The number of items to have per row.
	 * @return The generated list.
	 */
	public static String GenerateList(List<String> items, int perRow, boolean numbered){
		StringBuilder list = new StringBuilder();
		Pattern emote = Pattern.compile("(<(:[a-z]+:)\\d+>)", Pattern.CASE_INSENSITIVE);

		int inRow = 0;
		for(String i : items){
			if(list.length() > 0) {
				list.append(numbered ? "\n" : ", ");
			}

			if(!numbered && perRow > 0) {
				inRow++;

				if (inRow > perRow) {
					list.append("\n");
					inRow = 1;
				}
			}

			if(numbered) {
				list.append("> ");
				list.append(items.indexOf(i)+1);
				list.append(": ");
			}

			Matcher eMatcher = emote.matcher(i);
			while(eMatcher.find()){
				i = i.replaceFirst(eMatcher.group(1), eMatcher.group(2));
			}

			list.append("`");
			list.append(i);
			list.append("`");
		}

		return list.toString();
	}
}
