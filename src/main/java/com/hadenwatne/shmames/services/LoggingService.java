package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.enums.LogType;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoggingService {
    private static List<String> tempLog;
    private static Calendar cal;

    public static void Init() {
        tempLog = new ArrayList<>();
        cal = Calendar.getInstance();

        File logDir = new File("logs");

        if(!logDir.exists()) {
            logDir.mkdir();
        }
    }

    public static void Log(LogType type, String log) {
        log = "["+getDateTime()+"]["+type.name()+"] "+log;

        System.out.println(log);
        tempLog.add(log);
    }

    public static void LogException(Throwable e) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(getDateTime());
        sb.append("]");
        sb.append("[");
        sb.append(LogType.ERROR.name());
        sb.append("] ");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);
        sb.append(sw.toString());

        pw.close();

        String log = sb.toString();

        System.out.println(log);
        tempLog.add(log);
    }

    public static void Write() {
        try {
            File logFile = new File("logs/"+getDate()+".log");
            FileWriter fw;

            if(!logFile.exists()) {
                logFile.createNewFile();

                fw = new FileWriter(logFile, false);
            } else {
                fw = new FileWriter(logFile, true);
            }

            PrintWriter pw = new PrintWriter(fw);

            for(String log : tempLog) {
                pw.println(log);
            }

            pw.close();
            fw.close();

            tempLog.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getDate() {
        cal.setTime(new Date());

        return cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
    }

    private static String getDateTime() {
        int minute = cal.get(Calendar.MINUTE);
        return getDate()+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+(minute < 10 ? "0"+minute : minute);
    }
}
