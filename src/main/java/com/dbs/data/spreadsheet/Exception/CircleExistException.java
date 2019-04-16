package com.dbs.data.spreadsheet.Exception;

/**
 * @author erpu.yang
 * @date 2019/04/16
 */
public class CircleExistException extends Exception {

    public CircleExistException() {
        super("some nodes depend on each other.");
    }
}
