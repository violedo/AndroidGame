package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.*;

public class MainActivity extends AppCompatActivity {

    public MediaPlayer mp = null;
    public static boolean start=false;
    public static boolean stop=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp=MediaPlayer.create(this, R.raw.music);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (start && !stop){
                        try {
                            Thread.sleep(4800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                    }
                    else if (!start && stop&&mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }
}
