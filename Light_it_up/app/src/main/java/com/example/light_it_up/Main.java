package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntent();//이름 가져와서 누구님 안전한 귀갓길 추천드리겠습니다! 해주싈~~?
    }
}