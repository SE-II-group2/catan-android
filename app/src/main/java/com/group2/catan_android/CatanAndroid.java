package com.group2.catan_android;

import android.app.Application;

import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.data.repository.lobby.LobbyRepository;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.PreferenceManager;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.data.service.ApiService;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.data.service.StompDriver;
import com.group2.catan_android.data.service.StompManager;
import com.group2.catan_android.data.util.ObjectMapperProvider;

public class CatanAndroid extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        //Initialize Singletons
        PreferenceManager.initialize(this.getApplicationContext());
        TokenRepository.initialize(PreferenceManager.getInstance());
        LobbyRepository.initialize(ApiService.getInstance().getGameApi());
        StompManager.initialize(StompDriver.getInstance(), ObjectMapperProvider.getMapper());
        GameController.initialize(StompManager.getInstance(), TokenRepository.getInstance(), PlayerRepository.getInstance(), LobbyRepository.getInstance(), CurrentGamestateRepository.getInstance(), GameProgressRepository.getInstance());
        MoveSenderRepository.initialize(ApiService.getInstance().getGameApi());
    }
}
