/*
 */

package com.leaningtech.client;

/**
 * This is just a dummy class
 */
public
class Global {
    public static Object jsCall(String funcName, Object... arg) {
        System.out.println("Called dummy jsCall()");
        return null;
    }

    public static int jsCallI(String funcName, Object... arg) {
        System.out.println("Called dummy jsCallI()");
        return -1;
    }
}
