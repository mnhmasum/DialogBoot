package com.masum.dialogboot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.masum.dialogbootloader.DialogBootLoader;

/**
 * Created by mac on 3/28/18.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DialogBootLoader.bind(this);
    }
}
