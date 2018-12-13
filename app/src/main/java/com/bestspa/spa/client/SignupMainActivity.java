package com.bestspa.spa.client;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.bestspa.spa.client.Model.MerchantModel;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.bestspa.spa.client.Utiles.UtilFun;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupMainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    LinearLayout linHeader;
    EditText edt_username,edt_emailid,edt_password;
    Button btn_signup;
    APIInterface apiInterface;
    String SignUpWith, StEmail, StUsername, StPassword, UserType ;
    Intent intent;
    SessionManager sessionManager;
    String newToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_signup_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

            }
        });

        UserType = getIntent().getStringExtra(Constant.UserType);
        SignUpWith = getIntent().getStringExtra(Constant.Signupwith);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        sessionManager = new SessionManager(this);

        videoView = (VideoView) findViewById(R.id.videoView);
        linHeader = (LinearLayout) findViewById(R.id.layoutHead);
        edt_emailid = (EditText) findViewById(R.id.emailId);
        edt_password = (EditText)findViewById(R.id.password);
        edt_username = (EditText) findViewById(R.id.userName);
        btn_signup = (Button)findViewById(R.id.signUpBtn);

        if(UserType.equals(Constant.Business))
        {
            edt_username.setHint(getString(R.string.businessname));
        }

        linHeader.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
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
            this.videoView.setOnCompletionListener(new SignupMainActivity.C14427());
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layoutHead:

                intent = new Intent(this,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.signUpBtn:

                if(ValidateField())
                {
                    UtilFun.ShowProgres(this,getString(R.string.signing));
                    final User user = new User();
                    user.setEmail(edt_emailid.getText().toString());
                    user.setPassword(edt_password.getText().toString());
                    user.setSignupwith(SignUpWith);
                    user.setUserTypes(UserType);
                    user.setFcmToken(newToken);
                    MerchantModel merchantModel = new MerchantModel();
                    ArrayList<Integer> integerArrayList = new ArrayList<>();
                    integerArrayList.add(1);
                    if(UserType.equals(Constant.Business))
                    {
                        merchantModel.setBusinessName(edt_username.getText().toString());
                    }else
                    {
                        user.setUserName(edt_username.getText().toString());
                    }
                    merchantModel.setRating(integerArrayList);
                    user.setMerchantid(merchantModel);

                    apiInterface.UserRegistration(user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            UtilFun.DismissProgress();
                            if (response.code() == 200)
                            {
                                User userData = response.body();
                                String userTypes = userData.getUserTypes();
                                sessionManager.setLogin(true);
                                sessionManager.createLoginSession(userData);

                                if (userTypes.equals(Constant.Business))
                                {
                                    intent = new Intent(SignupMainActivity.this,BussinessSettingActivity.class);
                                    intent.putExtra(Constant.FromStaffSignUp,true);
                                }else
                                {
                                    intent = new Intent(SignupMainActivity.this,MainActivity.class);
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constant.User,new Gson().toJson(userData));
                                startActivity(intent);
                            } else if (response.code() == 404) {
                                Toast.makeText(SignupMainActivity.this, user.getEmail().toString() + (R.string.email_allready_available), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignupMainActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            UtilFun.DismissProgress();
                            Toast.makeText(SignupMainActivity.this,getString(R.string.somethingwentwrong),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
            case R.id.termsBtn:

                intent = new Intent(SignupMainActivity.this,SignUpTermCondition.class);
                startActivity(intent);

                break;

        }
    }

    private boolean ValidateField() {

        Boolean isValidate = true;

        if (edt_username.getText().toString().equals(""))
        {
            isValidate = false;
            edt_username.setError(getString(R.string.enter_valid_name));
            Toast.makeText(this,getString(R.string.enter_valid_name),Toast.LENGTH_SHORT).show();
            return isValidate;

        }else if (!isValidEmail(edt_emailid.getText().toString()))
        {
            isValidate = false;
            Toast.makeText(this,getString(R.string.enter_valid_email),Toast.LENGTH_SHORT).show();
            return isValidate;

        }else if (edt_password.getText().toString().equals(""))
        {
            isValidate = false;
            Toast.makeText(this,getString(R.string.enter_valid_password),Toast.LENGTH_SHORT).show();
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

}
