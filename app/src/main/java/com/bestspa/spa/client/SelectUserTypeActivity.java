package com.bestspa.spa.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.Model.MerchantModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectUserTypeActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private LinearLayout staffSignUpBtn, layoutHead, customerSignUpBtn;
    private String uriPathLarge = "";
    Intent intent;
    String SIGNUP_WITH, UserType;
    CallbackManager callbackManager;
    private AccessToken mAccessToken;
    SessionManager sessionManager;
    APIInterface apiInterface;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    String newToken, name, photoUrl, address;
    User googleUser;
    File imageFile;
    List<MultipartBody.Part> parts = new ArrayList<>();
    User userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_sign_up);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
            }
        });

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_google_client_key))
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        SIGNUP_WITH = getIntent().getStringExtra(Constant.Signupwith);
        sessionManager = new SessionManager(this);

        videoView = (VideoView) findViewById(R.id.videoView);
        this.layoutHead = (LinearLayout) findViewById(R.id.layoutHead);
        this.layoutHead.setOnClickListener(this);
        this.staffSignUpBtn = (LinearLayout) findViewById(R.id.staffSignUpBtn);
        this.staffSignUpBtn.setOnClickListener(this);
        this.customerSignUpBtn = (LinearLayout) findViewById(R.id.customerSignUpBtn);
        this.customerSignUpBtn.setOnClickListener(this);
        customerSignUpBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                logOut();
                logOutFacebook();
                return true;
            }
        });

        mAccessToken =  AccessToken.getCurrentAccessToken();
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

    private void getUserProfile(final AccessToken mAccessToken) {

        UtilFun.ShowProgres(SelectUserTypeActivity.this,getString(R.string.register_new_account));

        GraphRequest graphRequest = GraphRequest.newMeRequest(mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                Log.d("data", new Gson().toJson(object));

                try {

                    final String username = object.getString("name");
                    Log.d("username",username);
                    final String fb_id = object.getString("id");
                    final String email = object.getString("email");
                    final String picture = object.getJSONObject("picture").
                            getJSONObject("data").getString("url");
                    Glide.with(SelectUserTypeActivity.this).load(picture).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            String path = Environment.getExternalStorageDirectory().toString()
                                    + "/Pictures/" + System.currentTimeMillis() + ".png";
                            OutputStream out = null;
                            imageFile = new File(path);
                            try {
                                out = new FileOutputStream(imageFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }

                                } catch (Exception exc) {
                                }
                            }
                            userData = new User();
                            userData.setFcmToken(newToken);
                            userData.setSignupwith(Constant.Facebook);
                            userData.setUserTypes(UserType);
                            userData.setPassword(mAccessToken.getToken());
                            userData.setEmail(email);
                            userData.setUserName(username);
                            MerchantModel merchantModel = new MerchantModel();
                            ArrayList<Integer> integerArrayList = new ArrayList<>();
                            integerArrayList.add(1);
                            if (UserType.equals(Constant.Business)) {
                                merchantModel.setBusinessName(username);
                                merchantModel.setRating(integerArrayList);
                                userData.setMerchantid(merchantModel);
                            }

                            apiInterface.UserRegistration(userData).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.code() == 200) {
                                        userData = response.body();
                                        String userTypes = userData.getUserTypes();
                                        sessionManager.setLogin(true);
                                        if (userTypes.equals(Constant.Business)) {
                                            intent = new Intent(SelectUserTypeActivity.this, BussinessSettingActivity.class);
                                            intent.putExtra(Constant.FromStaffSignUp, true);
                                        } else {
                                            intent = new Intent(SelectUserTypeActivity.this, MainActivity.class);
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra(Constant.User, new Gson().toJson(userData));
                                        UtilFun.ShowProgres(SelectUserTypeActivity.this, getString(R.string.uploadingimg));
                                        MultipartBody.Builder builder = new MultipartBody.Builder();
                                        builder.setType(MultipartBody.FORM);
                                        parts.clear();
                                        File file = new File(imageFile.getPath());
                                        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                                        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
                                        parts.add(body);
                                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), "upload");
                                        apiInterface.postImage(parts,requestBody,userData.get_id()).enqueue(new Callback<Object>() {
                                            @Override
                                            public void onResponse(Call<Object> call, Response<Object> response) {
                                                UtilFun.DismissProgress();
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                                    JSONArray files = jsonObject.getJSONArray("files");
                                                    ArrayList<String> arrayList = new ArrayList<String>();
                                                    arrayList.add(files.getJSONObject(0).getString("filename"));
                                                    userData.setUserImage(arrayList);
                                                    sessionManager.createLoginSession(userData);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }finally {
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Object> call, Throwable t) {
                                                UtilFun.DismissProgress();
                                                Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                                startActivity(intent);

                                            }
                                        });


                                    } else if (response.code() == 404) {
                                        UtilFun.DismissProgress();
                                        logOutFacebook();
                                        Toast.makeText(SelectUserTypeActivity.this, userData.getEmail() + " " + getString(R.string.email_allready_available), Toast.LENGTH_SHORT).show();

                                    } else {
                                        UtilFun.DismissProgress();
                                        logOutFacebook();
                                        Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    UtilFun.DismissProgress();
                                    Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                    logOutFacebook();
                                }
                            });

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,birthday,picture.width(200)");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
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
            this.videoView.setOnCompletionListener(new VideoListener());
        } catch (Exception e) {
        }
    }

    class VideoListener implements MediaPlayer.OnCompletionListener {

        public void onCompletion(MediaPlayer mp) {
            videoView.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layoutHead:

                onBackPressed();

                break;
            case R.id.staffSignUpBtn:

                UserType = Constant.Business;

                if (SIGNUP_WITH.equals(Constant.Facebook)) {

                    if (mAccessToken != null && !mAccessToken.isExpired())
                    {
                        logOutFacebook();
                    }
                    LoginManager.getInstance().logInWithReadPermissions(SelectUserTypeActivity.this, Arrays.asList("public_profile"));

                } else if (SIGNUP_WITH.equals(Constant.Google)) {

                    googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                    if (googleSignInAccount == null) {
                        Intent signInIntent = googleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, Constant.GOOGLE_SIGNIN_CODE);
                    } else {
                        Log.d("alreadylogin", googleSignInAccount.getEmail());
                    }


                } else if (SIGNUP_WITH.equals(Constant.Email)) {
                    intent = new Intent(this, SignupMainActivity.class);
                    intent.putExtra(Constant.UserType, Constant.Business);
                    intent.putExtra(Constant.Signupwith, Constant.Email);
                    startActivity(intent);
                }


                break;
            case R.id.customerSignUpBtn:

                UserType = Constant.Business;

                if (SIGNUP_WITH.equals(Constant.Facebook)) {

                    if (mAccessToken != null && !mAccessToken.isExpired())
                    {
                        logOutFacebook();
                    }
                    LoginManager.getInstance().logInWithReadPermissions(SelectUserTypeActivity.this, Arrays.asList("public_profile"));


                } else if (SIGNUP_WITH.equals(Constant.Google)) {

                    googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                    if (googleSignInAccount == null) {
                        Intent signInIntent = googleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, Constant.GOOGLE_SIGNIN_CODE);
                    } else {
                        Log.d("alreadylogin", googleSignInAccount.getEmail());
                    }


                } else if (SIGNUP_WITH.equals(Constant.Email)) {
                    intent = new Intent(this, SignupMainActivity.class);
                    intent.putExtra(Constant.UserType, Constant.Customer);
                    intent.putExtra(Constant.Signupwith, Constant.Email);
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GOOGLE_SIGNIN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful())
            {
                handleSignInResult(task);
                UtilFun.ShowProgres(this, getString(R.string.register_new_account));
            }

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            String email = account.getEmail();
            if (account.getDisplayName().equals("")) {
                if (account.getGivenName().equals("")) {
                    name = account.getFamilyName();
                } else {
                    name = account.getGivenName();
                }
            } else {
                name = account.getDisplayName();
            }

            googleUser = new User();
            googleUser.setFcmToken(newToken);
            googleUser.setSignupwith(Constant.Google);
            googleUser.setUserTypes(UserType);
            googleUser.setUserName(name);
            googleUser.setPassword(idToken);
            googleUser.setEmail(email);
            MerchantModel merchantModel = new MerchantModel();
            ArrayList<Integer> integerArrayList = new ArrayList<>();
            integerArrayList.add(1);
            if (UserType.equals(Constant.Business)) {
                merchantModel.setBusinessName(name);
                merchantModel.setRating(integerArrayList);
                googleUser.setMerchantid(merchantModel);
            } else {
                googleUser.setUserName(name);
            }


            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            String path = Environment.getExternalStorageDirectory().toString()
                                    + "/Pictures/" + System.currentTimeMillis() + ".png";
                            OutputStream out = null;
                            imageFile = new File(path);
                            try {
                                out = new FileOutputStream(imageFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }

                                } catch (Exception exc) {
                                }
                            }

                            apiInterface.UserRegistration(googleUser).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
//                    UtilFun.DismissProgress();
                                    if (response.code() == 200) {
                                        userData = response.body();
                                        String userTypes = userData.getUserTypes();
                                        sessionManager.setLogin(true);
                                        if (userTypes.equals(Constant.Business)) {
                                            intent = new Intent(SelectUserTypeActivity.this, BussinessSettingActivity.class);
                                            intent.putExtra(Constant.FromStaffSignUp, true);
                                        } else {
                                            intent = new Intent(SelectUserTypeActivity.this, MainActivity.class);
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra(Constant.User, new Gson().toJson(userData));
                                        UtilFun.ShowProgres(SelectUserTypeActivity.this, getString(R.string.uploadingimg));
                                        MultipartBody.Builder builder = new MultipartBody.Builder();
                                        builder.setType(MultipartBody.FORM);
                                        parts.clear();
                                        File file = new File(imageFile.getPath());
                                        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                                        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
                                        parts.add(body);
                                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), "upload");
                                        apiInterface.postImage(parts,requestBody,userData.get_id()).enqueue(new Callback<Object>() {
                                            @Override
                                            public void onResponse(Call<Object> call, Response<Object> response) {
                                                UtilFun.DismissProgress();
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                                    JSONArray files = jsonObject.getJSONArray("files");
                                                    ArrayList<String> arrayList = new ArrayList<String>();
                                                    arrayList.add(files.getJSONObject(0).getString("filename"));
                                                    userData.setUserImage(arrayList);
                                                    sessionManager.createLoginSession(userData);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }finally {
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Object> call, Throwable t) {
                                                UtilFun.DismissProgress();
                                                Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                                startActivity(intent);

                                            }
                                        });

                                    } else if (response.code() == 404) {
                                        UtilFun.DismissProgress();
                                        logOut();
                                        Toast.makeText(SelectUserTypeActivity.this, googleUser.getEmail() + " " + getString(R.string.email_allready_available), Toast.LENGTH_SHORT).show();

                                    } else {
                                        UtilFun.DismissProgress();
                                        logOut();
                                        Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    UtilFun.DismissProgress();
                                    Toast.makeText(SelectUserTypeActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                    logOut();
                                }
                            });

                        }
                    });

        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    public void logOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(SelectUserTypeActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("logout", "success");
                        }
                    }
                });
    }

    public void logOutFacebook()
    {
        LoginManager.getInstance().logOut();
    }
}
