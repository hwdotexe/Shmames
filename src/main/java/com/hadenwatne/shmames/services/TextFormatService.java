package com.hadenwatne.shmames.services;

import java.util.Calendar;

public class TextFormatService {
    public static final String EMOJI_RED_X = "\u274c";

    public static final String EMOJI_NUMBER_0 = "\u0030\u20E3";
    public static final String EMOJI_NUMBER_1 = "\u0031\u20E3";
    public static final String EMOJI_NUMBER_2 = "\u0032\u20E3";
    public static final String EMOJI_NUMBER_3 = "\u0033\u20E3";
    public static final String EMOJI_NUMBER_4 = "\u0034\u20E3";
    public static final String EMOJI_NUMBER_5 = "\u0035\u20E3";
    public static final String EMOJI_NUMBER_6 = "\u0036\u20E3";
    public static final String EMOJI_NUMBER_7 = "\u0037\u20E3";
    public static final String EMOJI_NUMBER_8 = "\u0038\u20E3";
    public static final String EMOJI_NUMBER_9 = "\u0039\u20E3";

    public static final String EMOJI_LETTER_A = "\uD83C\uDDE6";
    public static final String EMOJI_LETTER_B = "\uD83C\uDDE7";
    public static final String EMOJI_LETTER_C = "\uD83C\uDDE8";
    public static final String EMOJI_LETTER_D = "\uD83C\uDDE9";
    public static final String EMOJI_LETTER_E = "\uD83C\uDDEA";
    public static final String EMOJI_LETTER_F = "\uD83C\uDDEB";
    public static final String EMOJI_LETTER_G = "\uD83C\uDDEC";
    public static final String EMOJI_LETTER_H = "\uD83C\uDDED";
    public static final String EMOJI_LETTER_I = "\uD83C\uDDEE";
    public static final String EMOJI_LETTER_J = "\uD83C\uDDEF";
    public static final String EMOJI_LETTER_K = "\uD83C\uDDF0";
    public static final String EMOJI_LETTER_L = "\uD83C\uDDF1";
    public static final String EMOJI_LETTER_M = "\uD83C\uDDF2";
    public static final String EMOJI_LETTER_N = "\uD83C\uDDF3";
    public static final String EMOJI_LETTER_O = "\uD83C\uDDF4";
    public static final String EMOJI_LETTER_P = "\uD83C\uDDF5";
    public static final String EMOJI_LETTER_Q = "\uD83C\uDDF6";
    public static final String EMOJI_LETTER_R = "\uD83C\uDDF7";
    public static final String EMOJI_LETTER_S = "\uD83C\uDDF8";
    public static final String EMOJI_LETTER_T = "\uD83C\uDDF9";
    public static final String EMOJI_LETTER_U = "\uD83C\uDDFA";
    public static final String EMOJI_LETTER_V = "\uD83C\uDDFB";
    public static final String EMOJI_LETTER_W = "\uD83C\uDDFC";
    public static final String EMOJI_LETTER_X = "\uD83C\uDDFD";
    public static final String EMOJI_LETTER_Y = "\uD83C\uDDFE";
    public static final String EMOJI_LETTER_Z = "\uD83C\uDDFF";

    public static final String EMOJI_SYMBOL_DASH = "\u2796";
    public static final String EMOJI_SYMBOL_DOLLAR = "\uD83D\uDCB2";
    public static final String EMOJI_SYMBOL_MIDDLE_FINGER = "\uD83D\uDD95";


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
                return EMOJI_NUMBER_1;
            case 2:
                return EMOJI_NUMBER_2;
            case 3:
                return EMOJI_NUMBER_3;
            case 4:
                return EMOJI_NUMBER_4;
            case 5:
                return EMOJI_NUMBER_5;
            case 6:
                return EMOJI_NUMBER_6;
            case 7:
                return EMOJI_NUMBER_7;
            case 8:
                return EMOJI_NUMBER_8;
            case 9:
                return EMOJI_NUMBER_9;
            default:
                return EMOJI_NUMBER_0;
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
            case EMOJI_NUMBER_1:
                return 1;
            case EMOJI_NUMBER_2:
                return 2;
            case EMOJI_NUMBER_3:
                return 3;
            case EMOJI_NUMBER_4:
                return 4;
            case EMOJI_NUMBER_5:
                return 5;
            case EMOJI_NUMBER_6:
                return 6;
            case EMOJI_NUMBER_7:
                return 7;
            case EMOJI_NUMBER_8:
                return 8;
            case EMOJI_NUMBER_9:
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
                return EMOJI_LETTER_A;
            case 'b':
                return EMOJI_LETTER_B;
            case 'c':
                return EMOJI_LETTER_C;
            case 'd':
                return EMOJI_LETTER_D;
            case 'e':
                return EMOJI_LETTER_E;
            case 'f':
                return EMOJI_LETTER_F;
            case 'g':
                return EMOJI_LETTER_G;
            case 'h':
                return EMOJI_LETTER_H;
            case 'i':
                return EMOJI_LETTER_I;
            case 'j':
                return EMOJI_LETTER_J;
            case 'k':
                return EMOJI_LETTER_K;
            case 'l':
                return EMOJI_LETTER_L;
            case 'm':
                return EMOJI_LETTER_M;
            case 'n':
                return EMOJI_LETTER_N;
            case 'o':
                return EMOJI_LETTER_O;
            case 'p':
                return EMOJI_LETTER_P;
            case 'q':
                return EMOJI_LETTER_Q;
            case 'r':
                return EMOJI_LETTER_R;
            case 's':
                return EMOJI_LETTER_S;
            case 't':
                return EMOJI_LETTER_T;
            case 'u':
                return EMOJI_LETTER_U;
            case 'v':
                return EMOJI_LETTER_V;
            case 'w':
                return EMOJI_LETTER_W;
            case 'x':
                return EMOJI_LETTER_X;
            case 'y':
                return EMOJI_LETTER_Y;
            case 'z':
                return EMOJI_LETTER_Z;
            case '-':
            case '_':
                return EMOJI_SYMBOL_DASH;
            case '$':
                return EMOJI_SYMBOL_DOLLAR;
            default:
                return EMOJI_SYMBOL_MIDDLE_FINGER;
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
