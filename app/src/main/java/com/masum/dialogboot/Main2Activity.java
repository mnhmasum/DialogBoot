package com.masum.dialogboot;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import dialogboot.InjectDialog;
import dialogboot.InjectView;

public class Main2Activity extends BaseActivity implements View.OnClickListener{

    /*@InjectDialog(isCancelable = false,
            getMessage = "Hi Second"
    )
    public AlertDialog dialog;

*/
   /* @InjectView(layout = R.layout.layout)
    View view;

    @InjectView(layout = R.layout.layout)
    View view1;*/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*DialogBootLoader.bind(this, view);
        Button button = (Button) view.findViewById(R.id.button2);
        button.setOnClickListener(this);
        dialog.setView(view);*/
        //dialog.show();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            Toast.makeText(this, "Hi therer", Toast.LENGTH_SHORT).show();
            //dialog.dismiss();
        }
    }
}
