package com.masum.dialogboot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dialogboot.masum.com.javalib.MyOnlyJava;
import dialogboot.masum.com.mylibrary.MyLib;


public class MainActivity extends BaseActivity implements View.OnClickListener{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "" + MyLib.getString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + MyOnlyJava.getJavaString(), Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            Toast.makeText(this, "Hi there", Toast.LENGTH_SHORT).show();
            //dialog.dismiss();
        }
    }
}
