package com.ape.leather2.service;

import com.ape.leather2.module.data.LeatherData;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class LeatherAccessibilityManager {
    
    private static final String LEATHER_ACCESSIBILITY_SERVICE = "com.ape.leather2/com.ape.leather2.service.LeatherAccessibilityService";
    
    private static LeatherAccessibilityManager ourInstance = new LeatherAccessibilityManager();
    
    public static LeatherAccessibilityManager getInstance() {
        return ourInstance;
    }
    
    private LeatherAccessibilityManager() {
        
    }
    
    public void init(Context context) {
        enableAccessibilityService(context);
    }
    
    private void enableAccessibilityService(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        boolean isEnable = false;
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        isEnable = true;
                        break;
                    }
                }
            }
        }
        boolean folio_status = LeatherData.getInstance().getLeatherStatus(context);
        if(!isEnable && folio_status) {
            StringBuilder sb = new StringBuilder();
            if(!TextUtils.isEmpty(flat)) {
                sb.append(flat);
                sb.append(":");
            }
            sb.append(LEATHER_ACCESSIBILITY_SERVICE);
            try {
                Settings.Secure.putString(contentResolver,Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, sb.toString());
                Settings.Secure.putInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
