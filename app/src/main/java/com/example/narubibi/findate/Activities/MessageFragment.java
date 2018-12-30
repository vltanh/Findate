package com.example.narubibi.findate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Match.MatchesActivity;

public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        Button btn = view.findViewById(R.id.btn_match);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getContext(), MatchesActivity.class);
                getContext().startActivity(settingIntent);
            }
        });

        return view;
    }
}
