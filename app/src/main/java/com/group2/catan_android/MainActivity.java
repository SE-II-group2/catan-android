package com.group2.catan_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Button demo = findViewById(R.id.btnDemo);
        demo.setOnClickListener(v -> navigate(DemoActivity.class));

        Button connection = findViewById(R.id.btnConnection);
        connection.setOnClickListener(v -> navigate(ConnectToGameActivity.class));

        Button demoBoard = findViewById(R.id.demoBoard);
        demoBoard.setOnClickListener(v -> navigate(demoboard.class));
    }

    private void navigate(Class<?> cl){
        Intent i = new Intent(getApplicationContext(), cl);
        startActivity(i);
    }
}