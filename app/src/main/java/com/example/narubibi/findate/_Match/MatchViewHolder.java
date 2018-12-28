package com.example.narubibi.findate._Match;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.narubibi.findate.R;

public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView matchId, matchName;
    public ImageView matchProfileImage;

    public MatchViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        matchId = itemView.findViewById(R.id.match_userid);
        matchName = itemView.findViewById(R.id.match_username);
        matchProfileImage = itemView.findViewById(R.id.match_profile_image);
    }

    @Override
    public void onClick(View v) {

    }
}
