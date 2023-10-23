package com.hadenwatne.shmames.services;

import com.hadenwatne.botcore.service.LoggingService;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

    /**
     * Attempts to load a given File as a String, and returns the result.
     * @param f The File to load.
     * @return The File's content as a String, or "" by default.
     */
    public static String LoadFileAsString(File f) {
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
            LoggingService.LogException(e);
        }

        return "";
    }

    /**
     * Writes a byte array to a file specified. Overwrites existing file contents.
     * @param directory The parent directory to contain the file.
     * @param fileName The name of the file to write, including extension.
     * @param bytesToWrite The byte array to write to file.
     */
    public static File SaveBytesToFile(String directory, String fileName, byte[] bytesToWrite) {
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

            return file;
        } catch (Exception e) {
            LoggingService.LogException(e);
        }

        return null;
    }
}
