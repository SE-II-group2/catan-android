package com.group2.catan_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.fragments.PlayerScoresFragment;
import com.group2.catan_android.gamelogic.Player;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class GameOverActivity extends AppCompatActivity {

    private PlayerScoresFragment playerScoresFragment;
    private List<Player> players;
    Disposable finalGamestateDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_game_over);

        playerScoresFragment = new PlayerScoresFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.playerFinalScoresFragment, playerScoresFragment).commit();

        CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
        finalGamestateDisposable = currentGamestateRepository.getAllPlayerObservable().subscribe(playerList -> this.players = playerList);

        Button lobbyButton = findViewById(R.id.leave);
        lobbyButton.setOnClickListener(v -> {leave(); navigate(LobbyActivity.class);});

        setToFullScreen();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setPlayerScores(players);
    }

    private void setPlayerScores(List<Player> playerList) {
        playerScoresFragment.setFinalScores(playerList);
    }

    private void setToFullScreen() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void leave(){
        GameController.getInstance().cleanupFinishedGame();
    }

    private void navigate(Class<?> cl){
        Intent i = new Intent(getApplicationContext(), cl);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finalGamestateDisposable.dispose();
    }
}
