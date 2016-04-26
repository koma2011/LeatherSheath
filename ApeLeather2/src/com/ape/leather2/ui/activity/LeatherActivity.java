package com.ape.leather2.ui.activity;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;
import com.ape.leather2.ui.LeatherViewManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class LeatherActivity extends BaseActivity {

    private static final String TAG = LeatherActivity.class.getName();
    private static final String KOMA_TAG = "KomaLeatherSheath";
    
    private static LeatherActivity sLeatherActivity = null;
    
    private Button mOpenButton;
    
    private LeatherViewManager mLeatherViewManager;
    
    public static LeatherActivity getInstance() {
        return sLeatherActivity;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.i(KOMA_TAG, TAG + "[onCreate]LeatherActivity created~~");
        sLeatherActivity = this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leather);
        
        mLeatherViewManager = LeatherViewManager.getInstance(mContext);

        /*mOpenButton = (Button) findViewById(R.id.open);
        mOpenButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mLeatherViewManager.openLeather();
            }
        });*/
        mLeatherViewManager.openLeather();//add by MJ
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD |
                LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    
    @Override
    public void onBackPressed() {
        if (mLeatherViewManager.isOpened()) {
            mLeatherViewManager.closeLeather();
        } else {
            super.onBackPressed();
        }
    }
}
