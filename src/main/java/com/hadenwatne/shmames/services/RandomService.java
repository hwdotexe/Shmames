package com.hadenwatne.shmames.services;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomService {
    private static Random r;

    /**
     * Prepare variables for use.
     */
    public static void Init() {
        r = new Random();
    }

    /**
     * Retrieves the basic Random instance currently being used.
     * @return A Random object.
     */
    public static Random GetRandomObj() {
        return r;
    }

    /**
     * Creates a random integer based on a possible maximum (exclusive).
     * @param bound The exclusive maximum value.
     * @return A random integer.
     */
    public static int GetRandom(int bound) {
        return r.nextInt(bound);
    }

    /**
     * Returns a random value from a Set input.
     * @param set The unordered list to use.
     * @return A random item from the Set.
     */
    public static <T> T GetRandomHashMap(Set<T> set) {
        int num = GetRandom(set.size());
        for (T t : set)
            if (--num < 0)
                return t;
        throw new AssertionError();
    }

    /**
     * Returns a random string from a Set of strings.
     * @param items The Set to use.
     * @return A random string.
     */
    public static String GetRandomStringFromSet(Set<String> items) {
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
    public static String GetRandomStringFromList(List<String> items) {
        int target = r.nextInt(items.size());

        return items.get(target);
    }

    /**
     * Creates a random, 5-character ID.
     * @return A string ID.
     */
    public static String CreateID() {
        final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder newID = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            newID.append(alpha.charAt(GetRandom(alpha.length())));
        }

        return newID.toString();
    }
}
