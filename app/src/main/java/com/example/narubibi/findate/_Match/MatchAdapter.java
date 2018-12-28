package com.example.narubibi.findate._Match;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolder> {
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
    public void onBindViewHolder(@NonNull MatchViewHolder matchViewHolder, int i) {
        matchViewHolder.matchId.setText(listMatches.get(i).getUserId());
        matchViewHolder.matchName.setText(listMatches.get(i).getUserName());
        Glide.with(context).load(listMatches.get(i).getProfileImageUrl()).into(matchViewHolder.matchProfileImage);
    }

    @Override
    public int getItemCount() {
        return listMatches.size();
    }
}
