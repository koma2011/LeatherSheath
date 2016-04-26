package com.ape.leather2.module.telephone;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ape.leather2.module.log.Logger;

/**
 * @author juan.li
 * @date 2015-09-10 14:55:00
 */
@SuppressWarnings("unchecked")
public class TelephonyReflect {

    private static final String TAG = TelephonyReflect.class.getName();

    private static TelephonyReflect sTelephonyReflect;
    private TelephonyManager mTelephonyManager;
    @SuppressWarnings("rawtypes")
    private Class mClazz;

    public static TelephonyReflect getInstance(Context context) {
        if (sTelephonyReflect == null) {
            sTelephonyReflect = new TelephonyReflect(context);
        }
        return sTelephonyReflect;
    }

    private TelephonyReflect(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mClazz = TelephonyManager.class;
    }

    public void answerRingingCall() {
        try {
            Method method = mClazz.getMethod("answerRingingCall");
            method.invoke(mTelephonyManager);
            Logger.i(TAG, "[answerRingingCall]");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void endCall() {
        try {
            Method method = mClazz.getMethod("endCall");
            method.invoke(mTelephonyManager);
            Logger.i(TAG, "[endCall]");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void silenceRinger() {
        try {
            Method method = mClazz.getMethod("silenceRinger");
            method.invoke(mTelephonyManager);
            Logger.i(TAG, "[silenceRinger]");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean isOffhook() {
        try {
            Method method = mClazz.getMethod("isOffhook");
            Logger.i(TAG, "[isOffhook]");
            return (Boolean) method.invoke(mTelephonyManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isRinging() {
        try {
            Method method = mClazz.getMethod("isRinging");
            Logger.i(TAG, "[isRinging]");
            return (Boolean) method.invoke(mTelephonyManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isIdle() {
        try {
            Method method = mClazz.getMethod("isIdle");
            Logger.i(TAG, "[isIdle]");
            return (Boolean) method.invoke(mTelephonyManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}
