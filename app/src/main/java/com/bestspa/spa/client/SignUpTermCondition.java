package com.bestspa.spa.client;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

public class SignUpTermCondition extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_terms_video);
        ((LinearLayout) findViewById(R.id.layoutHead)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((Button) findViewById(R.id.backTerm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        try {
            playVideo("android.resource://" + getPackageName() + "/raw/easy_spa_video");
        } catch (Exception e) {
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void playVideo(String uriPath) {
        try {
            this.videoView = (VideoView) findViewById(R.id.videoView);
            this.videoView.setVideoURI(Uri.parse(uriPath));
            this.videoView.start();
            this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
