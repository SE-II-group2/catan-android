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
import com.group2.catan_android.networking.dto.ApiErrorResponse;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.dto.JoinGameRequest;
import com.group2.catan_android.networking.dto.JoinGameResponse;
import com.group2.catan_android.networking.dto.ListGameResponse;
import com.group2.catan_android.networking.repository.GameRepository;

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

        playerNameEditText = findViewById(R.id.LobbyPlayerNameEditText);
        Button connectButton = findViewById(R.id.LobbyJoinButton);
        Button createButton = findViewById(R.id.LobbyCreateButton);
        ImageButton refreshButton = findViewById(R.id.LobbyRefreshButton);

        GameListAdapter.ItemClickListener listener = game -> {
            selectedGameID = game.getGameID();
            Toast.makeText(lobbyActivity.this, "Game selected: " + game.getGameID(), Toast.LENGTH_SHORT).show();
        };

        gameListAdapter = new GameListAdapter(listener);

        RecyclerView recyclerView = findViewById(R.id.LobbyGameRecyclerView);
        recyclerView.setAdapter(gameListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestActiveGames();
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                joinGame(false);
                Log.d("LobbyActivity", "Connect button pressed with playername: " + playerName + "and gameID: " + selectedGameID);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                joinGame(true);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestActiveGames();
            }
        });
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

    private void joinGame(boolean create) {
        String playerName = playerNameEditText.getText().toString().trim();
        Log.d("LobbyActivity", "Player Name: " + playerName + ", Create: " + create);

        if (playerName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Player name is required", Toast.LENGTH_LONG).show();
            return;
        }

        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName(playerName);
        if (!create && selectedGameID != null) {
            request.setGameID(selectedGameID);
        }

        GameRepository repository = GameRepository.getInstance();
        repository.join(request, new GameRepository.JoinCallback() {
            @Override
            public void onJoin(JoinGameResponse joinGameResponse) {
                Toast.makeText(lobbyActivity.this, "Connected to game: " + joinGameResponse.getGameID(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onJoinUnsuccessful(ApiErrorResponse errorResponse) {
                Toast.makeText(lobbyActivity.this, "Failed to join game: " + errorResponse.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("LobbyActivity", "Error joining game", throwable);
                Toast.makeText(lobbyActivity.this, "Error connecting to game", Toast.LENGTH_LONG).show();
            }
        }, create);
    }

}