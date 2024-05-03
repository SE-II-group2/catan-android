package com.group2.catan_android;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.group2.catan_android.adapter.PlayerListAdapter;
import com.group2.catan_android.databinding.ActivityInLobbyBinding;
import com.group2.catan_android.viewmodel.InLobbyViewModel;

import io.reactivex.disposables.Disposable;

public class InLobby extends AppCompatActivity {
    private ActivityInLobbyBinding binding;
    private PlayerListAdapter adapter;
    private InLobbyViewModel mViewModel;

    Disposable leaveDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(InLobbyViewModel.initializer)).get(InLobbyViewModel.class);

        adapter = new PlayerListAdapter(this);
        binding.playerList.setAdapter(adapter);
        binding.playerList.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getPlayersLiveData().observe(this, displayablePlayers -> adapter.setPlayers(displayablePlayers));
        mViewModel.getPlayerIsAdminLiveData().observe(this, this::setAdmin );

        binding.leave.setOnClickListener(v -> {
            doLeave();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setGameID();

    }

    private void setGameID(){
        String gameID = mViewModel.getGameID();
        binding.gameID.setText(gameID);
    }

    private void setAdmin(boolean isAdmin){
        binding.start.setEnabled(isAdmin);
        if(isAdmin)
            binding.start.setBackgroundColor(getColor(R.color.button_not_available));
        else
            binding.start.setBackgroundColor(getColor(R.color.button_available));
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
        super.onDestroy();
    }
}