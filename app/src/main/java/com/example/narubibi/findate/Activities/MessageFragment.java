package com.example.narubibi.findate.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Match.Match;
import com.example.narubibi.findate._Match.MatchAdapter;
import com.example.narubibi.findate._Match.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MessageFragment extends Fragment {
    private EditText editTextSearch;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter matchAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Match> listMatches = new ArrayList<>();
    private ArrayList<Match> filterMatches = new ArrayList<>();

    private String currentUId;
    private DatabaseReference usersDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        editTextSearch = view.findViewById(R.id.ed_message_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMatches.clear();
                for (Match match : listMatches) {
                    if (match.getUserName().toLowerCase().contains(editTextSearch.getText().toString().toLowerCase()))
                        filterMatches.add(match);
                }
                matchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = view.findViewById(R.id.recyclerView_message_match);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        matchAdapter = new MatchAdapter(getContext(), getDataSetMatches());
        recyclerView.setAdapter(matchAdapter);

        getUserMatchId();

        return view;
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = usersDb.child(currentUId).child("connections").child("match");
        matchDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String matchId = dataSnapshot.getKey();
                FetchMatchInfo(matchId);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String matchId = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference userDb;
    private void FetchMatchInfo(String key) {
        userDb = usersDb.child(key);
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

                    Match match = new Match(matchId, matchName, matchProfileImageUrl);
                    listMatches.add(match);

                    if (match.getUserName().toLowerCase().contains(editTextSearch.getText().toString().toLowerCase()))
                        filterMatches.add(match);
                    matchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Match> getDataSetMatches() {
        return filterMatches;
    }
}
