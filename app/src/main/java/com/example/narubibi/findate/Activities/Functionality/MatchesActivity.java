package com.example.narubibi.findate.Activities.Functionality;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Match.Match;
import com.example.narubibi.findate._Match.MatchAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter matchAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Match> listMatches = new ArrayList<>();

    private String currentUId;
    private DatabaseReference usersDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.recycler_view_matches);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(MatchesActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        matchAdapter = new MatchAdapter(MatchesActivity.this, getDataSetMatches());
        recyclerView.setAdapter(matchAdapter);

        getUserMatchId();
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = usersDb.child(currentUId).child("connections").child("match");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchMatchInfo(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInfo(String key) {
        DatabaseReference userDb = usersDb.child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String matchId = dataSnapshot.getKey();
                    String matchName = "Findate User", matchProfileImageUrl = "";
                    if (dataSnapshot.child("name").getValue() != null) {
                        matchName = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("profile_image_url").getValue() != null) {
                        matchProfileImageUrl = dataSnapshot.child("profile_image_url").getValue().toString();
                    }

                    listMatches.add(new Match(matchId, matchName, matchProfileImageUrl));
                    matchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Match> getDataSetMatches() {
        return listMatches;
    }
}
