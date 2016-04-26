package com.ape.leather2.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * @author juan.li
 * @date Dec 16, 2015 3:52:17 PM
 */
public class ScrollTextView extends TextView {

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ScrollTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
