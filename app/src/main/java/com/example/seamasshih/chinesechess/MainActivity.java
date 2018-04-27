package com.example.seamasshih.chinesechess;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    MediaPlayer background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        background = MediaPlayer.create(this,R.raw.background);
        background.setVolume((float)0.03,(float)0.03);
        background.setLooping(true);
    }

    @Override
    protected void onPause() {
        background.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        background.start();
        super.onResume();
    }
}
