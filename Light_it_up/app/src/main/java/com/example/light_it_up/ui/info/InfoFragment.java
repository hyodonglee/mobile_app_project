package com.example.light_it_up.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.light_it_up.R;

public class InfoFragment extends Fragment {

    String name;

    public InfoFragment(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        Button btn = (Button)view.findViewById(R.id.bnt);

        if(getArguments() != null) {
            name = getArguments().getString("name");
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), name,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}