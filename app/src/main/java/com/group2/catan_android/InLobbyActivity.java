package com.group2.catan_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group2.catan_android.adapter.PlayerListAdapter;
import com.group2.catan_android.data.live.GameStartedDto;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.data.service.StompManager;
import com.group2.catan_android.databinding.ActivityInLobbyBinding;
import com.group2.catan_android.viewmodel.InLobbyViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class InLobbyActivity extends AppCompatActivity {
    private ActivityInLobbyBinding binding;
    private PlayerListAdapter adapter;
    private InLobbyViewModel mViewModel;

    Disposable leaveDisposable;
    Disposable gameStartDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(InLobbyViewModel.initializer)).get(InLobbyViewModel.class);

        gameStartDisposable = StompManager.getInstance().filterByType(GameStartedDto.class).subscribe((gameStartedDto -> navigateToGameActivity()));

        adapter = new PlayerListAdapter(this);
        binding.playerList.setAdapter(adapter);
        binding.playerList.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getPlayersLiveData().observe(this, displayablePlayers -> adapter.setPlayers(displayablePlayers));
        mViewModel.getPlayerIsAdminLiveData().observe(this, this::setAdmin );

        binding.leave.setOnClickListener(v -> {
            doLeave();
        });
        binding.start.setOnClickListener(v ->{
            doStart();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setGameID();
    }

    private void navigateToGameActivity(){
        Intent i = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(i);
        finish();
    }

    private void doStart() {
        MoveSenderRepository.getInstance().startGame(TokenRepository.getInstance().getToken()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    private void setGameID(){
        String gameID = mViewModel.getGameID();
        binding.gameID.setText(gameID);
    }

    private void setAdmin(boolean isAdmin){
        binding.start.setEnabled(isAdmin);
        if(isAdmin)
            binding.start.setBackgroundColor(getColor(R.color.button_available));
        else
            binding.start.setBackgroundColor(getColor(R.color.button_not_available));
    }

    private void doLeave(){
        leaveDisposable = mViewModel.leaveGame()
                .subscribe(this::finish);
    }

    @Override
    public void onBackPressed(){
        doLeave();
        super.onBackPressed();
    }
    @Override
    public void onDestroy(){
        if(leaveDisposable != null)
            leaveDisposable.dispose();
        if(gameStartDisposable != null)
            gameStartDisposable.dispose();
        super.onDestroy();
    }

}