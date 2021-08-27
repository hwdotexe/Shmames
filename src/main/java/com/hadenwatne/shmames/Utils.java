package com.hadenwatne.shmames;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.HTTPVerb;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	private static Random r;

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
		for (T t : set)
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
		StringBuilder newID = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			newID.append(alpha.charAt(getRandom(alpha.length())));
		}

		return newID.toString();
	}

	/**
	 * Creates a friendly time readout from a given Calendar object.
	 * @param c The Calendar to use.
	 * @return A string representing this calendar.
	 */
	public static String getFriendlyDate(Calendar c) {
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);

		return (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + " at " + (hour == 0 ? "12" : hour) + ":" + (minute < 10 ? "0" + minute : minute) + (c.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM");
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
			ShmamesLogger.logException(e);
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

		for (int val : mapValues) {
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				int comp1 = passedMap.get(key);

				if (comp1 == val) {
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
		Lang lang = Shmames.getDefaultLang();

		try {
			String searchFormatted = URLEncoder.encode(search, "UTF-8");
			String result = sendHTTPReq(HTTPVerb.GET, "http://api.wolframalpha.com/v1/result?appid=" + Shmames.getBrains().getMotherBrain().getWolframAPIKey() + "&i=" + searchFormatted, null);

			if (result != null) {
				return result.trim();
			} else {
				return lang.getError(Errors.ITEMS_NOT_FOUND, true);
			}
		} catch (Exception e) {
			return lang.getError(Errors.BOT_ERROR, true);
		}
	}

	/**
	 * Makes a request to Tenor and returns a random result.
	 * @param search The query to search for.
	 * @return A random GIF URL from the results of the search.
	 */
	public static String getGIF(String search, String filter) {
		search = search.trim().replaceAll(" ", "%20");
		String result = sendHTTPReq(HTTPVerb.GET, "https://g.tenor.com/v1/search?q=" + search + "&key=" + Shmames.getBrains().getMotherBrain().getTenorAPIKey() + "&contentfilter=" + filter + "&limit=25", null);

		JSONObject json = new JSONObject(result);
		JSONArray jsonArray = json.getJSONArray("results");
		List<String> gifURLs = new ArrayList<String>();

		for (int i = 0; i < jsonArray.length(); i++) {
			gifURLs.add(jsonArray.getJSONObject(i).getString("url"));
		}

		if (gifURLs.size() > 0) {
			String url = gifURLs.get(r.nextInt(gifURLs.size()));

			ShmamesLogger.log(LogType.NETWORK, "[GIF Search: " + search + " @ " + filter + "]");

			return url;

		} else {
			String[] keyword = new String[]{"lost", "crash", "404", "anime", "cat", "doggo", "explode", "dragon", "deal with it"};
			String[] message = new String[]{"Aw shoot, this is the best I can do", "All I found was this", "The bad news is I didn't find that. The good news is",
					"I think you'd like this instead", "Nah, how 'bout", "I would prefer not to"};

			return message[r.nextInt(message.length)] + ": " + getGIF(keyword[r.nextInt(keyword.length)], filter);
		}
	}

	/**
	 * Retrieves the basic Random instance currently being used.
	 * @return A Random object.
	 */
	public static Random getRandomObj() {
		return r;
	}

	/**
	 * Increments the tally for a given emote.
	 * @param b The Brain to tally the emote in.
	 * @param id The Emote ID.
	 */
	public static void incrementEmoteTally(Brain b, String id) {
		if (b.getEmoteStats().containsKey(id)) {
			b.getEmoteStats().put(id, b.getEmoteStats().get(id) + 1);
		} else {
			b.getEmoteStats().put(id, 1);
		}
	}

	/**
	 * Checks whether the member complies with the setting's permission
	 * requirements, if applicable.
	 * @param setting The setting to check.
	 * @param user The user to check.
	 * @return A boolean representing whether the user complies.
	 */
	public static boolean checkUserPermission(Guild server, BotSetting setting, User user) {
		if(server != null) {
			if (setting.getType() == BotSettingType.ROLE) {
				Member member = server.getMember(user);
				String sv = setting.getValue();

				if (member != null) {
					if (Shmames.isDebug)
						return true;

					if (sv.equals("everyone"))
						return true;

					if (sv.equals("administrator"))
						return member.hasPermission(Permission.ADMINISTRATOR);

					Role r = member.getGuild().getRolesByName(sv, true).get(0);

					return member.getRoles().contains(r);
				}
			}
		}

		return false;
	}

	/**
	 * Creates a list of items in a standardized, visually-appealing way.
	 * @param items The items to list out.
	 * @param perRow The number of items to have per row.
	 * @return The generated list.
	 */
	public static String generateList(List<String> items, int perRow, boolean numbered, boolean indented) {
		StringBuilder list = new StringBuilder();
		Pattern emote = Pattern.compile("(<(:[a-z]+:)\\d+>)", Pattern.CASE_INSENSITIVE);

		int inRow = 0;
		for (int i=0; i<items.size(); i++) {
			String item = items.get(i);

			if (list.length() > 0) {
				list.append(numbered ? "\n" : ", ");
			}

			if (!numbered && perRow > 0) {
				inRow++;

				if (inRow > perRow) {
					list.append("\n");
					inRow = 1;
				}
			}

			if (numbered) {
				if (indented)
					list.append("> ");

				list.append(i + 1);
				list.append(": ");
			}

			Matcher eMatcher = emote.matcher(item);
			while (eMatcher.find()) {
				item = item.replaceFirst(eMatcher.group(1), eMatcher.group(2));
			}

			list.append("`");
			list.append(item);
			list.append("`");
		}

		return list.toString();
	}

	/**
	 * Creates a list of items in a standardized, visually-appealing way.
	 * @param items A map of items to list.
	 * @param perRow The number of items to have per row.
	 * @return The generated list.
	 */
	public static <T> String generateList(HashMap<String, T> items, int perRow, boolean indented) {
		StringBuilder list = new StringBuilder();

		int inRow = 0;
		for (String i : items.keySet()) {
			if (perRow > 0) {
				inRow++;

				if (inRow > perRow) {
					list.append("\n");

					if(indented)
						list.append("> ");

					inRow = 1;
				} else {
					if (list.length() > 0)
						list.append("  ");
					else
						if(indented)
							list.append("> ");
				}
			} else {
				if (list.length() > 0)
					list.append("  ");
				else
					if(indented)
						list.append("> ");
			}

			list.append("`");
			list.append(i);
			list.append("`");
			list.append(": [");
			list.append(items.get(i));
			list.append("]");
		}

		return list.toString();
	}

	/**
	 * Returns a random string from a Set of strings.
	 * @param items The Set to use.
	 * @return A random string.
	 */
	public static String getRandomStringFromSet(Set<String> items) {
		int target = r.nextInt(items.size());
		int i = 0;

		for(String o : items) {
			if(i == target) {
				return o;
			}

			i++;
		}

		return "";
	}

	/**
	 * Returns a random string from a List of strings.
	 * @param items The List to use.
	 * @return A random string.
	 */
	public static String getRandomStringFromList(List<String> items) {
		int target = r.nextInt(items.size());

		return items.get(target);
	}

	/**
	 * Splits a string into multiple strings on the given length, taking care to split on whitespaces.
	 * @param s The string to split.
	 * @param interval The number of characters to split on.
	 * @return An array of split strings.
	 */
	public static String[] splitString(String s, int interval) {
		int breaks = (int) Math.ceil((double) s.length() / (double) interval);
		String[] result = new String[breaks];

		if (s.length() > interval) {
			int lastIndex = 0;
			for (int i = 0; i < breaks; i++) {
				String sub = s.length() >= lastIndex + interval ? s.substring(lastIndex, lastIndex + interval) : s.substring(lastIndex);

				// Remove any breaks at the beginning
				if(sub.startsWith(System.lineSeparator())){
					sub = sub.substring(System.lineSeparator().length());
				}

				// Experiment: Break on newline chars when possible.
				if(sub.contains(System.lineSeparator())) {
					if (sub.endsWith(System.lineSeparator())|| sub.length() < interval) {
						result[i] = sub;
						lastIndex += interval;
					} else {
						int lastSpace = sub.lastIndexOf(System.lineSeparator());

						result[i] = sub.substring(0, lastSpace);
						lastIndex = s.indexOf(result[i]) + result[i].length();
					}
				} else {
					if (sub.charAt(sub.length() - 1) == ' ' || sub.length() < interval) {
						result[i] = sub;
						lastIndex += interval;
					} else {
						int lastSpace = sub.lastIndexOf(" ");

						result[i] = sub.substring(0, lastSpace);
						lastIndex = s.indexOf(result[i]) + result[i].length();
					}
				}
			}
		} else {
			result[0] = s;
		}

		return result;
	}

	/**
	 * Converts a String representation of an amount of time into seconds.
	 * Example: 1d -> 86400; 1d24h -> 172800
	 * @param timeString The time String to convert. Example: 1d24h30m15s
	 * @return An integer equal to the time String in Seconds.
	 */
	public static int convertTimeStringToSeconds(String timeString) {
		Matcher timeMatcher = Pattern.compile("(\\d{1,3})([ydhms])", Pattern.CASE_INSENSITIVE).matcher(timeString);
		int seconds = 0;

		while(timeMatcher.find()) {
			int multiplier = 1;

			switch(timeMatcher.group(2).toLowerCase()) {
				case "y":
					multiplier = 31536000;
					break;
				case "d":
					multiplier = 86400;
					break;
				case "h":
					multiplier = 3600;
					break;
				case "m":
					multiplier = 60;
					break;
				default:
					break;
			}

			seconds += Integer.parseInt(timeMatcher.group(1)) * multiplier;
		}

		return seconds;
	}

	/**
	 * Converts an integer to a Unicode emoji of the same number.
	 * @param i The number to convert.
	 * @return A Unicode string representing the emoji character.
	 */
	public static String intToEmoji(int i) {
		switch(i) {
			case 1:
				return "\u0031\u20E3";
			case 2:
				return "\u0032\u20E3";
			case 3:
				return "\u0033\u20E3";
			case 4:
				return "\u0034\u20E3";
			case 5:
				return "\u0035\u20E3";
			case 6:
				return "\u0036\u20E3";
			case 7:
				return "\u0037\u20E3";
			case 8:
				return "\u0038\u20E3";
			case 9:
				return "\u0039\u20E3";
			default:
				return "\u0030\u20E3";
		}
	}

	/**
	 * Converts a Unicode emoji string into an Integer, if the
	 * emoji represents a number.
	 * @param i The String to convert.
	 * @return An Integer from the emoji.
	 */
	public static int emojiToInt(String i) {
		switch(i) {
			case "\u0031\u20E3":
				return 1;
			case "\u0032\u20E3":
				return 2;
			case "\u0033\u20E3":
				return 3;
			case "\u0034\u20E3":
				return 4;
			case "\u0035\u20E3":
				return 5;
			case "\u0036\u20E3":
				return 6;
			case "\u0037\u20E3":
				return 7;
			case "\u0038\u20E3":
				return 8;
			case "\u0039\u20E3":
				return 9;
			default:
				return 0;
		}
	}

	/**
	 * Converts a character letter to an emoji representation.
	 * @param letter The letter to convert.
	 * @return A Unicode string for the emoji.
	 */
	public static String letterToEmoji(char letter) {
		switch(letter) {
			case 'a':
				return "\uD83C\uDDE6";
			case 'b':
				return "\uD83C\uDDE7";
			case 'c':
				return "\uD83C\uDDE8";
			case 'd':
				return "\uD83C\uDDE9";
			case 'e':
				return "\uD83C\uDDEA";
			case 'f':
				return "\uD83C\uDDEB";
			case 'g':
				return "\uD83C\uDDEC";
			case 'h':
				return "\uD83C\uDDED";
			case 'i':
				return "\uD83C\uDDEE";
			case 'j':
				return "\uD83C\uDDEF";
			case 'k':
				return "\uD83C\uDDF0";
			case 'l':
				return "\uD83C\uDDF1";
			case 'm':
				return "\uD83C\uDDF2";
			case 'n':
				return "\uD83C\uDDF3";
			case 'o':
				return "\uD83C\uDDF4";
			case 'p':
				return "\uD83C\uDDF5";
			case 'q':
				return "\uD83C\uDDF6";
			case 'r':
				return "\uD83C\uDDF7";
			case 's':
				return "\uD83C\uDDF8";
			case 't':
				return "\uD83C\uDDF9";
			case 'u':
				return "\uD83C\uDDFA";
			case 'v':
				return "\uD83C\uDDFB";
			case 'w':
				return "\uD83C\uDDFC";
			case 'x':
				return "\uD83C\uDDFD";
			case 'y':
				return "\uD83C\uDDFE";
			case 'z':
				return "\uD83C\uDDFF";
			case '-':
				return "\u2796";
			case '_':
				return "\u2796";
			case '$':
				return "\uD83D\uDCB2";
			default:
				return "\uD83D\uDD95";
		}
	}

	/**
	 * Same as @letterToEmoji, but returns different emoji
	 * in order to provide duplicates.
	 * @param letter The letter to convert.
	 * @return A Unicode string for the emoji.
	 */
	public static String duplicateLetterToEmoji(char letter) {
		switch(letter) {
			case 'a':
				return "\uD83C\uDD70";
			case 'b':
				return "\uD83C\uDD71";
			case 'e':
				return "\u0033\u20E3";
			case 'i':
				return "\u2139";
			case 'l':
				return "\u0031\u20E3";
			case 'm':
				return "\u24C2";
			case 'o':
				return "\u0030\u20E3";
			case 'p':
				return "\uD83C\uDD7F";
			case 's':
				return "\u0035\u20E3";
			case 'x':
				return "\u2716";
			case 'z':
				return "\u0032\u20E3";
			default:
				return null;
		}
	}

	/**
	 * Uses a filter to build a list of files in a given directory path. Creates the directory if it does not exist.
	 * @param directoryPath The path to list child files.
	 * @param filter A filter to use in the search.
	 * @return A File array of files that matched the filter within the directory.
	 */
	public static File[] listFilesInDirectory(String directoryPath, FileFilter filter) {
		File directory = new File(directoryPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory.listFiles(filter);
	}

	/**
	 * Attempts to load a given File as a String, and returns the result.
	 * @param f The File to load.
	 * @return The File's content as a String, or "" by default.
	 */
	public static String loadFileAsString(File f) {
		try {
			int data;
			FileInputStream is = new FileInputStream(f);
			StringBuilder jsonData = new StringBuilder();

			while ((data = is.read()) != -1) {
				jsonData.append((char) data);
			}

			is.close();

			return jsonData.toString();
		} catch (Exception e) {
			ShmamesLogger.logException(e);
		}

		return "";
	}

	/**
	 * Writes a byte array to a file specified. Overwrites existing file contents.
	 * @param directory The parent directory to contain the file.
	 * @param fileName The name of the file to write, including extension.
	 * @param bytesToWrite The byte array to write to file.
	 */
	public static void saveBytesToFile(String directory, String fileName, byte[] bytesToWrite) {
		try {
			File file = new File(directory + File.separator + fileName);
			File parentDirectory = new File(directory);

			if(!parentDirectory.exists()) {
				parentDirectory.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream os = new FileOutputStream(file);

			os.write(bytesToWrite);
			os.flush();
			os.close();
		} catch (Exception e) {
			ShmamesLogger.logException(e);
		}
	}

	/**
	 * Figures if the String input is an Integer.
	 * @param test The String to test.
	 * @return True if and only if the String is an Integer.
	 */
	public static boolean isInt(String test) {
		try {
			Integer.parseInt(test);
			return true;
		} catch (Exception ignored) {}

		return false;
	}

	/**
	 * Figures if the String input is a Long.
	 * @param test The String to test.
	 * @return True if and only if the String is a Long.
	 */
	public static boolean isLong(String test) {
		try {
			Long.parseLong(test);
			return true;
		} catch (Exception ignored) {}

		return false;
	}

	/**
	 * Returns a list of other Guilds that the provided Guild is connected to via
	 * Shmames Family, excluding the Guild that was passed in.
	 * @param g The Guild to search with.
	 * @return A list of connected Guilds, exclusively.
	 */
	public static List<Guild> GetConnectedFamilyGuilds(Guild g) {
		List<Guild> guilds = new ArrayList<>();

		for(String fid : Shmames.getBrains().getBrain(g.getId()).getFamilies()){
			Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

			for(long mg : f.getMemberGuilds()) {
				if(mg != g.getIdLong()) {
					Guild familyGuild = Shmames.getJDA().getGuildById(mg);

					if(familyGuild != null) {
						guilds.add(familyGuild);
					} else {
						// The guild came back null, so remove it from the Family.
						f.getMemberGuilds().remove(mg);
					}
				}
			}
		}

		return guilds;
	}

	/**
	 * Retrieves a Message indicated by the user. Aware of slash commands & traditional commands.
	 * @param messagingChannel The channel data to use.
	 * @param count The number of messages above to retrieve.
	 * @return A Message, if possible.
	 */
	public static Message GetMessageIndicated(ShmamesCommandMessagingChannel messagingChannel, int count) {
		try {
			long latestMessageID = messagingChannel.getChannel().getLatestMessageIdLong();
			Message originMessage = messagingChannel.hasOriginMessage() ? messagingChannel.getOriginMessage() : messagingChannel.getChannel().retrieveMessageById(latestMessageID).complete();

			Message indicated = null;
			List<Message> messageHistory;
			int limit = count;

			if (messagingChannel.hasHook()) {
				if (count == 1) {
					indicated = originMessage;
					limit = 0;
				} else {
					limit -= 1;
				}
			}

			if (limit > 0) {
				messageHistory = messagingChannel.getChannel().getHistoryBefore(originMessage, limit).complete().getRetrievedHistory();

				// The oldest message in the history we fetched.
				indicated = messageHistory.get(messageHistory.size() - 1);
			}

			return indicated;
		} catch (PermissionException pe) {
			return null;
		}
	}
}
