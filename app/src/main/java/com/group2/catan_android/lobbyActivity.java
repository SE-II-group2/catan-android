package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.group2.catan_android.adapter.GameListAdapter;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.dto.ListGameResponse;
import com.group2.catan_android.networking.repository.GameRepository;

import java.util.ArrayList;
import java.util.List;


public class lobbyActivity extends AppCompatActivity {
    private String selectedGameID = null;
    private String playerName = "";
    private EditText playerNameEditText;

    private GameListAdapter gameListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobbyscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText playerNameEditText = findViewById(R.id.LobbyPlayerNameEditText);
        Button connectButton = findViewById(R.id.LobbyJoinButton);
        Button createButton = findViewById(R.id.LobbyCreateButton);
        ImageButton refreshButton = findViewById(R.id.LobbyRefreshButton);



        GameListAdapter.ItemClickListener listener = game -> Toast.makeText(lobbyActivity.this, "Game selected: " + game.getGameID(), Toast.LENGTH_SHORT).show();

        gameListAdapter = new GameListAdapter(listener);

        RecyclerView recyclerView = findViewById(R.id.LobbyGameRecyclerView);
        recyclerView.setAdapter(gameListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                Log.d("LobbyActivity", "Connect button pressed with playername: " + playerName + "and gameID: " + selectedGameID);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestActiveGames();
            }
        });

        requestActiveGames();
        }

    private void requestActiveGames() {
        GameRepository repository = GameRepository.getInstance();
        repository.listGames(new GameRepository.listCallback() {
            @Override
            public void onListReceived(ListGameResponse response) {
                List<Game> games = response.getGameList();
                gameListAdapter.setGames(games);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(lobbyActivity.this, "Failed to fetch games", Toast.LENGTH_SHORT).show();
            }
        });
    }
}