package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {

    public static void log(final String messg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                java.util.Date date = new java.util.Date();
                try {
                    String logMessage = null;
                    logMessage = new Timestamp(date.getTime()).toString() + "=>" + messg;
                    FileWriter fw = new FileWriter("out.log", true);
                    fw.write(logMessage + "\n");
                    fw.close();
                    System.out.println(logMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}