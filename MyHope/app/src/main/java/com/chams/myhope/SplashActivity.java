package com.chams.myhope;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    Context context = this;

    private TextToSpeech textToSpeech;
    private final static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });
        }


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                welcomeSpeaker(textToSpeech, "Welcome to Blind Assistant,.. HOPE\n Now you are focusing on speaker mode", context);
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
//                welcomeSpeaker(textToSpeech, "Now you are focusing on speaker mode", context);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void welcomeSpeaker(TextToSpeech mTextToSpeech, String focusOn, Context mContext){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            mTextToSpeech.speak( focusOn, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
