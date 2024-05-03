package com.group2.catan_android.data.repository.lobby;

import com.group2.catan_android.data.model.AvailableGame;

import java.util.List;

import io.reactivex.Single;

public interface LobbyLoader {
    Single<List<AvailableGame>> getLobbies();
}
