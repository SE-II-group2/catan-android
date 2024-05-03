package com.group2.catan_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.catan_android.adapter.GameListAdapter;
import com.group2.catan_android.data.api.ApiErrorResponse;
import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.model.AvailableGame;
import com.group2.catan_android.data.repository.lobby.LobbyRepository;
import com.group2.catan_android.data.service.ApiService;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.data.service.StompManager;
import com.group2.catan_android.databinding.ActivityConnectToGameBinding;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.socket.SocketManager;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class ConnectToGameActivity extends AppCompatActivity {

    private Handler mainHandler;
   private ActivityConnectToGameBinding binding;
   private GameListAdapter adapter;
   private final SocketManager socketManager = SocketManager.getInstance();

   private CompositeDisposable compositeDisposable;
   private final GameListAdapter.ItemClickListener listener = new GameListAdapter.ItemClickListener() {
       @Override
       public void onItemClicked(AvailableGame game) {
           binding.selectedGame.gameID.setText(game.getGameID());
           binding.selectedGame.playersConnected.setText(Integer.toString(game.getPlayerCount()));
           selectedGame = game;
       }
   };

   private AvailableGame selectedGame;
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
        compositeDisposable = new CompositeDisposable();

        getAvailableGames();
        binding.refresh.setOnClickListener(v -> {
            getAvailableGames();
        });
        binding.createNew.setOnClickListener(v -> {
            createGame();
        });
        binding.connect.setOnClickListener(v -> {
            joinGame();
        });

        adapter = new GameListAdapter(this.listener);
        binding.gamesRecyclerView.setAdapter(adapter);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void getAvailableGames() {
        setLoading(true);
        LobbyRepository test = LobbyRepository.getInstance();
        test.getLobbies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<AvailableGame> availableGames) {
                        Log.d("Data", availableGames.size() + " games fetched");
                        adapter.setGames(availableGames);
                        setLoading(false);
                        binding.gamesRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setLoading(false);
                        showMessage(e.getMessage());
                    }
                });
    }

    private void createGame(){
        GameController gc = GameController.getInstance();
        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName(binding.playerName.getText().toString());
        gc.createGame(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Intent i = new Intent(getApplicationContext(), InLobby.class);
                        startActivity(i);
                        Log.d("Testing", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Testing", e.getMessage() != null ? e.getMessage() : "error");
                    }
                });
    }
    private void joinGame(){
        GameController gc = GameController.getInstance();
        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName(binding.playerName.getText().toString());
        request.setGameID(this.selectedGame.getGameID());
        gc.joinGame(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Intent i = new Intent(getApplicationContext(), InLobby.class);
                        startActivity(i);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Testing", e.getMessage() != null ? e.getMessage() : "error");
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        compositeDisposable.clear();
    }

}