package com.example.li.gank.ui.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.li.gank.BuildConfig;
import com.example.li.gank.R;
import com.example.li.gank.widget.SplashView;

import java.util.Random;

import butterknife.Bind;

/**
 * Created by Li on 2017/10/24.
 */

public class SplashActivity extends MVPBaseActivity {

    private static final String TAG="SplashActivity";

    private Handler mHandler = new Handler();

    @Bind(R.id.splash_view)
    SplashView splash_view;
    @Bind(R.id.tv_splash_info)
    TextView tv_splash_info;


    protected int provideContentViewId() {
        return R.layout.activity_splash;
    }

    protected void onStart(){
        super.onStart();

        AssetManager mgr=getAssets();
        Typeface tf=Typeface.createFromAsset(mgr,"fonts/rm_albion.ttf");
        tv_splash_info.setTypeface(tf);
        startLoadingData();

    }
    private void startLoadingData(){
        Random random=new Random();
        mHandler.postDelayed(this::onLoadingDataEnded,1000+random.nextInt(2000));
    }
    private void onLoadingDataEnded(){
        splash_view.splashAndDisappear(new SplashView.ISplashListener() {
            @Override
            public void onStart() {
                if (BuildConfig.DEBUG){
                    Log.d(TAG,"splash started");
                }
            }

            @Override
            public void onUpdate(float completionFraction) {
                if (BuildConfig.DEBUG){
                    Log.d(TAG,"splash at"+String.format("%.2f",(completionFraction*100))+"%");
                }
            }

            @Override
            public void onEnd() {
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "splash ended");
                }
                splash_view=null;
                goToMain();
            }
        });
    }
    public void goToMain(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
    public void startActivity(Intent intent){
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,0);
    }



}
