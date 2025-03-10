package com.hadenwatne.shmames.services;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hadenwatne.shmames.App;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class FileService {
    /**
     * Uses a filter to build a list of files in a given directory path. Creates the directory if it does not exist.
     * @param directoryPath The path to list child files.
     * @param filter A filter to use in the search.
     * @return A File array of files that matched the filter within the directory.
     */
    public static File[] ListFilesInDirectory(String directoryPath, FileFilter filter) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory.listFiles(filter);
    }

    public static <T> T LoadFileAsType(File f, Type type) {
        try {
            Reader reader = new FileReader(f, StandardCharsets.UTF_8);
            JsonReader jsonReader = App.gson.newJsonReader(reader);

            T obj = App.gson.fromJson(jsonReader, type);

            jsonReader.close();

            return obj;
        } catch (Exception e) {
            LoggingService.LogException(e);
        }

        return null;
    }

    public static File SaveBytesToFile(String directory, String fileName, Gson gson, Object dataToWrite, Type objectType) {
        try {
            File file = new File(directory + File.separator + fileName);
            File parentDirectory = new File(directory);

            if(!parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            Writer writer = new FileWriter(file, StandardCharsets.UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);

            gson.toJson(dataToWrite, objectType, jsonWriter);

            jsonWriter.close();

            return file;
        } catch (Exception e) {
            LoggingService.LogException(e);
        }

        return null;
    }
}
