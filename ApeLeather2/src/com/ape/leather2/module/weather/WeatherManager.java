package com.ape.leather2.module.weather;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.Calendar;
import java.util.regex.Pattern;

import com.ape.leather2.R;
import com.ape.leather2.module.log.Logger;

/**
 * @author juan.li
 * @date 2015-10-15 14:37:00
 */
public class WeatherManager {

    private static final String TAG = WeatherManager.class.getName();

    private static final String PACKAGENAME_WEATHER_2 = "com.ape.weather";
    private static final String PACKAGENAME_WEATHER_3 = "com.ape.weather3";

    private static final Uri WEATHER2_CONTENT_URI = Uri.parse("content://com.ape.weather/widgets");

    private static final Uri WEATHER3_CONTENT_URI_CITYFLAGS = Uri.parse("content://com.ape.weather3.provider/city_flags");
    private static final Uri WEATHER3_CONTENT_URI_FORECAST = Uri.parse("content://com.ape.weather3.provider/forecast");
    private static final Uri WEATHER3_CONTENT_URI_WEATHER = Uri.parse("content://com.ape.weather3.provider/weather");
    private static final Uri WEATHER3_CONTENT_URI_CITY = Uri.parse("content://com.ape.weather3.provider/city");

    private static final String ACTION_WEATHER2_UPDATE_APP = "com.ape.weather.UPDATE_APP";

    private static final long WEATHER_UPDATE_DELAY      = 1000;

    private static final int MASK_WEATHER_ALL           = 0x0011;
    private static final int MASK_WEATHER_2             = 0x0001;
    private static final int MASK_WEATHER_3             = 0x0010;

    public static final int INVALID_RESID             = -1;
    public static final int INVALID_TEMPERATURE         = Integer.MAX_VALUE;

    private Context mContext;

    private IWeatherCallbacks mCallback;
    private PackageManager mPackageManager;

    private int WEATHER_MASK = 0x0;
    private VERSION mVersion;

    public enum VERSION {
        WEATHER2,
        WEATHER3,
        BOTH,
        NONE
    }

    private int[] WEATHER3_ICON = {
//            R.drawable.ic_cell_weather_na,
//            R.drawable.ic_cell_weather_tornado,
//            R.drawable.ic_cell_weather_tropical_storm,
//            R.drawable.ic_cell_weather_hurricane,
//            R.drawable.ic_cell_weather_blustery,
//            R.drawable.ic_cell_weather_light_rain,
//            R.drawable.ic_cell_weather_rain,
//            R.drawable.ic_cell_weather_heavy_rain,
//            R.drawable.ic_cell_weather_showers,
//            R.drawable.ic_cell_weather_thundershowers,
//            R.drawable.ic_cell_weather_thunderstorms,
//            R.drawable.ic_cell_weather_severe_thunderstorms,
//            R.drawable.ic_cell_weather_rain_and_snow,
//            R.drawable.ic_cell_weather_rain_and_hail,
//            R.drawable.ic_cell_weather_light_snow,
//            R.drawable.ic_cell_weather_snow,
//            R.drawable.ic_cell_weather_heavy_snow,
//            R.drawable.ic_cell_weather_snow_showers,
//            R.drawable.ic_cell_weather_snow_and_hail,
//            R.drawable.ic_cell_weather_hail,
//            R.drawable.ic_cell_weather_dust,
//            R.drawable.ic_cell_weather_foggy,
//            R.drawable.ic_cell_weather_haze,
//            R.drawable.ic_cell_weather_sunny,
//            R.drawable.ic_cell_weather_sunny_night,
//            R.drawable.ic_cell_weather_cloudy,
//            R.drawable.ic_cell_weather_cloudy_night
    };
    
    private int[] WEATHER3_STRING = {
            R.string.weather_na,
            R.string.weather_tornado,
            R.string.weather_tropical_storm,
            R.string.weather_hurricane,
            R.string.weather_blustery,
            R.string.weather_light_rain,
            R.string.weather_rain,
            R.string.weather_heavy_rain,
            R.string.weather_showers,
            R.string.weather_thunderstorms,
            R.string.weather_rainstorms,
            R.string.weather_severe_rainstorms,
            R.string.weather_rain_and_snow,
            R.string.weather_snow_and_hail,
            R.string.weather_light_snow,
            R.string.weather_snow,
            R.string.weather_heavy_snow,
            R.string.weather_snow_showers,
            R.string.weather_snow_and_hail,
            R.string.weather_hail,
            R.string.weather_dust,
            R.string.weather_foggy,
            R.string.weather_haze,
            R.string.weather_sunny,
            R.string.weather_sunny_night,
            R.string.weather_cloudy,
            R.string.weather_cloudy_night
  };

    private static WeatherManager sWeatherManager;

    public static WeatherManager getInstance(Context context) {
        if (sWeatherManager == null) {
            sWeatherManager = new WeatherManager(context);
        }
        return sWeatherManager;
    }

    private WeatherManager(Context context) {
        mContext = context;
        initialize();
    }

    public void open() {
        checkVersion();
        if (mVersion == VERSION.WEATHER2) {
            initWeather2();
        } else if (mVersion == VERSION.WEATHER3) {
            initWeather3();
        } else if (mVersion == VERSION.BOTH) {
            Logger.i(TAG, "====>more than one WEATHER APP installed<====");
        } else {
            Logger.i(TAG, "====>WEATHER APP uninstalled<====");
        }
    }

    public void close() {
        if (mVersion == VERSION.WEATHER2) {
            destoryWeather2();
        } else if (mVersion == VERSION.WEATHER3) {
            destoryWeather3();
        } else if (mVersion == VERSION.BOTH) {
            // do nothing
        }
    }

    public Weather getWeather() {
        Weather weather = null;

        checkVersion();
        if (mVersion == VERSION.WEATHER2) {
            weather = getWeather2Info();
        } else if (mVersion == VERSION.WEATHER3) {
            weather = getWeather3Info();
        } else if (mVersion == VERSION.BOTH) {
            Logger.i(TAG, "====>more than one WEATHER APP installed<====");
        } else {
            Logger.i(TAG, "====>WEATHER APP uninstalled<====");
        }

        return weather;
    }

    public void setCallback(IWeatherCallbacks callback) {
        mCallback = callback;
    }

    private void initialize() {
        mPackageManager = mContext.getPackageManager();
    }

    private void checkVersion() {
        Intent weather2Launch = mPackageManager.getLaunchIntentForPackage(PACKAGENAME_WEATHER_2);
        if (weather2Launch != null) {
            WEATHER_MASK |= MASK_WEATHER_2;
            Logger.i(TAG, "[checkVersion]PackageName:com.ape.weather exist");
        } else {
            WEATHER_MASK &= ~MASK_WEATHER_2;
            Logger.i(TAG, "[checkVersion]PackageName:com.ape.weather not exist");
        }

        Intent weather3Launch = mPackageManager.getLaunchIntentForPackage(PACKAGENAME_WEATHER_3);
        if (weather3Launch != null) {
            WEATHER_MASK |= MASK_WEATHER_3;
            Logger.i(TAG, "[checkVersion]PackageName:com.ape.weather3 exist");
        } else {
            WEATHER_MASK &= ~MASK_WEATHER_3;
            Logger.i(TAG, "[checkVersion]PackageName:com.ape.weather3 not exist");
        }

        switch (WEATHER_MASK) {
            case MASK_WEATHER_2:
                mVersion = VERSION.WEATHER2;
                break;

            case MASK_WEATHER_3:
                mVersion = VERSION.WEATHER3;
                break;

            case MASK_WEATHER_ALL:
                mVersion = VERSION.BOTH;
                break;

            default:
                mVersion = VERSION.NONE;
                break;
        }
    }

    public VERSION getVersion() {
        return mVersion;
    }

    public interface IWeatherCallbacks {
        void onWeatherUpdate(int iconRes, int temperature);
    }

    public class Weather {
        private int iconResId;
        private int strResId;
        private int temperature;
        private int tempcLow;
        private int tempcHigh;

        public int getIconResId() {
            return iconResId;
        }

        public void setIconResId(int iconResId) {
            this.iconResId = iconResId;
        }
        
        public int getStrResId() {
            return strResId;
        }
        
        public void setStrResId(int strResId) {
            this.strResId = strResId;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }
        
        public int getTempcLow() {
            return tempcLow;
        }
        
        public void setTempcLow(int tempcLow) {
            this.tempcLow = tempcLow;
        }
        
        public int getTempcHigh() {
            return tempcHigh;
        }
        
        public void setTempcHigh(int tempcHigh) {
            this.tempcHigh = tempcHigh;
        }
    }

    /**
     * for PACKAGENAME_WEATHER_2
     */
    private static final String CURRENTCITY = "isCurrentCity";
    private static final String CONDITION = "condition";
    private static final String TEMPERATURE = "tempC";

    private Weather2Receiver mWeather2Receiver;

    private static final Pattern sIconStorm = Pattern.compile(
            "(thunder|tstms)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconSnow = Pattern.compile(
            "(snow|ice|frost|flurries)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconShower = Pattern.compile(
            "(showers)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconSun = Pattern.compile(
            "(sunny|breezy|clear|Fair)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconClouds = Pattern.compile(
            "(cloud|overcast)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconPartlyCloudy = Pattern.compile(
            "(partly cloudy|mostly sunny)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconMostCloudy = Pattern.compile(
            "(mostly cloudy)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconLightrain = Pattern.compile(
            "(light rain|drizzle)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconChanceOfRain = Pattern.compile(
            "(chance of rain|showers)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconHeavyrain = Pattern.compile(
            "(heavy rain)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconRain = Pattern.compile(
            "(rain|storm)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconHaze = Pattern.compile(
            "(haze|Mist)", Pattern.CASE_INSENSITIVE);
    private static final Pattern sIconFog = Pattern.compile(
            "(fog|Fog)", Pattern.CASE_INSENSITIVE);


    private void initWeather2() {
        Logger.i(TAG, "[initWeather2]");
        if (mWeather2Receiver == null) {
            mWeather2Receiver = new Weather2Receiver();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_WEATHER2_UPDATE_APP);

        mContext.registerReceiver(mWeather2Receiver, filter);
    }

    private Weather getWeather2Info() {
        Weather weather = new Weather();

        Cursor cursor = null;
        ContentResolver resolver = mContext.getContentResolver();
        cursor = resolver.query(WEATHER2_CONTENT_URI, null, null, null, null);
        int temperature = INVALID_TEMPERATURE;
        int tempcLow = INVALID_TEMPERATURE;
        int tempcHigh = INVALID_TEMPERATURE;
//        int iconResId = INVALID_RESID;
        int strResId = INVALID_RESID;
        String condition = null;
        if (cursor != null) {
            int mark = -1;
            while (cursor.moveToNext()) {
                mark = cursor.getInt(cursor.getColumnIndex(CURRENTCITY));
                if (mark == 1) {
                    temperature = cursor.getInt(cursor.getColumnIndex(TEMPERATURE));
                    tempcLow = cursor.getInt(cursor.getColumnIndex("low"));
                    tempcHigh = cursor.getInt(cursor.getColumnIndex("hight"));
                    condition = cursor.getString(cursor.getColumnIndex(CONDITION));
                    break;
                }
            }
            cursor.close();
        }

//        iconResId = getWeather2Icon(condition);
//        weather.setIconResId(iconResId);
        weather.setTemperature(temperature);
        weather.setTempcLow(tempcLow);
        weather.setTempcHigh(tempcHigh);
        strResId = getWeather2String(condition);
        weather.setStrResId(strResId);

        return weather;
    }

    private void destoryWeather2() {
        Logger.i(TAG, "[destoryWeather2]");
        if (mWeather2Receiver != null) {
            mContext.unregisterReceiver(mWeather2Receiver);
            mWeather2Receiver = null;
        }
    }

    private static final int DAYTIME_BEGIN_HOUR = 6;
    private static final int DAYTIME_END_HOUR = 18;
    public boolean isDaytime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return (hour >= DAYTIME_BEGIN_HOUR && hour <= DAYTIME_END_HOUR);
    }

    private int[] WEATHER2_ICON = {
//            R.drawable.ic_cell_weather_na,
//            R.drawable.ic_cell_weather_light_rain,
//            R.drawable.ic_cell_weather_rain,
//            R.drawable.ic_cell_weather_heavy_rain,
//            R.drawable.ic_cell_weather_showers,
//            R.drawable.ic_cell_weather_thunderstorms,
//            R.drawable.ic_cell_weather_heavy_snow,
//            R.drawable.ic_cell_weather_foggy,
//            R.drawable.ic_cell_weather_haze,
//            R.drawable.ic_cell_weather_sunny,
//            R.drawable.ic_cell_weather_sunny_night,
//            R.drawable.ic_cell_weather_cloudy,
//            R.drawable.ic_cell_weather_cloudy_night
    };
    
//    private int getWeather2Icon(String condition) {
//        int icon;
//
//        if (condition == null) {
//            icon = R.drawable.ic_cell_weather_na;
//        } else if (sIconStorm.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_thunderstorms;
//        } else if (sIconSnow.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_heavy_snow;
//        } else if (sIconLightrain.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_light_rain;
//        } else if (sIconShower.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_showers;
//        } else if (sIconPartlyCloudy.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_cloudy;
//        } else if (sIconMostCloudy.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_cloudy;
//        } else if (sIconSun.matcher(condition).find()) {
//            icon = isDaytime() ? R.drawable.ic_cell_weather_sunny
//                    : R.drawable.ic_cell_weather_sunny_night;
//        } else if (sIconClouds.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_cloudy;
//        } else if (sIconHeavyrain.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_heavy_rain;
//        } else if (sIconRain.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_rain;
//        } else if (sIconHaze.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_haze;
//        } else if (sIconFog.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_foggy;
//        } else if (sIconChanceOfRain.matcher(condition).find()) {
//            icon = R.drawable.ic_cell_weather_rain;
//        } else{
//            icon = R.drawable.ic_cell_weather_na;
//        }
//        return icon;
//    }


    private int[] WEATHER2_STRING = {
            R.string.weather_na,
            R.string.weather_rainstorms,
            R.string.weather_snow,
            R.string.weather_showers,
            R.string.weather_sunny,
            R.string.weather_cloudy,
            R.string.weather_cloudy,
            R.string.weather_cloudy,
            R.string.weather_light_rain,
            R.string.weather_showers,
            R.string.weather_heavy_rain,
            R.string.weather_rain,
            R.string.weather_haze,
            R.string.weather_foggy
  };
    
    private int getWeather2String(String condition) {
        int string;
        
        if (condition == null) {
            string = R.string.weather_na;
        } else if (sIconStorm.matcher(condition).find()) {
            string = R.string.weather_rainstorms;
        } else if (sIconSnow.matcher(condition).find()) {
            string = R.string.weather_snow;
        } else if (sIconLightrain.matcher(condition).find()) {
            string = R.string.weather_light_rain;
        } else if (sIconShower.matcher(condition).find()) {
            string = R.string.weather_showers;
        } else if (sIconPartlyCloudy.matcher(condition).find()) {
            string = R.string.weather_cloudy;
        } else if (sIconMostCloudy.matcher(condition).find()) {
            string = R.string.weather_cloudy;
        } else if (sIconSun.matcher(condition).find()) {
            string = R.string.weather_sunny;
        } else if (sIconClouds.matcher(condition).find()) {
            string = R.string.weather_cloudy;
        } else if (sIconHeavyrain.matcher(condition).find()) {
            string = R.string.weather_heavy_rain;
        } else if (sIconRain.matcher(condition).find()) {
            string = R.string.weather_rain;
        } else if (sIconHaze.matcher(condition).find()) {
            string = R.string.weather_haze;
        } else if (sIconFog.matcher(condition).find()) {
            string = R.string.weather_foggy;
        } else if (sIconChanceOfRain.matcher(condition).find()) {
            string = R.string.weather_rain;
        } else {
            string = R.string.weather_na;
        }
        return string;
    }
    
    private class Weather2Receiver extends BroadcastReceiver {

        private long lastUpdateTime;

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(TAG, "[Weather2Receiver.onReceive]Action:%s", intent.getAction());
            if (ACTION_WEATHER2_UPDATE_APP.equals(intent.getAction()) &&
                    mCallback != null) {
                long now = System.currentTimeMillis();
                if (Math.abs(now - lastUpdateTime) < WEATHER_UPDATE_DELAY) {
                    return;
                }
                int temperature = intent.getIntExtra("tempc", INVALID_TEMPERATURE);
                String condition = intent.getStringExtra("condition");

                int iconResId = -1; //getWeather2Icon(condition);
                mCallback.onWeatherUpdate(iconResId, temperature);

                lastUpdateTime = now;
            }
        }
    }

    /**
     * for PACKAGENAME_WEATHER_3
     */

    private ContentResolver mResolver;

    private void initWeather3() {
        Logger.i(TAG, "[initWeather3]");
        mResolver = mContext.getContentResolver();
        mResolver.registerContentObserver(WEATHER3_CONTENT_URI_CITYFLAGS, true, mWeather3Observer);
        mResolver.registerContentObserver(WEATHER3_CONTENT_URI_WEATHER, true, mWeather3Observer);
        mResolver.registerContentObserver(WEATHER3_CONTENT_URI_CITY, true, mWeather3Observer);
    }

    public void destoryWeather3() {
        Logger.i(TAG, "[destoryWeather3]");
        mResolver.unregisterContentObserver(mWeather3Observer);
    }

    private ContentObserver mWeather3Observer = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            String city = getCurrentCity();
            Weather weather = getCityWeather(city);

            if (mCallback != null) {
                mCallback.onWeatherUpdate(weather.getIconResId(), weather.getTemperature());
            }
        }
    };

    private Weather getWeather3Info() {
        return getCityWeather(getCurrentCity());
    }

    private String getCurrentCity() {
        String id = null;

        ContentResolver resolver = mContext.getContentResolver();
        String[] projection = new String[] {"current_city"};

        Cursor cursor = resolver.query(WEATHER3_CONTENT_URI_CITYFLAGS, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(0) != 0) {
                id = String.valueOf(cursor.getInt(0));
            }
            cursor.close();
        }

        Logger.i(TAG, "[getCurrentCity]city:%s", id);
        return id;
    }

    private Weather getCityWeather(String city) {
        Weather weather = new Weather();

        int temperature = INVALID_TEMPERATURE;
        int tempcLow = INVALID_TEMPERATURE;
        int tempcHigh = INVALID_TEMPERATURE;
//        int iconResId = INVALID_RESID;
        int strResId = INVALID_RESID;
        String condition = null;
        int weatherId = -1;
        int forecastId = -1;

        if (city != null) {
            ContentResolver resolver = mContext.getContentResolver();

            Cursor cursor = resolver.query(WEATHER3_CONTENT_URI_CITY,
                    null, "_id=?", new String[]{city}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                weatherId = cursor.getInt(cursor.getColumnIndex("weather_id"));
                forecastId = cursor.getInt(cursor.getColumnIndex("forecast0"));
                Cursor c = resolver.query(WEATHER3_CONTENT_URI_WEATHER,
                        null, "_id=?", new String[] {String.valueOf(weatherId)}, null);
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    condition = c.getString(c.getColumnIndex("condition"));
                    temperature = c.getInt(c.getColumnIndex("temperature"));
                    c.close();
                }

                cursor.close();
                cursor = null;
            }
            
            Cursor forecast = resolver.query(WEATHER3_CONTENT_URI_FORECAST,
                    null, "_id=?", new String[]{String.valueOf(forecastId)}, null);
            Logger.i(TAG, "[getCityWeather]cursor:%s, count:%d", forecast, forecast.getCount());
            if (forecast != null && forecast.getCount() > 0) {
                forecast.moveToFirst();
                tempcLow = forecast.getInt(forecast.getColumnIndex("low"));
                tempcHigh = forecast.getInt(forecast.getColumnIndex("high"));
                
                Logger.i(TAG, "[getCityWeather]low:%d, high:%d",
                        tempcLow, tempcHigh);
            }
            if (forecast != null) {
                forecast.close();
                forecast = null;
            }
        }
        
        Logger.i(TAG, "[getCityWeather]temperature:%d, low:%d, high:%d",
                temperature, tempcLow, tempcHigh);
//        iconResId = getWeather3Icon(condition);
//        weather.setIconResId(iconResId);
        weather.setTemperature(temperature);
        weather.setTempcLow(tempcLow);
        weather.setTempcHigh(tempcHigh);

        strResId = getWeather3String(condition);
        weather.setStrResId(strResId);
        return weather;
    }

    private int getWeather3Icon(String condition) {
        int type = 0;
        if (condition != null) {
            type = Integer.valueOf(condition);
        }
        return WEATHER3_ICON[type];
    }
    
    private int getWeather3String(String condition) {
        int type = 0;
        if (condition != null) {
            type = Integer.valueOf(condition);
        }
        return WEATHER3_STRING[type];
    }
}
