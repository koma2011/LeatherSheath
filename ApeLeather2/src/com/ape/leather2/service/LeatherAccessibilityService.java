package com.ape.leather2.service;

import com.ape.leather2.module.data.LeatherData;
import com.ape.leather2.module.log.Logger;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class LeatherAccessibilityService extends AccessibilityService {
    
    private static final String TAG = LeatherAccessibilityService.class.getSimpleName();
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "[onCreate]");
        LeatherData.getInstance().setLeatherStatusOpen(this);
    }
    
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.i(TAG, "[onServiceConnected]");
        
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Logger.i(TAG, "[onAccessibilityEvent]");
    }

    @Override
    public void onInterrupt() {
        Logger.i(TAG, "[onInterrupt]");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "[onDestroy]");
        LeatherData.getInstance().setLeatherStatusClose(this);
    }
    
    

}
