package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class SignIn extends AppCompatActivity {
    String strNickname, strProfile, strEmail, strGender;
    EditText strPhone;
    String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_next);

        TextView tvNickname = findViewById(R.id.tvNickname);
        ImageView ivProfile = findViewById(R.id.ivProfile);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvGender = findViewById(R.id.tvGender);
        Button btnLogout = findViewById(R.id.btnLogout);
        Intent intent = getIntent();

        strNickname = intent.getStringExtra("name");
        strProfile = intent.getStringExtra("profile");
        strEmail = intent.getStringExtra("email");
        strGender = intent.getStringExtra("gender");
        strPhone = (EditText)findViewById(R.id.EditPhone);
        phoneNum = strPhone.toString();

        tvNickname.setText(strNickname);
        tvEmail.setText(strEmail);
        tvGender.setText(strGender);

        Glide.with(this).load(strProfile).into(ivProfile); //프로필 사진 url을 사진으로 보여줌
        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(SignIn.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

    }


    public void signUp(View view) {

        Intent intent = new Intent(getApplicationContext(), Main.class);
        System.out.println(strNickname);
        intent.putExtra("name",strNickname);
        intent.putExtra("phone", phoneNum);
        Toast.makeText(getApplicationContext(), "정상적으로 회원가입 되었습니다.",Toast.LENGTH_SHORT).show();

        //디비 처리 해야함~~~~~~~~~~~~~~
        startActivity(intent);
    }
}