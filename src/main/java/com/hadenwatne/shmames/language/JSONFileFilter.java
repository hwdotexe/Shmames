package com.hadenwatne.shmames.language;

import java.io.File;
import java.io.FileFilter;

public class JSONFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".json");
    }
}
