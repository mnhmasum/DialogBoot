package com.masum.dialogboot;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.masum.annotation.InjectDialog;
import com.masum.annotation.InjectView;

import com.masum.dialogbootloader.DialogBootLoader;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @InjectDialog(
            isCancelable = true,
            layout = R.layout.layout,
            getMessage = "Hi am a Dialog Boot to create a dialog"
    )
    public AlertDialog dialog;


    @InjectView(layout = R.layout.layout)
    View view;

    @InjectView(layout = R.layout.layout)
    View view1;

    @InjectView(layout = R.layout.layout)
    View view2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogBootLoader.bind(this, view);

        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(this);
        dialog.setView(view);
        dialog.show();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            Toast.makeText(this, "Hi there", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}
