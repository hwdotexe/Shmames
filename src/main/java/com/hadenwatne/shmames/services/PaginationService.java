package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaginationService {
    /**
     * Builds a PaginatedList representing the paginated form of the data provided.
     * @param data Raw data to paginate.
     * @param numberPerPage The number of items per each page.
     * @param maxElementLength The max string length of each item (for display purposes). Use -1 to ignore truncating.
     * @param number Whether to number the items in the list.
     * @return A PaginatedList representing the data.
     */
    public static PaginatedList GetPaginatedList(List<String> data, int numberPerPage, int maxElementLength, boolean number) {
        List<String> paginatedList = new ArrayList<>();
        StringBuilder workingPage = new StringBuilder();
        int itemsAddedToPage = 0;

        for(int i=0; i<data.size(); i++) {
            String item = data.get(i);
            item = (maxElementLength > 0 && item.length() > maxElementLength) ? (item.substring(0, maxElementLength) + "...") : item;

            if(itemsAddedToPage == numberPerPage) {
                paginatedList.add(workingPage.toString());
                workingPage = new StringBuilder();
                itemsAddedToPage = 0;
            }

            if(workingPage.length() > 0) {
                workingPage.append(System.lineSeparator());
            }

            if(number) {
                workingPage.append("**"+(i+1)+"**: ");
            }

            workingPage.append(item);
            itemsAddedToPage++;
        }

        if(workingPage.length() > 0) {
            paginatedList.add(workingPage.toString());
        }

        PaginatedList listObject = new PaginatedList(paginatedList, data.size());

        return listObject;
    }

    /**
     * Generates an embedded message using a pre-paginated List.
     * @param paginatedList The pre-paginated list to draw.
     * @param pageToDisplay The page of the list to display.
     * @param prefix The prefix to use in the header before the page indicator.
     * @param color The color to make for this embed.
     * @param language The Lang file being used.
     * @return An EmbedBuilder displaying the data.
     */
    public static EmbedBuilder DrawEmbedPage(PaginatedList paginatedList, int pageToDisplay, String prefix, Color color, Language language) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        List<String> listData = paginatedList.getPaginatedList();
        int pageIndex = pageToDisplay - 1;

        eBuilder.setColor(color);
        eBuilder.setAuthor(App.Shmames.getBotName(), null, App.Shmames.getJDA().getSelfUser().getAvatarUrl());
        eBuilder.setFooter(paginatedList.getItemCount() + " items");

        if(pageToDisplay > listData.size() || pageToDisplay == 0) {
            eBuilder.addField("Error", language.getError(ErrorKey.PAGE_NOT_FOUND), false);
        } else {
            eBuilder.addField(prefix + " (Page " + pageToDisplay + " of " + listData.size() + ")", listData.get(pageIndex), false);
        }

        return eBuilder;
    }

    /**
     * Creates a list of items in a standardized, visually-appealing way.
     * @param items The items to list out.
     * @param perRow The number of items to have per row.
     * @param numbered Whether to number the list.
     * @param indented Whether to make the list indented using Discord's quote styling.
     * @return The generated list.
     */
    public static String GenerateList(List<String> items, int perRow, boolean numbered, boolean indented) {
        StringBuilder list = new StringBuilder();
        Pattern emote = Pattern.compile("(<(:[a-z]+:)\\d+>)", Pattern.CASE_INSENSITIVE);

        int inRow = 0;
        for (int i=0; i<items.size(); i++) {
            String item = items.get(i);

            if (list.length() > 0) {
                list.append(numbered ? System.lineSeparator() : ", ");
            }

            if (!numbered && perRow > 0) {
                inRow++;

                if (inRow > perRow) {
                    list.append(System.lineSeparator());
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
     * Creates a list of items in a standardized way.
     * @param items A map of items to list.
     * @param perRow The number of items to have per row.
     * @param indented Whether to make the list indented using Discord's quote styling.
     * @return The generated list.
     */
    public static <T> String GenerateList(HashMap<String, T> items, int perRow, boolean indented) {
        StringBuilder list = new StringBuilder();

        int inRow = 0;
        for (String i : items.keySet()) {
            if (perRow > 0) {
                inRow++;

                if (inRow > perRow) {
                    list.append(System.lineSeparator());

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
     * Takes a String List and compiles the values into a single string, with each value separated on a new line.
     * @param stringList The List to compile.
     * @return The compiled String.
     */
    public static String CompileListToString(List<String> stringList) {
        StringBuilder sb = new StringBuilder();

        for(String s : stringList) {
            if(sb.length() > 0) {
                sb.append(System.lineSeparator());
            }

            sb.append(s);
        }

        return sb.toString();
    }

    /**
     * Splits a string into multiple strings on the given length, taking care to split on whitespaces.
     * @param s The string to split.
     * @param interval The number of characters to split on.
     * @return An array of split strings.
     */
    public static List<String> SplitString(String s, int interval) {
        List<String> splits = new ArrayList<>();

        if (s.length() > interval) {
            int lastIndex = 0;

            while(lastIndex < s.length()-1) {
                String sub = s.length() >= lastIndex + interval ? s.substring(lastIndex, lastIndex + interval) : s.substring(lastIndex);

                // Remove any breaks at the beginning
                if(sub.startsWith(System.lineSeparator())){
                    sub = sub.substring(System.lineSeparator().length());
                }

                // Break on newline chars when possible.
                if(sub.contains(System.lineSeparator())) {
                    if (sub.endsWith(System.lineSeparator())|| sub.length() < interval) {
                        splits.add(sub);
                        lastIndex += interval;
                    } else {
                        int lastSpace = sub.lastIndexOf(System.lineSeparator());

                        String newSplit = sub.substring(0, lastSpace);
                        splits.add(newSplit);
                        lastIndex = s.indexOf(newSplit) + newSplit.length();
                    }
                } else {
                    if (sub.charAt(sub.length() - 1) == ' ' || sub.length() < interval) {
                        splits.add(sub);
                        lastIndex += interval;
                    } else {
                        int lastSpace = sub.lastIndexOf(" ");

                        String newSplit = sub.substring(0, lastSpace);
                        splits.add(newSplit);
                        lastIndex = s.indexOf(newSplit) + newSplit.length();
                    }
                }
            }
        } else {
            splits.add(s);
        }

        return splits;
    }
}
