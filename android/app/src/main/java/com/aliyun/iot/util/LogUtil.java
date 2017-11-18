/**
 * Alibaba.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.aliyun.iot.util;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @version $Id: LogUtil.java,v 0.1 2016年6月2日 下午4:39:18  Exp $
 */
public class LogUtil {
    
    /** 是否打印日志 **/
    public static boolean   showLog      = true;

    static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    /**
     * 简单日志打印
     * 
     * @param msg
     */
    public static void print(String msg) {
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
