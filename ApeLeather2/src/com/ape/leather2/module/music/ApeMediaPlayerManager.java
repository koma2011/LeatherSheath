package com.ape.leather2.module.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ape.leather2.module.log.Logger;
import com.ape.music.IMediaPlaybackService;

/**
 * @author juan.li
 * @date 2015-11-13 18:13:00
 */
public class ApeMediaPlayerManager extends AbsMediaPlayerManager {

    private static final String TAG = ApeMediaPlayerManager.class.getName();

    public static final String MEDIA_PACKAGE = "com.ape.music";
    public static final String MEDIA_SERVICE = "com.ape.music.MediaPlaybackService";

    private IMediaPlaybackService mMediaPlayService;
    private boolean mIsRemoteBind = false;

    public ApeMediaPlayerManager(Context context) {
        super(context);
    }

    @Override
    public void bindService() {
        if (mMediaPlayService == null) {
            Intent media = new Intent(MEDIA_SERVICE);
            media.setPackage(MEDIA_PACKAGE);
            mContext.startService(media);

            Intent service = new Intent(MEDIA_SERVICE);
            service.setPackage(MEDIA_PACKAGE);
            mIsRemoteBind = mContext.bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
            Logger.i(TAG, "[bindMediaService]media player service bind:%s, action:%s, packageName:%s",
                    mIsRemoteBind, MEDIA_SERVICE, MEDIA_PACKAGE);
        }
    }

    @Override
    public void unbindService() {
        if (mMediaPlayService != null && mIsRemoteBind) {
            mContext.unbindService(mServiceConnection);
            mMediaPlayService = null;
            mIsRemoteBind = false;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayService != null) {
            try {
                return mMediaPlayService.isPlaying();
            } catch (RemoteException e) {
                Logger.e(TAG, "[isPlaying]ERROR:" + e);
            }
        }
        return false;
    }

    @Override
    public void pause() {
        if (mMediaPlayService != null) {
            try {
                mMediaPlayService.pause();
            } catch (RemoteException e) {
                Logger.e(TAG, "[isPlaying]ERROR:" + e);
            }
        }
    }

    @Override
    public void play() {
        if (mMediaPlayService != null) {
            try {
                mMediaPlayService.play();
            } catch (RemoteException e) {
                Logger.e(TAG, "[isPlaying]ERROR:" + e);
            }
        }
    }

    @Override
    public void prev() {
        if (mMediaPlayService != null) {
            try {
                mMediaPlayService.prev();
            } catch (RemoteException e) {
                Logger.e(TAG, "[isPlaying]ERROR:" + e);
            }
        }
    }

    @Override
    public void next() {
        if (mMediaPlayService != null) {
            try {
                mMediaPlayService.next();
            } catch (RemoteException e) {
                Logger.e(TAG, "[isPlaying]ERROR:" + e);
            }
        }
    }
    
    @Override
    public String getTrack() {
        if (mMediaPlayService != null) {
            try {
                return mMediaPlayService.getTrackName();
            } catch (RemoteException e) {
                Logger.e(TAG, "[getTrack]ERROR:" + e);
            }
        }
        return null;
    }
    
    @Override
    public String getArtist() {
        if (mMediaPlayService != null) {
            try {
                return mMediaPlayService.getArtistName();
            } catch (RemoteException e) {
                Logger.e(TAG, "[getArtist]ERROR:" + e);
            }
        }
        return null;
    }
    
    @Override
    public long position() {
        if (mMediaPlayService != null) {
            try {
                return mMediaPlayService.position();
            } catch (RemoteException e) {
                Logger.e(TAG, "[getArtist]ERROR:" + e);
            }
        }
        return 0;
    }
    
    @Override
    public long duration() {
        if (mMediaPlayService != null) {
            try {
                return mMediaPlayService.duration();
            } catch (RemoteException e) {
                Logger.e(TAG, "[getArtist]ERROR:" + e);
            }
        }
        return 0;
    }
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaPlayService = IMediaPlaybackService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMediaPlayService = null;
        }
    };
}
