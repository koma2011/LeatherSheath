package com.ape.leather2.ui.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author juan.li
 * @date Dec 3, 2015 10:55:26 AM
 */
public abstract class AbsLeatherPage {
    
    private static final String TAG = AbsLeatherPage.class.getName();
    
    protected Context mContext;
    
    public AbsLeatherPage(Context context) {
        mContext = context;
    }
    
    public abstract View onCreate(ViewGroup parent);
    
//    public abstract void onDestory();
    
    public abstract String getTag();
}
