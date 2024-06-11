package com.group2.catan_android.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.group2.catan_android.R;

import java.util.HashMap;


public class GameEffectManager {
    private final Vibrator vibrator;
    private final SoundPool soundPool;
    private final Context context;
    private final HashMap<Integer, Integer> soundMap;

    public GameEffectManager(Context context){
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();
        soundMap = new HashMap<>();
    }

    public void loadSound(int soundResourceID){
        int soundID = soundPool.load(context, soundResourceID, 1);
        soundMap.put(soundResourceID, soundID);
    }
    public void playSound(int soundResourceID){
       Integer  soundID = soundMap.get(soundResourceID);
       if(soundID != null)
            soundPool.play(soundID, 1, 1, 0, 0, 1);
       else
           Log.d("GameEffects", "Sound not played. Please load it first");
    }

    public void vibrate(){
        vibrate(100, VibrationEffect.DEFAULT_AMPLITUDE);
    }

    public void vibrate(int duration, int intensity){
        if(vibrator != null && vibrator.hasVibrator()){
            vibrator.vibrate(VibrationEffect.createOneShot(duration, intensity));
        }
    }

    public void vibrate(VibrationEffect vibrationEffect){
        vibrator.vibrate(vibrationEffect);
    }

    public void doubleVibrate(){
        long[] pattern = {0, 100, 100, 100};
        vibrate(VibrationEffect.createWaveform(pattern, -1));
    }

    /**
     * To be called on Activities onDestroy()
     */
    public void release(){
        soundPool.release();
    }
}
