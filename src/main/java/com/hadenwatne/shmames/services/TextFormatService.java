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
     * Converts a number (1 - 26) to the corresponding letter in the alphabet. 1 = A, 26 = Z.
     * @param number The number to convert.
     * @return A Unicode string for the emoji.
     */
    public static String NumberToLetter(int number) {
        switch(number) {
            case 1:
                return "\uD83C\uDDE6";
            case 2:
                return "\uD83C\uDDE7";
            case 3:
                return "\uD83C\uDDE8";
            case 4:
                return "\uD83C\uDDE9";
            case 5:
                return "\uD83C\uDDEA";
            case 6:
                return "\uD83C\uDDEB";
            case 7:
                return "\uD83C\uDDEC";
            case 8:
                return "\uD83C\uDDED";
            case 9:
                return "\uD83C\uDDEE";
            case 10:
                return "\uD83C\uDDEF";
            case 11:
                return "\uD83C\uDDF0";
            case 12:
                return "\uD83C\uDDF1";
            case 13:
                return "\uD83C\uDDF2";
            case 14:
                return "\uD83C\uDDF3";
            case 15:
                return "\uD83C\uDDF4";
            case 16:
                return "\uD83C\uDDF5";
            case 17:
                return "\uD83C\uDDF6";
            case 18:
                return "\uD83C\uDDF7";
            case 19:
                return "\uD83C\uDDF8";
            case 20:
                return "\uD83C\uDDF9";
            case 21:
                return "\uD83C\uDDFA";
            case 22:
                return "\uD83C\uDDFB";
            case 23:
                return "\uD83C\uDDFC";
            case 24:
                return "\uD83C\uDDFD";
            case 25:
                return "\uD83C\uDDFE";
            case 26:
                return "\uD83C\uDDFF";
            default:
                return "\uD83D\uDD95";
        }
    }

    /**
     * Converts a letter emoji to the corresponding position in the alphabet. 1 = A, 26 = Z.
     * @param letterEmoji The letter to convert.
     * @return An Integer
     */
    public static Integer LetterToNumber(String letterEmoji) {
        switch(letterEmoji) {
            case "\uD83C\uDDE6":
                return 1;
            case "\uD83C\uDDE7":
                return 2;
            case "\uD83C\uDDE8":
                return 3;
            case "\uD83C\uDDE9":
                return 4;
            case "\uD83C\uDDEA":
                return 5;
            case "\uD83C\uDDEB":
                return 6;
            case "\uD83C\uDDEC":
                return 7;
            case "\uD83C\uDDED":
                return 8;
            case "\uD83C\uDDEE":
                return 9;
            case "\uD83C\uDDEF":
                return 10;
            case "\uD83C\uDDF0":
                return 11;
            case "\uD83C\uDDF1":
                return 12;
            case "\uD83C\uDDF2":
                return 13;
            case "\uD83C\uDDF3":
                return 14;
            case "\uD83C\uDDF4":
                return 15;
            case "\uD83C\uDDF5":
                return 16;
            case "\uD83C\uDDF6":
                return 17;
            case "\uD83C\uDDF7":
                return 18;
            case "\uD83C\uDDF8":
                return 19;
            case "\uD83C\uDDF9":
                return 20;
            case "\uD83C\uDDFA":
                return 21;
            case "\uD83C\uDDFB":
                return 22;
            case "\uD83C\uDDFC":
                return 23;
            case "\uD83C\uDDFD":
                return 24;
            case "\uD83C\uDDFE":
                return 25;
            case "\uD83C\uDDFF":
                return 26;
            default:
                return -1;
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
