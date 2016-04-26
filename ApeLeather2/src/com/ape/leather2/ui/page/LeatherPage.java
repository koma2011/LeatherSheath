package com.ape.leather2.ui.page;

import java.util.List;

import com.ape.leather2.R;
import com.ape.leather2.module.mms.MmsManager;
import com.ape.leather2.module.mms.MmsManager.IMmsCallback;
import com.ape.leather2.module.telephone.CallInfo;
import com.ape.leather2.module.telephone.MissedCallManager;
import com.ape.leather2.module.telephone.MissedCallManager.IMissedCallback;
import com.ape.leather2.module.time.DateTimeManager;
import com.ape.leather2.module.time.DateTimeManager.IDateTimeCallback;
import com.ape.leather2.module.weather.WeatherManager;
import com.ape.leather2.module.weather.WeatherManager.IWeatherCallbacks;
import com.ape.leather2.module.weather.WeatherManager.Weather;
import com.ape.leather2.utility.LeatherUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author juan.li
 * @date Dec 3, 2015 11:13:28 AM
 */
public class LeatherPage extends AbsLeatherPage implements
        IWeatherCallbacks, IMmsCallback, IMissedCallback, IDateTimeCallback {
    
    private static final String TAG = LeatherPage.class.getName();
    
    private TextView mTimeView;
    private TextView mTimeAPView;
    private TextView mDateView;
    private TextView mWeekView;
    private TextView mWeatherView;
    private TextView mTempeView;
    private ImageView mMissedImage;
    private ImageView mUnreadImage;
    private View mMissedLayout;
    private View mUnreadLayout;
    
    private DateTimeManager mDateTimeManager;
    private WeatherManager mWeatherManager;
    private MmsManager mMmsManager;
    private MissedCallManager mMissedCallManager;
    
    public LeatherPage(Context context) {
        super(context);
    }
    
    @Override
    public View onCreate(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_leather_layout, null);
        
        mTimeView = (TextView) view.findViewById(R.id.time);
        mTimeAPView = (TextView) view.findViewById(R.id.ampm);
        mDateView = (TextView) view.findViewById(R.id.date);
        mWeekView = (TextView) view.findViewById(R.id.week);
        mWeatherView = (TextView) view.findViewById(R.id.weather);
        mTempeView = (TextView) view.findViewById(R.id.temperature);
        mMissedImage = (ImageView) view.findViewById(R.id.missed_call);
        mUnreadImage = (ImageView) view.findViewById(R.id.unread_message);
        mMissedLayout = view.findViewById(R.id.missed_call_layout);
        mUnreadLayout = view.findViewById(R.id.unread_message_layout);
        
        mWeatherManager = WeatherManager.getInstance(mContext);
        mMmsManager = MmsManager.getInstance(mContext);
        mMissedCallManager = MissedCallManager.getInstance(mContext);
        mDateTimeManager = DateTimeManager.getInstance(mContext);
        
        mWeatherManager.setCallback(this);
        mMmsManager.setCallback(this);
        mMissedCallManager.addMissedCallback(this);;
        mDateTimeManager.setCallback(this);
        
        mWeatherManager.open();
        mMmsManager.open();
        mMissedCallManager.open();
        mDateTimeManager.open();
        
        updateTime();
        updateDate();
        updateWeather();
        updateUnread();
        updateMissed();
        return view;
    }
    
//    @Override
//    public void onDestory() {
//    }
    
    @Override
    public String getTag() {
        return TAG;
    }
    
    @Override
    public void onWeatherUpdate(int iconRes, int temperature) {
        updateWeather();
    }
    
    @Override
    public void onMissedCallUpdate(List<CallInfo> callInfo) {
        updateMissed();
    }
    
    @Override
    public void onMmsUnreadUpdate(int unread) {
        updateUnread();
    }
    
    @Override
    public void onDateUpdate(int month, int dayofMonth) {
        updateDate();
    }
    
    @Override
    public void onTimeUpdate(String hour, String minute) {
        updateTime();
    }
    
    private void updateTime() {
        String time = mDateTimeManager.getTime();
        mTimeView.setText(time);
        
        String ampm = mDateTimeManager.getAmPm();
        if (ampm != null) {
            mTimeAPView.setVisibility(View.VISIBLE);
            mTimeAPView.setText(ampm);
        } else {
            mTimeAPView.setVisibility(View.GONE);
        }
    }
    
    private void updateDate() {
        String date = mDateTimeManager.getDate();
        mDateView.setText(date);
        
        String week = mDateTimeManager.getWeek();
        mWeekView.setText(week);
    }
    
    private void updateWeather() {
        Weather weather = mWeatherManager.getWeather();
        
        if (weather.getTemperature() == WeatherManager.INVALID_TEMPERATURE) {
            mTempeView.setVisibility(View.GONE);
            mWeatherView.setVisibility(View.GONE);
        } else {
            String temp = String.format("%s℃/%s℃", weather.getTempcLow(), weather.getTempcHigh());
            mWeatherView.setText(mContext.getString(weather.getStrResId()));
            mTempeView.setText(temp);
            mTempeView.setVisibility(View.VISIBLE);
            mWeatherView.setVisibility(View.VISIBLE);
        }
    }
    
    private void updateUnread() {
        int count = mMmsManager.getUnread();
        if (count != 0) {
            mUnreadImage.setImageResource(LeatherUtil.getImageResource(count));
            mUnreadLayout.setVisibility(View.VISIBLE);
        } else {
            mUnreadLayout.setVisibility(View.GONE);
        }
    }
    
    private void updateMissed() {
        int count = mMissedCallManager.getMissedCall();
        if (count != 0) {
            mMissedImage.setImageResource(LeatherUtil.getImageResource(count));
            mMissedLayout.setVisibility(View.VISIBLE);
        } else {
            mMissedLayout.setVisibility(View.GONE);
        }
    }
    
//    private int getImageResource(int index) {
//        int result = 0;
//        if (1 == index) {
//            result = R.drawable.unread_1;
//        } else if (2 == index) {
//            result = R.drawable.unread_2;
//        } else if (3 == index) {
//            result = R.drawable.unread_3;
//        } else if (4 == index) {
//            result = R.drawable.unread_4;
//        } else if (5 == index) {
//            result = R.drawable.unread_5;
//        } else if (6 == index) {
//            result = R.drawable.unread_6;
//        } else if (7 == index) {
//            result = R.drawable.unread_7;
//        } else if (8 == index) {
//            result = R.drawable.unread_8;
//        } else if (9 == index) {
//            result = R.drawable.unread_9;
//        } else if (index > 9) {
//            result = R.drawable.unread_9plus;
//        }
//        return result;
//    }
}
