package com.ape.leather2.module.telephone;

import com.ape.leather2.module.log.Logger;

import android.os.Handler;
import android.os.SystemClock;

/**
 * @author juan.li
 * @date 2015-09-11 09:29:00
 */
public class CallTimer extends Handler {

    private static final String TAG = CallTimer.class.getName();

    private Runnable mInternalCallback;
    private Runnable mCallback;
    private long mLastReportedTime;
    private long mInterval;
    private boolean mRunning;

    public CallTimer(Runnable callback) {
        if (callback == null) {
            throw new NullPointerException();
        }

        mInterval = 0;
        mLastReportedTime = 0;
        mRunning = false;
        mCallback = callback;
        mInternalCallback = new CallTimerCallback();
    }

    public boolean start(long interval) {
        if (interval <= 0) {
            return false;
        }

        cancel();

        mInterval = interval;
        mLastReportedTime = SystemClock.uptimeMillis();

        mRunning = true;
        Logger.i(TAG, "[start]start timer");
        periodicUpdateTimer();
        return true;
    }

    public void cancel() {
        Logger.i(TAG, "[cancel]cancel timer");
        removeCallbacks(mInternalCallback);
        mRunning = false;
    }

    private void periodicUpdateTimer() {
        if (!mRunning) {
            return;
        }

        final long now = SystemClock.uptimeMillis();
        long nextReport = mLastReportedTime + mInterval;
        while (now >= nextReport) {
            nextReport += mInterval;
        }

        postAtTime(mInternalCallback, nextReport);
        mLastReportedTime = nextReport;

        mCallback.run();
    }

    private class CallTimerCallback implements Runnable {

        @Override
        public void run() {
            periodicUpdateTimer();
        }
    }
}
