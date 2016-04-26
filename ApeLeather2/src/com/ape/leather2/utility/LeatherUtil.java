package com.ape.leather2.utility;

import com.ape.leather2.R;

/**
 * @author juan.li
 * @date Dec 16, 2015 3:33:32 PM
 */
public class LeatherUtil {
    
    public static int getImageResource(int index) {
        int result = 0;
        if (1 == index) {
            result = R.drawable.unread_1;
        } else if (2 == index) {
            result = R.drawable.unread_2;
        } else if (3 == index) {
            result = R.drawable.unread_3;
        } else if (4 == index) {
            result = R.drawable.unread_4;
        } else if (5 == index) {
            result = R.drawable.unread_5;
        } else if (6 == index) {
            result = R.drawable.unread_6;
        } else if (7 == index) {
            result = R.drawable.unread_7;
        } else if (8 == index) {
            result = R.drawable.unread_8;
        } else if (9 == index) {
            result = R.drawable.unread_9;
        } else if (index > 9) {
            result = R.drawable.unread_9plus;
        }
        return result;
    }
    
}
