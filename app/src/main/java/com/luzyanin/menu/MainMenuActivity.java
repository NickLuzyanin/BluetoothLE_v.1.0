package com.luzyanin.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.luzyanin.bletester.DeviceConnect;
import com.luzyanin.bletester.MainActivity;
import com.xinzhongxinbletester.R;



public class MainMenuActivity extends Activity implements View.OnClickListener {

    //private Button btn1;
    private Button btn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Start the application
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Регистрация приборов учета");
        //btn1 = (Button)findViewById(R.id.button_01);
        btn2 = (Button)findViewById(R.id.button_02);
        //btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {


            case R.id.button_02:
                Intent intAdd = new Intent(MainMenuActivity.this,MainActivity.class);
                startActivity(intAdd);
                break;
        }
    }
}