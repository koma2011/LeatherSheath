package com.ape.leather2.module.music;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ape.leather2.module.model.Command;
import com.ape.leather2.module.music.AbsMediaPlayerManager.MediaState;

/**
 * @author juan.li
 * @date 2015-10-13 09:27:00
 */
public class MediaPlayerManager {

    private static final String TAG = MediaPlayerManager.class.getName();
    
    private static MediaPlayerManager sMediaPlayerManager;

    private AbsMediaPlayerManager mMediaPlayerManager;
    
    public static MediaPlayerManager getInstance(Context context) {
        if (sMediaPlayerManager == null) {
            sMediaPlayerManager = new MediaPlayerManager(context);
        }
        return sMediaPlayerManager;
    }

    private MediaPlayerManager(Context context) {
        mMediaPlayerManager = getMediaPlayer(context);
    }

    private AbsMediaPlayerManager getMediaPlayer(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent launch = packageManager.getLaunchIntentForPackage(ApeMediaPlayerManager.MEDIA_PACKAGE);
        if (launch != null) {
            return new ApeMediaPlayerManager(context);
        } else {
            launch = packageManager.getLaunchIntentForPackage(AndroidMediaPlayerManager.MEDIA_PACKAGE);
            if (launch != null) {
                return new AndroidMediaPlayerManager(context);
            }
        }
        return null;
    }
    
    public void setCallback(IMediaPlayerCallbacks callback) {
        mMediaPlayerManager.setCallback(callback);
    }
    
    public void open() {
        mMediaPlayerManager.open();
    }
    
    public void close() {
        mMediaPlayerManager.close();
    }
    
    public boolean isPlaying() {
        return mMediaPlayerManager.isPlaying();
    }
    
    public void pause() {
        mMediaPlayerManager.pause();
    }
    
    public void play() {
        mMediaPlayerManager.play();
    }
    
    public void prev() {
        mMediaPlayerManager.prev();
    }
    
    public void next() {
        mMediaPlayerManager.next();
    }
    
    public long position() {
        return mMediaPlayerManager.position();
    }
    
    public long duration() {
        return mMediaPlayerManager.duration();
    }
    
    public String getTrack() {
        return mMediaPlayerManager.getTrack();
    }
    
    public String getArtist() {
        return mMediaPlayerManager.getArtist();
    }
    
    public Command getCommand(MediaState state) {
        return mMediaPlayerManager.getCommand(state);
    }
    
    public int getIcon(MediaState state) {
        return mMediaPlayerManager.getIcon(state);
    }
}
