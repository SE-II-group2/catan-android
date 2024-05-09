package com.group2.catan_android.data.repository.board;

import android.database.Observable;

import com.group2.catan_android.gamelogic.Board;

public interface BoardProvider {
    Observable<Board> getBoardObservable();
}
