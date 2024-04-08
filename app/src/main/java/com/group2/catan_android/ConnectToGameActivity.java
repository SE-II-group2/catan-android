package com.group2.catan_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.catan_android.adapter.GameListAdapter;
import com.group2.catan_android.databinding.ActivityConnectToGameBinding;
import com.group2.catan_android.networking.WebSocketClient;
import com.group2.catan_android.networking.dto.ApiErrorResponse;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.dto.JoinGameRequest;
import com.group2.catan_android.networking.dto.JoinGameResponse;
import com.group2.catan_android.networking.dto.ListGameResponse;
import com.group2.catan_android.networking.repository.GameRepository;
import com.group2.catan_android.networking.socket.SocketManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ConnectToGameActivity extends AppCompatActivity {

    private Handler mainHandler;
   private ActivityConnectToGameBinding binding;
   private GameListAdapter adapter;
   private final GameRepository repository = GameRepository.getInstance();
   private final SocketManager socketManager = SocketManager.getInstance();
   private final GameListAdapter.ItemClickListener listener = new GameListAdapter.ItemClickListener() {
       @Override
       public void onItemClicked(Game game) {
           binding.selectedGame.gameID.setText(game.getGameID());
           binding.selectedGame.playersConnected.setText(Integer.toString(game.getPlayerCount()));
           selectedGame = game;
       }
   };

   private Game selectedGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityConnectToGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getAvailableGames();
        binding.refresh.setOnClickListener(v -> {
            getAvailableGames();
        });
        binding.createNew.setOnClickListener(v -> {
            joinGame(true);
        });
        binding.connect.setOnClickListener(v -> {
            joinGame(false);
        });

        adapter = new GameListAdapter(this.listener);
        binding.gamesRecyclerView.setAdapter(adapter);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void getAvailableGames() {
        setLoading(true);
        GameRepository repository = GameRepository.getInstance();
        repository.listGames(new GameRepository.listCallback() {
            @Override
            public void onListReceived(ListGameResponse response) {
                List<Game> games = response.getGameList();
                if(games.isEmpty()) {
                    showMessage("No games Found");
                }else {
                    adapter.setGames(games);
                    binding.gamesRecyclerView.setVisibility(View.VISIBLE);
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Networking", "Failed to fetch games", throwable);
                showMessage("Failed to fetch games");
                setLoading(false);
            }
        });
    }

    private void showMessage(String message) {
        binding.TextHint.setText(message);
        binding.TextHint.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean isLoading) {
        if(isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.gamesRecyclerView.setVisibility(View.GONE);
            binding.TextHint.setVisibility(View.GONE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private boolean checkInput(){
        return !binding.playerName.getText().toString().isBlank();
    }

    private void joinGame(boolean create) {
        if (!checkInput()) {
            Toast.makeText(getApplicationContext(), "Name not set", Toast.LENGTH_LONG).show();
            return;
        }

        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName(binding.playerName.getText().toString());
        if(!create)
            request.setGameID(selectedGame.getGameID());

        GameRepository.JoinCallback callback = new GameRepository.JoinCallback() {
            @Override
            public void onJoin(JoinGameResponse joinGameResponse) {
                Log.d("Game", "joined " + joinGameResponse.getGameID());
                Log.d("Game", "token " + joinGameResponse.token);
                Toast.makeText(ConnectToGameActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                socketManager.connect(joinGameResponse.token);
                socketManager.onLifecycleEvent(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case ERROR:
                            Log.d("Socket", "error");
                            break;
                        case OPENED:
                            Log.d("socket", "Opened");
                            break;
                        case CLOSED:
                            Log.d("Socket", "closed");
                    }
                    socketManager.subscribe("/topic/game/" + joinGameResponse.getGameID() + "/messages", message -> {
                        Log.d("Comm", message.getPayload().strip());
                        mainHandler.post(() -> {
                                    Toast.makeText(ConnectToGameActivity.this, message.getPayload().strip(), Toast.LENGTH_SHORT).show();
                                }
                            );
                    });
                });
            }

            @Override
            public void onJoinUnsuccessful(ApiErrorResponse errorResponse) {
                Log.d("Game", "did not join" + errorResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("Game", "error", throwable);
            }
        };
        repository.join(request, callback, create);
    }

}