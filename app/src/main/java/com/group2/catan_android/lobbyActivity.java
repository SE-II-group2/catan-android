package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
//import com.group2.catan_android.networking.dto.Game;

public class lobbyActivity extends AppCompatActivity {
    private ConstraintLayout selectedGame = null;
    private String selectedGameID = null;
    private String playerName = "";
    private EditText playerNameEditText;

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

        playerNameEditText = findViewById(R.id.playerName);
        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                Log.d("LobbyActivity", "Connect button pressed with playername: " + playerName + "and gameID: " + selectedGameID);
            }
        });

        // TODO: Just to test UI, should be easy replaceable with the Game object from networking
        List<Game> testLobbies = new ArrayList<>();
        testLobbies.add(new Game("ID_1", 1, 4));
        testLobbies.add(new Game("ID_2", 3, 4));
        testLobbies.add(new Game("ID_3", 2, 4));
        testLobbies.add(new Game("ID_4", 4, 4));
        testLobbies.add(new Game("ID_5", 1, 4));
        testLobbies.add(new Game("ID_6", 2, 4));
        testLobbies.add(new Game("ID_7", 3, 4));
        testLobbies.add(new Game("ID_8", 4, 4));
        testLobbies.add(new Game("ID_9", 3, 4));
        testLobbies.add(new Game("ID_10", 2, 4));
        testLobbies.add(new Game("ID_11", 4, 4));
        testLobbies.add(new Game("ID_12", 1, 4));

        renderLobbies(testLobbies);
    }

    private void renderLobbies(List<Game> availableLobbies){

        LinearLayout linearLayout = findViewById(R.id.lobbies);

        for (Game game : availableLobbies) {
            ConstraintLayout constraintLayout = createGameEntry(game);
            linearLayout.addView(constraintLayout);

            // TODO: Refactor setOnClickListener() for connection functionality
            constraintLayout.setOnClickListener(v -> {
                if (selectedGame != null && selectedGame != v){
                    selectedGame.setBackgroundColor(Color.parseColor("#FF7EC850"));
                }
                v.setBackgroundColor(Color.parseColor("#FF588c38"));
                selectedGame = (ConstraintLayout) v;
                selectedGameID = game.gameID;
                Log.d("LobbyActivity", "user chose lobby: " + game.getGameID());
            });
        }
    }

    private ConstraintLayout createGameEntry(Game game){
        String players = game.getPlayerCount()+ "/" + game.getMaxPlayers() + "Players";
        String gameId = "Game: " + game.getGameID();

        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);


        params.topMargin = 18;
        params.leftMargin = 70;
        params.rightMargin = 70;
        constraintLayout.setLayoutParams(params);
        constraintLayout.setBackgroundColor(Color.parseColor("#FF7EC850"));

        // TextView links
        TextView textViewLeft = new TextView(this);
        textViewLeft.setId(View.generateViewId());
        textViewLeft.setText(gameId);
        textViewLeft.setTextColor(Color.WHITE);
        textViewLeft.setTextSize(18);

        // TextView rechts
        TextView textViewRight = new TextView(this);
        textViewRight.setId(View.generateViewId());
        textViewRight.setText(players);
        textViewRight.setTextColor(Color.WHITE);
        textViewRight.setTextSize(18);

        constraintLayout.addView(textViewLeft);
        constraintLayout.addView(textViewRight);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(textViewLeft.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 16);
        constraintSet.connect(textViewRight.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 16);
        constraintSet.applyTo(constraintLayout);

        return constraintLayout;
    }

    // TODO: Remove prototype implementation when networking is implemented for the lobbyActivity
    class Game {
        private final String gameID;
        private int playerCount;
        private final int maxPlayers;

        public Game(String gameID, int playerCount, int maxPlayers){
            this.gameID = gameID;
            this.playerCount = playerCount;
            this.maxPlayers = maxPlayers;
        }

        public String getGameID() {
            return gameID;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public int getMaxPlayers(){
            return maxPlayers;
        }
    }
}