package com.example.narubibi.findate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.narubibi.findate.Activities.Functionality.SettingsActivity;
import com.example.narubibi.findate.R;

public class ProfileFragment extends Fragment {

    FloatingActionButton btnSetting;
    FloatingActionButton btnEditInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnSetting = view.findViewById(R.id.btn_Settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getContext(), SettingsActivity.class);
                getContext().startActivity(settingIntent);
            }
        });

        btnEditInfo = view.findViewById(R.id.btn_adjustProfile);
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editInfoIntent = new Intent(getContext(), EditInfoActivity.class);
                getContext().startActivity(editInfoIntent);
            }
        });


        return view;
    }
}
