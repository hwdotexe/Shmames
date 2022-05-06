package com.hadenwatne.shmames.services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataService {
    /**
     * Sorts a HashMap based on the Integer value of a String key.
     * From https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
     * @param passedMap The HashMap to sort.
     * @return A sorted HashMap.
     */
    public static LinkedHashMap<String, Integer> SortHashMap(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapValues);
        Collections.reverse(mapKeys);

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
     * Converts a String representation of an amount of time into seconds.
     * Example: 1d -> 86400; 1d24h -> 172800
     * @param timeString The time String to convert. Example: 1d24h30m15s
     * @return An integer equal to the time String in Seconds.
     */
    public static int ConvertTimeStringToSeconds(String timeString) {
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
     * Figures if the String input is a Long.
     * @param test The String to test.
     * @return True if and only if the String is a Long.
     */
    public static boolean IsLong(String test) {
        try {
            Long.parseLong(test);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    /**
     * Figures if the String input is a Boolean.
     * @param test The String to test.
     * @return True if and only if the String is a Long.
     */
    public static boolean IsBoolean(String test) {
        return test.equalsIgnoreCase("true") || test.equalsIgnoreCase("false");
    }

    /**
     * Figures if the String input is an Integer.
     * @param test The String to test.
     * @return True if and only if the String is an Integer.
     */
    public static boolean IsInteger(String test) {
        try {
            Integer.parseInt(test);
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
