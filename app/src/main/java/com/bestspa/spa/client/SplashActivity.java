package com.bestspa.spa.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.bestspa.spa.client.Utiles.Constant;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    LinearLayout linFacebook, linGoogle, linEmail, linLogin;
    Intent intent;
    LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bestspa",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        videoView = (VideoView) findViewById(R.id.videoView);
        linFacebook = (LinearLayout) findViewById(R.id.signUpWithFb);
        linGoogle = (LinearLayout) findViewById(R.id.signUpWithGooglePlus);
        linEmail = (LinearLayout) findViewById(R.id.signUpWithEmail);
        linLogin = (LinearLayout) findViewById(R.id.emailLoginBtn);

        linFacebook.setOnClickListener(this);
        linGoogle.setOnClickListener(this);
        linEmail.setOnClickListener(this);
        linLogin.setOnClickListener(this);

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
            this.videoView.setOnCompletionListener(new VideoStart());
        } catch (Exception e) { }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.emailLoginBtn:

                intent = new Intent(this,LoginActivity.class);

                startActivity(intent);

                break;
            case R.id.signUpWithEmail:

                intent = new Intent(this,SelectUserTypeActivity.class);
                intent.putExtra(Constant.Signupwith,Constant.Email);
                startActivity(intent);

                break;
            case R.id.signUpWithFb:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Constant.WRITE_PERMISSION_CODE);
                        return;
                    }
                }

                intent = new Intent(this,SelectUserTypeActivity.class);
                intent.putExtra(Constant.Signupwith,Constant.Facebook);
                startActivity(intent);

                break;
            case R.id.signUpWithGooglePlus:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Constant.WRITE_PERMISSION_CODE);
                        return;
                    }
                }

                intent = new Intent(this,SelectUserTypeActivity.class);
                intent.putExtra(Constant.Signupwith,Constant.Google);
                startActivity(intent);

                break;
        }
    }


    class VideoStart implements MediaPlayer.OnCompletionListener {
        VideoStart() {
        }

        public void onCompletion(MediaPlayer mp) {
            videoView.start();
        }
    }
}
