package com.bestspa.spa.client.Utiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bestspa.spa.client.LoginActivity;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class SessionManager  {

    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private static String PREF_NAME ;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        PREF_NAME = context.getString(R.string.app_name);
        this.pref = this._context.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void createLoginSession(User user) {
        this.editor.putString(Constant.User,new Gson().toJson(user));
        this.editor.commit();
    }

    public boolean isLoggedIn() {
        return this.pref.getBoolean(Constant.IsLogin, false);
    }

    public void setLogin(Boolean islogin){this.editor.putBoolean(Constant.IsLogin,islogin);
        this.editor.commit();
    }

    public User getUserDetails() {
        User user = new User();
        user = new Gson().fromJson(this.pref.getString(Constant.User,null),User.class);
        return user;
    }

    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();
        Intent i = new Intent(this._context, LoginActivity.class);
        i.addFlags(67108864);
        this._context.startActivity(i);
    }

}
