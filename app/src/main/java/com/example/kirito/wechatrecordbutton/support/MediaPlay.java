package com.example.kirito.wechatrecordbutton.support;


import android.media.*;
import android.media.AudioManager;
import android.net.rtp.AudioStream;

import java.io.IOException;

/**
 * Created by kirito on 2016.11.10.
 */

public class MediaPlay {
    private static MediaPlayer mp;
    private static boolean isPause;

    public static void playAudio(String path, android.media.MediaPlayer.OnCompletionListener listener) {
        if (mp == null){
            mp = new MediaPlayer();
            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    return false;
                }
            });
        }else {
            mp.reset();
        }

        try {
            mp.setDataSource(path);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //添加OnCompletionListener
            mp.setOnCompletionListener(listener);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onPause(){
        if (mp != null && mp.isPlaying()){
            mp.pause();
            isPause = true;
        }
    }

    public static void onResume(){
        if (mp != null && !mp.isPlaying()){
            mp.start();
            isPause = false;
        }
    }

    public static void onRelease(){
        if (mp != null){
            mp.release();
            mp = null;
        }
    }
}
