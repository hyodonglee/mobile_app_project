package com.example.light_it_up.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.light_it_up.Login;
import com.example.light_it_up.R;
import com.example.light_it_up.Login.returnProfile;
import com.kakao.auth.Session;
import com.kakao.usermgmt.response.MeV2Response;

import java.util.HashMap;

public class InfoFragment extends Fragment {

    String name;
    String email;
    String tel;
    String gender;
    String profile;

    ImageView ivProfile;

    HashMap temp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);
//        Button btn = (Button)view.findViewById(R.id.bnt);
//
//        if(getArguments() != null) {
//            name = getArguments().getString("name");
//        }
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("테스트"+getArguments());
//                Toast.makeText(getContext(), name,Toast.LENGTH_SHORT).show();
//            }
//        });

        //사용자 정보 가져오기
        returnProfile info = new returnProfile();
        temp = info.profile();

        System.out.println("널이다 널"+temp);

        for(Object key : temp.keySet()){
            if(key=="name") {
                name = (String) temp.get(key);
            }
            else if(key=="email"){
                email = (String) temp.get(key);
            }
            else if(key=="tel"){
                tel = (String) temp.get(key);
            }
            else if(key=="gender"){
                gender = (String) temp.get(key);
            }
            else if(key=="profile"){
                profile = (String) temp.get(key);
            }
        }
        Toast.makeText(getContext(),name,Toast.LENGTH_SHORT).show();

        //Glide.with(this).load(profile).into(ivProfile);

        return view;
    }
}