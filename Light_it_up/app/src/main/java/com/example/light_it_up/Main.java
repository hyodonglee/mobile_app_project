package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView informName = (TextView) findViewById(R.id.name);


        Intent intent = getIntent();//이름 가져와서 누구님 안전한 귀갓길 추천드리겠습니다! 해주싈~~?
        String name = intent.getExtras().getString("name");
        name = name + "님, 안전한 귀갓길 안내하겠습니다!";
        informName.setText(name);
    }
}