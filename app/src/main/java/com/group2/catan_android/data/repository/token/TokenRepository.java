package com.group2.catan_android.data.repository.token;

import org.jetbrains.annotations.NotNull;

public class TokenRepository {
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_GAME_ID = "KEY_GAME_ID";
    private static final String KEY_IN_GAME_ID = "KEY_IN_GAME_ID";
    public static final int NO_IN_GAME_ID = -1;
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

    public void storeToken(String token){
        preferenceManager.save(KEY_TOKEN, token);
    }
    public String getToken(){
        return preferenceManager.get(KEY_TOKEN, null);
    }
    public void storeGameID(String gameID){
        preferenceManager.save(KEY_GAME_ID, gameID);
    }
    public String getGameID(){
        return preferenceManager.get(KEY_GAME_ID, null);
    }
    public void storeInGameID(int inGameID){
        preferenceManager.save(KEY_IN_GAME_ID, inGameID);
    }
    public int getInGameID(){
        return preferenceManager.getInt(KEY_IN_GAME_ID, NO_IN_GAME_ID);
    }
    public void clear(){
        storeGameID(null);
        storeToken(null);
        storeInGameID(NO_IN_GAME_ID);
    }

    public boolean fullDataAvailable(){
        return getToken() != null && getGameID() != null && getInGameID() != NO_IN_GAME_ID;
    }
}
