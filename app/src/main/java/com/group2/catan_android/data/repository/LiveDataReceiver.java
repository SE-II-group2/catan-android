package com.group2.catan_android.data.repository;

import io.reactivex.Flowable;

public interface LiveDataReceiver<T>{
    void setLiveData(Flowable<T> in);
}
