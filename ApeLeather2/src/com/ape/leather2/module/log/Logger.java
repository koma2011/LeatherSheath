package com.ape.leather2.module.log;

import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 * @author juan.li
 * @date Aug 12, 2015 4:31:34 PM
 */
public class Logger {
    
    public static boolean DEBUG = true;
    
    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }
    
    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }
    
    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }
    
    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
    
    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }
    
    public static void v(String tag, Object o) {
        if (DEBUG) {
            Log.v(tag, toJson(o));
        }
    }
    
    public static void d(String tag, Object o) {
        if (DEBUG) {
            Log.d(tag, toJson(o));
        }
    }
    
    public static void i(String tag, Object o) {
        if (DEBUG) {
            Log.i(tag, toJson(o));
        }
    }
    
    public static void e(String tag, Object o) {
        if (DEBUG) {
            Log.e(tag, toJson(o));
        }
    }
    
    public static void w(String tag, Object o) {
        if (DEBUG) {
            Log.w(tag, toJson(o));
        }
    }
    
    public static void v(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.v(tag, String.format(format, args));
        }
    }
    
    public static void d(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.d(tag, String.format(format, args));
        }
    }
    
    public static void i(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.i(tag, String.format(format, args));
        }
    }
    
    public static void e(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.e(tag, String.format(format, args));
        }
    }
    
    public static void w(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.w(tag, String.format(format, args));
        }
    }
    
    public static String toJson(Object msg) {
        if (msg instanceof String) {
            return msg.toString();
        }
        
        String json = JSON.toJSONString(msg);
        if (json.length() > 500) {
            json = json.substring(0, 500);
        }
        
        return json;
    }
}
