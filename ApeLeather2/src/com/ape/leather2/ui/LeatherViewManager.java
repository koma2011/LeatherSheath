package com.ape.leather2.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ape.leather2.R;
import com.ape.leather2.module.data.LeatherData;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.module.telephone.InCallingManager;
import com.ape.leather2.receiver.LeatherReceiver;
import com.ape.leather2.ui.activity.LeatherActivity;
import com.ape.leather2.ui.view.ContainerView;
import com.ape.leather2.ui.view.panel.CallPanel;
import com.ape.leather2.ui.view.panel.MasterPanel;
import com.ape.leather2.utility.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * @author juan.li
 * @date Dec 2, 2015 5:16:05 PM
 */
public class LeatherViewManager {
    
    private static final String TAG = LeatherViewManager.class.getName();
    private static final String KOMA_TAG = "KomaLeatherSheath";
    public static final int STATUS_LEATHER_OPENED                = 0x1000;
    public static final int STATUS_LEATHER_CLOSED                = 0x1001;
    
    private static LeatherViewManager sLeatherViewManager;
    
    private Context mContext;
    private WindowManager mWindowManager;
    private ContainerView mContainerView;
    private LayoutParams mContainerParams;
    private MasterPanel mMasterPanel;
    private CallPanel mCallPanel;
    private ScreenReceiver mScreenReceiver;
    
    private InCallingManager mInCallingManager;
    
    private int mLeatherStatus;
    
    private int mPanelWidth;
    private int mPanelHeight;
    private int mPanelX;
    private int mPanelY;
    
    public static LeatherViewManager getInstance(Context context) {
        if (sLeatherViewManager == null) {
            sLeatherViewManager = new LeatherViewManager(context);
        }
        return sLeatherViewManager;
    }
    
    private LeatherViewManager(Context context) {
        Logger.i(KOMA_TAG, "LeatherViewManager instance");
        mContext = context;

        mInCallingManager = InCallingManager.getInstance(context);
        initUI();
    }
    
    private void initUI() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mContainerView = new ContainerView(mContext);
        mContainerView.setBackgroundColor(Constants.PANEL_BACKGROUND_COLOR);
        
        mContainerParams = new LayoutParams();
        mContainerParams.width = LayoutParams.MATCH_PARENT;
        mContainerParams.height = LayoutParams.MATCH_PARENT;
        mContainerParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        mContainerParams.format = PixelFormat.RGBA_8888;
        mContainerParams.flags = (LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL |
                LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //mContainerParams.gravity = Gravity.CENTER;
        mContainerParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        
        mLeatherStatus = STATUS_LEATHER_CLOSED;
        
        mPanelWidth = mContext.getResources().getDimensionPixelSize(R.dimen.main_panel_width);
        mPanelHeight = mContext.getResources().getDimensionPixelSize(R.dimen.main_panel_height);
        mPanelX = mContext.getResources().getDimensionPixelSize(R.dimen.main_panel_margin_left);
        mPanelY = mContext.getResources().getDimensionPixelSize(R.dimen.main_panel_margin_top);
        
        // match_parent
        mPanelWidth = -1;
        mPanelX = 0;
        
        mScreenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mScreenReceiver, filter);
    }
    
    public void openLeather() {
        Logger.i(KOMA_TAG, "nthpower[openLeather]status:%d", mLeatherStatus);
        if (mLeatherStatus == STATUS_LEATHER_CLOSED) {
            mWindowManager.addView(mContainerView, mContainerParams);
            wakeUp();
            if (mInCallingManager.getCallStatus() != 
                    InCallingManager.CallState.NONE) {
                Logger.i(KOMA_TAG, "InCallingManager.CallState.NONE : ");
                if (mCallPanel == null) {
                    mCallPanel = new CallPanel(mContext);
                }
                mContainerView.replace(mCallPanel);
                closeBackgroundActivity();
            } else {
                if (mMasterPanel == null) {
                    mMasterPanel = new MasterPanel(mContext);
                }
                mContainerView.replace(mMasterPanel);
            }
            
            mContainerView.setSize(mPanelWidth, mPanelHeight);
            mContainerView.setPoint(mPanelX, mPanelY);
            mLeatherStatus = STATUS_LEATHER_OPENED;
        }
    }
    
    public void closeLeather() {
        Logger.i(KOMA_TAG, "nthpower[closeLeather]status:%d", mLeatherStatus);
        if (mLeatherStatus == STATUS_LEATHER_OPENED) {
            mWindowManager.removeView(mContainerView);
            mLeatherStatus = STATUS_LEATHER_CLOSED;
        }
    }
    
    public void openCallPanel() {
        if (mLeatherStatus == STATUS_LEATHER_OPENED) {
            if (mCallPanel == null) {
                mCallPanel = new CallPanel(mContext);
            }
            Logger.i(KOMA_TAG, "nthpower[openCallPanel]");
            mContainerView.replace(mCallPanel);
            openBackgroundActivity();
        }
    }
    
    public void closeCallPanel() {
        if (mLeatherStatus == STATUS_LEATHER_OPENED) {
            if (mMasterPanel == null) {
                mMasterPanel = new MasterPanel(mContext);
            }
            Logger.i(KOMA_TAG,  "nthpower[closeCallPanel]");
            mContainerView.replace(mMasterPanel);
        }
    }
    
    public void openBackgroundActivity() {
        Logger.i(TAG, "nthpower[openBackgroundActivity]");
        Intent intent = new Intent(mContext, LeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
    
    public void closeBackgroundActivity() {
        Logger.i(TAG, "nthpower[closeBackgroundActivity]");
        LeatherActivity folioActivity = LeatherActivity.getInstance();
        if (folioActivity != null) {
            folioActivity.finish();
        }
    }
    
    public int getStatus() {
        return mLeatherStatus;
    }
    
    public boolean isOpened() {
        return mLeatherStatus == STATUS_LEATHER_OPENED;
    }
    
    public boolean isHallActive() {
        boolean active = false;

        BufferedReader reader = null;
        try {
//            reader = new BufferedReader(
//                    new FileReader("sys/devices/platform/hall/driver/hall_state"), 128);
            reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream("sys/devices/platform/hall/driver/hall_state"), "UTF-8"), 128);
            if (reader != null) {
                Integer status = Integer.valueOf(reader.readLine());
                if (LeatherReceiver.HALL_STATUS_OPEN == status) {
                    active = true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.i(KOMA_TAG, "nthpower[isHallActive]active:%s", active);
            return active;
        }
    }
    
    private void wakeUp() {
        int timeout = getScreenOffTime();
        
        Logger.i(KOMA_TAG, "nthpower[wakeUp]timeout:%d", timeout);
        if (timeout != 0) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, TAG);
            wakeLock.acquire(timeout);
        }
    }
    
    private int getScreenOffTime() {
        int timeout = 0;
        try {
            timeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return timeout;
    }
    
    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(KOMA_TAG, "nthpower[ScreenReceiver.onReceive]Action:%s", intent.getAction() + "---isHallActive : " + isHallActive());
            if (LeatherData.getInstance().getLeatherStatus(context)) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    if (isHallActive() && mLeatherStatus == STATUS_LEATHER_CLOSED) {
                        //openLeather();
                    }
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if (mLeatherStatus == STATUS_LEATHER_OPENED) {
                        //closeLeather();
                    }
                }
            }
        }
    }
    
}
