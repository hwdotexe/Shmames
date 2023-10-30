package com.hadenwatne.corvus;

import java.net.URL;

public class CorvusAttachment {
    private URL fileUrl;
    private String fileName;

    CorvusAttachment(URL url, String file) {
        this.fileUrl = url;
        this.fileName = file;
    }

    public URL getFileUrl() {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }
}
