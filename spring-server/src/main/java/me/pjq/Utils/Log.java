package me.pjq.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    static String dateFormat = "yyyy-MM-dd hh:mm:ss";
    static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    static boolean showLog = true;

    public static void log(String tag, String msg) {
        if (showLog) {
            String source = null;
            try {
                StackTraceElement st = Thread.currentThread().getStackTrace()[2];
                source = "[" + st.getFileName() + "] - " + st.getMethodName() + "("
                        + st.getLineNumber() + ")";
            } catch (Exception e) {
            }

            System.out.println(fm.format(new Date()) + " - " + source + ":" + msg);
        }
    }
}
