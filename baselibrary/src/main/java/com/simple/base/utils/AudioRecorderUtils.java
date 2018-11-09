package com.simple.base.utils;

import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 */
public class AudioRecorderUtils {
    private static final String TAG = "AudioRecorderUtils";
    //文件完整路径
    private String filePath;
    //文件夹路径
    private String folderPath;
    //文件名称
    private String fileName;
    //录音最长时间（单位：毫秒）
    private int maxDuration;

    private MediaRecorder mMediaRecorder;

    // 记录是否正在进行录制
    boolean isRecording = false;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    /**
     * 文件存储默认sdcard/cadyd/record
     *
     * @param folderPath  保存文件的文件夹路径
     * @param fileName    文件名称（不包含后缀）录音默认格式为ACC，所以文件名称后缀就是".acc "，不需要设置。
     * @param maxDuration 录音最长时间（单位：毫秒）
     */

    public AudioRecorderUtils(@NonNull String folderPath, @NonNull String fileName, int maxDuration) {
        File path = new File(folderPath);
        if (!path.exists())
            path.mkdirs();
        this.folderPath = folderPath;
        this.fileName = fileName;
        if (maxDuration < 0) {
            throw new IllegalArgumentException("maxDuration 不能小于0");
        }
        this.maxDuration = maxDuration;
    }

    private long startTime;


    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {

        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            filePath = folderPath + fileName + ".aac";
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setAudioEncodingBitRate(8);
            mMediaRecorder.setMaxDuration(maxDuration);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            isRecording = true;
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.i("fan", "startTime" + startTime);
        } catch (IllegalStateException e) {
            audioStatusUpdateListener.onError();
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            audioStatusUpdateListener.onError();
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (!isRecording || mMediaRecorder == null)
            return 0L;
        long endTime = System.currentTimeMillis();
        //设置后不会崩
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
            isRecording = false;
        } catch (IllegalStateException e) {
            Log.d("stopRecord", e.getMessage());
        } catch (RuntimeException e) {
            Log.d("stopRecord", e.getMessage());
        } catch (Exception e) {
            Log.d("stopRecord", e.getMessage());
        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        int time = (int) (endTime - startTime);
        audioStatusUpdateListener.onStop(time, filePath);
        filePath = "";

        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        if (!isRecording || mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRecording = false;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        filePath = "";

    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        int SPACE = 100;// 间隔取样时间
        int BASE = 1;
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param time     录音时长
         * @param filePath 保存路径
         */
        public void onStop(int time, String filePath);

        /**
         * 录音失败
         */
        public void onError();
    }

}
