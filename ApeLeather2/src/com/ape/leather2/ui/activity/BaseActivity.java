package com.ape.leather2.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * @author juan.li
 * @date Dec 1, 2015 2:33:28 PM
 */
public class BaseActivity extends Activity {
    
    protected static final String TAG = BaseActivity.class.getName();
    
    protected Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }
}
