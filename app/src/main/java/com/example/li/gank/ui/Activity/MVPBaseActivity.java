package com.example.li.gank.ui.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Li on 2017/10/26.
 */

public abstract class MVPBaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());//布局
        ButterKnife.bind(this);
    }
    abstract protected int provideContentViewId();
}
