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
import android.widget.Button;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button demoBoard = findViewById(R.id.demoBoard);
        demoBoard.setOnClickListener(v -> navigate(GameActivity.class));

        Button lobby = findViewById(R.id.lobbyActivityButton);
        lobby.setOnClickListener(v -> navigate(lobbyActivity.class));

    }

    private void navigate(Class<?> cl){
        Intent i = new Intent(getApplicationContext(), cl);
        startActivity(i);
    }
}
