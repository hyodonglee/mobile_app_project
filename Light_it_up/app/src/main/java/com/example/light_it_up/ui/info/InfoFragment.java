package com.example.light_it_up.ui.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.light_it_up.Login;
import com.example.light_it_up.R;
import com.example.light_it_up.Login.returnProfile;
import com.example.light_it_up.SignIn;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.example.light_it_up.Login.*;

import java.util.HashMap;

public class InfoFragment extends Fragment {

    MeV2Response result;

    String name;
    String email;
    String tel;
    String gender;
    String profile;

    Button logout;
    ImageView ivProfile;
    TextView txName, txMail, txGender, txTel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        txName = (TextView) view.findViewById(R.id.tvNickname);
        txMail = (TextView) view.findViewById(R.id.tvEmail);
        txGender = (TextView) view.findViewById(R.id.tvGender);

        //사용자 정보 가져오기
        result=Login.myInfo();

        System.out.println("로그인에서 가져옴"+result);

        txName.setText(result.getNickname());
        txMail.setText(result.getKakaoAccount().getEmail());
        txGender.setText(result.getKakaoAccount().getGender().getValue());


        logout = (Button)view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(getContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

        return view;
    }
}