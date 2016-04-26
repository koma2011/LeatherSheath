package com.ape.leather2.module.model;

import com.ape.leather2.module.log.Logger;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * @author juan.li
 * @date 2015-10-13 21:56:00
 */
public class Command {

    private static final String TAG = Command.class.getName();

    private Handler handler;

    private int what;

    private Intent intent;

    private int elapse = 0;

    public Command(int what) {
        this.what = what;
    }

    public Command(Handler handler, int what) {
        this.handler = handler;
        this.what = what;
    }

    public Command add(Intent intent) {
        this.intent = intent;
        return this;
    }

    public Command add(Handler handler) {
        this.handler = handler;
        return this;
    }

    public Command setElapse(int elapse) {
        this.elapse = elapse;
        return this;
    }

    public int getWhat() {
        return what;
    }

    public Handler getHandler() {
        return handler;
    }

    public void execute() {
        execute(this.handler);
    }

    private long lastTime = 0;
    public void execute(Handler handler) {
        if (this.handler == null && handler == null) {
            throw new IllegalArgumentException("Handler must be set~~");
        }

        if (this.handler != handler) {
            this.handler = handler;
        }

        long now = SystemClock.elapsedRealtime();
        if (Math.abs(now - lastTime) > elapse) {
            if (intent != null) {
                Message message = this.handler.obtainMessage();
                message.what = what;
                message.obj = intent;
                this.handler.sendMessage(message);
            } else {
                this.handler.sendEmptyMessage(what);
            }
        } else {
            Logger.i(TAG, "[execute]Operation time is less than elapse:%d", elapse);
        }
        lastTime = now;
    }
}
