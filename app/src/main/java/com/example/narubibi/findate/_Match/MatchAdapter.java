package com.example.narubibi.findate._Match;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

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
        private String userId, chatId, lastMessage;

        public TextView matchId, matchName;
        public ImageView matchProfileImage;

        DatabaseReference chatDb;
        Query lastQuery;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            matchId = itemView.findViewById(R.id.match_userid);
            matchName = itemView.findViewById(R.id.match_username);
            matchProfileImage = itemView.findViewById(R.id.iv_match_user_image);

            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void bind(Match match) {
            id = match.getUserId();
            name = match.getUserName();
            imgUrl = match.getProfileImageUrl();
            getChatId();
            matchName.setText(name);
            Glide.with(context).load(imgUrl).into(matchProfileImage);
        }

        private void getChatId() {
            chatDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections").child("match").child(id).child("chat_id");
            chatDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        chatId = dataSnapshot.getValue().toString();
                        getLastMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getLastMessage() {
            lastQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).orderByKey().limitToLast(1);
            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>)((Map<String, Object>) dataSnapshot.getValue()).values().toArray()[0];

                        lastMessage = "";
                        if (map.get("sent_id") != null) {
                            if (map.get("sent_id").equals(userId))
                                lastMessage += "You: ";
                            else lastMessage += name + ": ";
                        }

                        if (map.get("type") != null && map.get("type").equals("sticker")) {
                            lastMessage += "Sent a sticker.";
                        }
                        else if (map.get("content") != null) {
                            lastMessage += map.get("content").toString();
                        }
                        matchId.setText(lastMessage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Handle possible errors.
                }
            });
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
