package com.simple.base.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

/**
 * 作者：Rance on 2016/12/15 15:11
 * 邮箱：rance935@163.com
 */
public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;
    private MediaStopListener listener;
    private static MediaPlayerManager instance;

    private MediaPlayerManager() {
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public MediaStopListener getListener() {
        return listener;
    }

    public void setMediaStopListener(MediaStopListener listener) {
        this.listener = listener;
    }

    /**
     * 播放音乐
     *
     * @param filePath
     * @param onCompletionListener
     */
    public void playSound(final String filePath, final OnCompletionListener onCompletionListener, OnErrorListener onErrorListener) {
        // String newfilePath="/storage/emulated/0/xiniu/record/CgAHEVtVcTmAPylKAABmrRTN8NU816.aac";
        Log.d(TAG, "playSound() called with: filePath = [" + filePath);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });

        } catch (Exception e) {
            onErrorListener.onError(mMediaPlayer, -1, -1);
            Log.e(TAG, "playSound:filePath: " + filePath, e);
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.stop();
            if (listener != null) {
                listener.onStop();
            }
        }
    }


    /**
     * 当前是isPause状态
     */
    public void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (listener != null) {
            listener = null;
        }
    }


    public interface MediaStopListener {
        void onStop();
    }

}
