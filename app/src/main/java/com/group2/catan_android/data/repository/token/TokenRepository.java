package com.group2.catan_android.data.repository.token;

import org.jetbrains.annotations.NotNull;

public class TokenRepository {
    private static TokenRepository instance;
    private final PreferenceManager preferenceManager;
    private TokenRepository(PreferenceManager preferenceManager){
        this.preferenceManager = preferenceManager;
    }

    //Initializes the singleton. To bo called on Application Start.
    public static synchronized void initialize(@NotNull PreferenceManager preferenceManager){
        if(instance == null){
            instance = new TokenRepository(preferenceManager);
        }
    }
    @NotNull
    public static synchronized TokenRepository getInstance(){
        return instance;
    }
}
