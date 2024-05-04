package com.group2.catan_android.data.repository.player;

import com.group2.catan_android.data.model.DisplayablePlayer;

import java.util.List;

import io.reactivex.Observable;

public interface PlayerProvider {
    Observable<List<DisplayablePlayer>> getPlayerObservable();
}
