package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {

    private static String file = "";

    public static boolean LOG_LEARNING = false;
    public static boolean LOG_AGENTS = false;
    public static boolean LOG_SUMO = false;


    private static void log(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                java.util.Date date = new java.util.Date();
                if (file.equals("")) {
                    file = "out_" + new Timestamp(new java.util.Date().getTime()).toString() + ".log";
                }
                try {
                    String logMessage = new Timestamp(date.getTime()).toString() + "=>" + message;
                    FileWriter fw = new FileWriter(file, true);
                    fw.write(logMessage + "\n");
                    fw.close();
                    System.out.println(logMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void logLearning(final String message) {
        if (LOG_LEARNING) {
            log("L: " + message);
        }
    }

    public static void logAgents(final String message) {
        if (LOG_AGENTS) {
            log("A: " + message);
        }
    }

    public static void logSumo(final String message) {
        if (LOG_SUMO) {
            log("S: " + message);
        }
    }

}