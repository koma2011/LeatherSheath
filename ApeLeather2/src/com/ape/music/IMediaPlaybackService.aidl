// IMediaPlaybackService.aidl
package com.ape.music;

// Declare any non-default types here with import statements

interface IMediaPlaybackService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void openFile(String path);
        void open(in long [] list, int position);
        int getQueuePosition();
        boolean isPlaying();
        void stop();
        void pause();
        void play();
        void prev();
        void next();
        long duration();
        long position();
        long seek(long pos);
        void playAll();
        String getTrackName();
        String getAlbumName();
        long getAlbumId();
        String getArtistName();
        long getArtistId();
        void enqueue(in long [] list, int action);
        long [] getQueue();
        void moveQueueItem(int from, int to);
        void setQueuePosition(int index);
        String getPath();
        long getAudioId();
        void setShuffleMode(int shufflemode);
        int getShuffleMode();
        int removeTracks(int first, int last);
        int removeTrack(long id);
        void setRepeatMode(int repeatmode);
        int getRepeatMode();
        int getMediaMountedCount();
        int getAudioSessionId();
        void setPlayingMode(boolean playingMode);
        boolean getPlayingMode();
        void onlineStop();
        void onlineReset();
        void downloadMusic(long musicId, String bitrate, int type);
        void downloadOnlineMusic(String bitrate, int type);
        void stopForground();
        boolean hasMounted();
        String getMIMEType();
        int getMusicMaxVolume();
        int getMusicCurrentVolume();
        void setMusicVolume(boolean UIFlag, boolean status);
        void setMusicVolumeForSeek(boolean UIFlag, int volume);
        String getMusicTitle();
        String getMusicAlbum();
        String getMusicArtist();
        int getFocusImageSize();
        List<String> getFocusItemCodeList();
        List<String> getFocusItemUriList();
        List<String> getFocusItemDescriptionList();
//        Bitmap getUrlBitmap(String url, String s);
        int getNetworkType();
        void notifyChange(String what);
        boolean getHeadsetStatus();
}
