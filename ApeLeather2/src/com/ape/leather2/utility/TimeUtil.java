package com.ape.leather2.utility;

/**
 * @author juan.li
 * @date 2015-09-10 20:12:00
 */
public class TimeUtil {
    public static String elapsedTimer(long elapsed) {

        int milli = (int) (elapsed % 1000);
        elapsed = elapsed / 1000;

        int second = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        int minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        int hour = (int) (elapsed % 60);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
