package com.jhyang12345.jariyo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    Animation animFadein;

    @BindView(R.id.fullscreen_content)
    TextView fullscreenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        fullscreenTextView.startAnimation(animFadein);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);

                startActivity(mainIntent);
                finish();
            }

        }, 3000L);
//        finish();


    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
