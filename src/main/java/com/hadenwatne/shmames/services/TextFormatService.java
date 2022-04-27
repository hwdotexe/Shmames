package com.hadenwatne.shmames.services;

import java.util.Calendar;

public class TextFormatService {
    /**
     * Creates a friendly time readout from a given Calendar object.
     * @param c The Calendar to use.
     * @return A string representing this calendar.
     */
    public static String GetFriendlyDateTime(Calendar c) {
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        return (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + " at " + (hour == 0 ? "12" : hour) + ":" + (minute < 10 ? "0" + minute : minute) + (c.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM");
    }

    /**
     * Creates a standardized date string.
     * @param c The Calendar to use.
     * @return A string representing this calendar.
     */
    public static String GetISO8601Date(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    /**
     * Converts an integer to a Unicode emoji of the same number.
     * @param i The number to convert.
     * @return A Unicode string representing the emoji character.
     */
    public static String IntToEmoji(int i) {
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
    public static int EmojiToInt(String i) {
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
    public static String LetterToEmoji(char letter) {
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
    public static String DuplicateLetterToEmoji(char letter) {
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
}
