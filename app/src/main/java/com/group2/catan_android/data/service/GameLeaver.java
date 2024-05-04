package com.group2.catan_android.data.service;

import io.reactivex.Completable;

public interface GameLeaver {
    Completable leaveGame();
}
