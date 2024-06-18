package com.group2.catan_android;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.catan_android.util.GameEffectManager;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        setToFullScreen();

        ImageView logo = findViewById(R.id.logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.grow_animation));

        Button lobby = findViewById(R.id.lobbyActivityButton);
        lobby.setOnClickListener(v -> navigate(lobbyActivity.class));
        lobby.startAnimation(AnimationUtils.loadAnimation(this,R.anim.blink_animation));

    }

    private void setToFullScreen() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void navigate(Class<?> cl){
        Intent i = new Intent(getApplicationContext(), cl);
        startActivity(i);
    }
}
