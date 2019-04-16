package com.dbs.data.log;

/**
 * @author erpu.yang
 * @date 2019/04/15
 */
public class Logger {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void error(String message) {
        System.err.println(message);
    }
}
