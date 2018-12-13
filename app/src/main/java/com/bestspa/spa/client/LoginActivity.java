package com.bestspa.spa.client;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.Links;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private VideoView videoView;
    LinearLayout lin_signup;
    Intent intent;
    Button btn_login;
    EditText edt_email, edt_pass;
    APIInterface apiInterface;
    SessionManager sessionManager;
    LinearLayout lin_loginfb, lin_logingoogle;
    User user;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    CallbackManager callbackManager;
    AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_login);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_google_client_key))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        videoView = (VideoView) findViewById(R.id.videoView);
        lin_signup = (LinearLayout) findViewById(R.id.signUpBtnLayout);
        btn_login = (Button) findViewById(R.id.emailLoginBtn);
        edt_email = (EditText) findViewById(R.id.emailLoginUserName);
        edt_pass = (EditText) findViewById(R.id.emailLoginPassword);
        lin_loginfb = (LinearLayout) findViewById(R.id.loginWithFb);
        lin_logingoogle = (LinearLayout) findViewById(R.id.loginWithGoogle);

        lin_signup.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        lin_logingoogle.setOnClickListener(this);
        lin_loginfb.setOnClickListener(this);

        mAccessToken = AccessToken.getCurrentAccessToken();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                Log.d("onresponse", String.valueOf(mAccessToken.getToken()));
                Log.d("onresponse", String.valueOf(AccessToken.getCurrentAccessToken()));
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {
                Log.d("onresponse", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("onresponse", "error");
            }
        });
    }

    private void getUserProfile(final AccessToken AccessToken) {


        try {

            UtilFun.ShowProgres(LoginActivity.this, getString(R.string.register_new_account));

            GraphRequest graphRequest = GraphRequest.newMeRequest(mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {

                        if (!(object.getString("email").equals("") || AccessToken.getToken().equals(""))) {
                            apiInterface.GoogleSignIn(object.getString("email"), AccessToken.getToken().toString() , Constant.Facebook).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    UtilFun.DismissProgress();
                                    if (response.code() == 200) {
                                        User user = response.body();
                                        sessionManager.setLogin(true);
                                        sessionManager.createLoginSession(user);
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(67108864);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.entercredentialwrong), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    UtilFun.DismissProgress();
                                    Toast.makeText(LoginActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            UtilFun.DismissProgress();
                            Toast.makeText(LoginActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            intent = new Intent(LoginActivity.this, SignupMainActivity.class);
                            intent.addFlags(67108864);
                            startActivity(intent);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,birthday,picture.width(200)");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onStart() {
        super.onStart();
        try {
            playVideo("android.resource://" + getPackageName() + "/raw/easy_spa_video");
        } catch (Exception e) {
        }
    }

    private void playVideo(String uriPath) {
        try {
            this.videoView.setVideoURI(Uri.parse(uriPath));
            this.videoView.start();
            this.videoView.setOnCompletionListener(new LoginActivity.C14427());
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtnLayout:

                intent = new Intent(this, SplashActivity.class);
                intent.addFlags(67108864);
                startActivity(intent);

                break;
            case R.id.emailLoginBtn:

                if (ValidateField()) {
                    UtilFun.ShowProgres(this, getString(R.string.loging));
                    apiInterface.UserLogin(edt_email.getText().toString(), edt_pass.getText().toString()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            UtilFun.DismissProgress();
                            if (response.code() == 200) {
                                User user = response.body();
                                sessionManager.setLogin(true);
                                sessionManager.createLoginSession(user);

                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(67108864);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.entercredentialwrong), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            UtilFun.DismissProgress();
                            Toast.makeText(LoginActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                        }
                    });

                    break;
                }
            case R.id.loginWithFb:

                if (mAccessToken != null && !mAccessToken.isExpired()) {
                    logOutFacebook();
                }
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));

                break;

            case R.id.loginWithGoogle:

                googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                if (googleSignInAccount == null) {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Constant.GOOGLE_SIGNIN_CODE);
                } else {
                    Log.d("alreadylogin", googleSignInAccount.getEmail());
                    logOut();
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Constant.GOOGLE_SIGNIN_CODE);
                }
                break;

            case R.id.layoutHead:
                intent = new Intent(this,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }

    private void logOutFacebook() {

        LoginManager.getInstance().logOut();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GOOGLE_SIGNIN_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            UtilFun.ShowProgres(this, getString(R.string.loging));
            if (task.isSuccessful())
            {
                handleSignInResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (!(account.getIdToken().equals("") || account.getEmail().equals(""))) {
                apiInterface.GoogleSignIn(account.getEmail(), account.getIdToken().toString(), Constant.Google).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        UtilFun.DismissProgress();
                        if (response.code() == 200) {
                            User user = response.body();
                            sessionManager.setLogin(true);
                            sessionManager.createLoginSession(user);

                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(67108864);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.entercredentialwrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        UtilFun.DismissProgress();
                        Toast.makeText(LoginActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                    }
                });


            } else {
                UtilFun.DismissProgress();
                Toast.makeText(LoginActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                intent = new Intent(LoginActivity.this, SignupMainActivity.class);
                intent.addFlags(67108864);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ValidateField() {

        Boolean isValidate = true;

        if (edt_email.getText().toString().equals("")) {
            isValidate = false;
            edt_email.setError(getString(R.string.enter_valid_email));
            Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
            return isValidate;

        } else if (edt_pass.getText().toString().equals("")) {
            isValidate = false;
            edt_pass.setError(getString(R.string.enter_valid_password));
            Toast.makeText(this, getString(R.string.enter_valid_password), Toast.LENGTH_SHORT).show();
            return isValidate;
        }

        return isValidate;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    class C14427 implements MediaPlayer.OnCompletionListener {
        C14427() {
        }

        public void onCompletion(MediaPlayer mp) {
            videoView.start();
        }

    }

    public void logOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("logout", "success");
                        }
                    }
                });
    }

}
