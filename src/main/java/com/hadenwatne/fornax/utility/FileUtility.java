package com.hadenwatne.fornax.utility;

import com.hadenwatne.fornax.App;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtility {
    public static File[] ListFilesInDirectory(String directoryPath, FileFilter filter) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory.listFiles(filter);
    }

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
            App.getLogger().LogException(e);
            return null;
        }
    }

    public static File WriteBytesToFile(String directory, String fileName, byte[] bytesToWrite) {
        try {
            File parentDirectory = new File(directory);

            if (!parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            File file = new File(directory + File.separator + fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream os = new FileOutputStream(file);

            os.write(bytesToWrite);
            os.flush();
            os.close();

            return file;
        } catch (Exception e) {
            App.getLogger().LogException(e);
        }

        return null;
    }
}
