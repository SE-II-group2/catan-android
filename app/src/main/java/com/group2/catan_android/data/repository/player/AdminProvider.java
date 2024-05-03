package com.group2.catan_android.data.repository.player;


import io.reactivex.Observable;

public interface AdminProvider {
    Observable<Boolean> getAdminObservable();
}
