package com.group2.catan_android.data.repository.token;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class PreferenceManager {
    private static final String PREF_NAME = "CATAN";
    private static PreferenceManager instance;
    private final SharedPreferences sharedPreferences;

    private PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initialize(@NotNull Context context){
        if(instance == null){
            instance = new PreferenceManager(context.getApplicationContext());
        }
    }
    @NotNull
    public static PreferenceManager getInstance(){
        return instance;
    }

    public void save(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key, String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

    public void save(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue){
        return sharedPreferences.getInt(key, defaultValue);
    }
}
