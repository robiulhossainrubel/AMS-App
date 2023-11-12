package com.rhr.ams.Utis;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefManager {
    private static SharePrefManager instance;
    private static Context ctx;
    private static final String SHAREPREF_NAME = "mysharepref12";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DEPARTMENT = "department";
    private SharePrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharePrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharePrefManager(context);
        }
        return instance;
    }

    public boolean userLogin(String username,String email,String department){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_NAME,username);
        editor.putString(KEY_DEPARTMENT,department);
        editor.apply();
        return true;
    }
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_NAME,null) !=null){
            return true;
        }
        return false;
    }
    public boolean logout(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
    public String getUsername(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME,null);
    }
    public String getEmail(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL,null);
    }
    public String getDepartment(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHAREPREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DEPARTMENT,null);
    }

}
