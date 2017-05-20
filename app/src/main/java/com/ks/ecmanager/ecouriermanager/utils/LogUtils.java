/*
 * Copyright (c) 2017. kazi srabon. Contact : kaziiit@gmail.com
 */

package com.ks.ecmanager.ecouriermanager.utils;

public class LogUtils {
    static final boolean log = false;

    public static void i(String tag, String string) {
        if (log) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (log) android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (log) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (log) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (log) android.util.Log.w(tag, string);
    }
}