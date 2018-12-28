package com.example.narubibi.findate._Match;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ChatActivity;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter {
    private ArrayList<Match> listMatches;
    private Context context;

    public MatchAdapter(Context context, ArrayList<Match> listMatches) {
        this.listMatches = listMatches;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new MatchViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MatchViewHolder)viewHolder).bind(listMatches.get(i));
    }

    @Override
    public int getItemCount() {
        return listMatches.size();
    }

    private class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String id, name, imgUrl;

        public TextView matchId, matchName;
        public ImageView matchProfileImage;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            matchId = itemView.findViewById(R.id.match_userid);
            matchName = itemView.findViewById(R.id.match_username);
            matchProfileImage = itemView.findViewById(R.id.match_profile_image);
        }

        public void bind(Match match) {
            id = match.getUserId();
            name = match.getUserName();
            imgUrl = match.getProfileImageUrl();

            matchId.setText(id);
            matchName.setText(name);
            Glide.with(context).load(imgUrl).into(matchProfileImage);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("matchId", id);
            intent.putExtra("matchName", name);
            intent.putExtra("imgUrl", imgUrl);
            v.getContext().startActivity(intent);
        }
    }
}
