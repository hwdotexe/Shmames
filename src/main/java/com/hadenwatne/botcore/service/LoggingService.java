package com.hadenwatne.botcore.service;

import com.hadenwatne.botcore.service.types.LogType;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoggingService {
    private final String LOGS_FOLDER = "logs";

    private List<String> _tempLog;
    private Calendar _cal;

    public LoggingService() {
        _tempLog = new ArrayList<>();
        _cal = Calendar.getInstance();

        File logDir = new File(LOGS_FOLDER);

        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }

    public void Log(LogType type, String log) {
        log = "[" + getDateTime() + "] [" + type.name() + "] " + log;

        System.out.println(log);
        _tempLog.add(log);
    }

    public void LogException(Throwable e) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(getDateTime());
        sb.append("] ");
        sb.append("[");
        sb.append(LogType.ERROR.name());
        sb.append("] ");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);
        sb.append(sw);

        pw.close();

        String log = sb.toString();

        System.out.println(log);
        _tempLog.add(log);
    }

    public void Write() {
        try {
            File logFile = new File(LOGS_FOLDER + File.separator + getDate() + ".log");
            FileWriter fw;

            if (!logFile.exists()) {
                logFile.createNewFile();

                fw = new FileWriter(logFile, false);
            } else {
                fw = new FileWriter(logFile, true);
            }

            PrintWriter pw = new PrintWriter(fw);

            for (String log : _tempLog) {
                pw.println(log);
            }

            pw.close();
            fw.close();

            _tempLog.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDate() {
        _cal.setTime(new Date());

        return _cal.get(Calendar.YEAR) + "-" + (_cal.get(Calendar.MONTH) + 1) + "-" + _cal.get(Calendar.DAY_OF_MONTH);
    }

    private String getDateTime() {
        String date = getDate();
        int minuteInt = _cal.get(Calendar.MINUTE);
        String minute = minuteInt < 10 ? ("0" + minuteInt) : Integer.toString(minuteInt);

        return date + " " + _cal.get(Calendar.HOUR_OF_DAY) + ":" + minute;
    }
}