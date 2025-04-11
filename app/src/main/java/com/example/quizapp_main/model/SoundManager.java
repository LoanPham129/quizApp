package com.example.quizapp_main.model;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {

    private MediaPlayer mediaPlayer;

    public void play(Context context, int resId, boolean loop) {
        stop(); // Dừng nếu đang phát cái khác
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setLooping(loop);
        mediaPlayer.start();
    }

    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }


}
