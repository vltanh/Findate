package com.example.narubibi.findate.Activities.Functionality;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.narubibi.findate.Activities.Authentication.LoginActivity;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Match.MatchesActivity;
import com.example.narubibi.findate._User.User;
import com.example.narubibi.findate._User.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SwipeActivity extends AppCompatActivity {
    private static final int ACTIVITY_SETTINGS = 1;
    private UserAdapter userAdapter;
    private ArrayList<User> listUsers;

    private DatabaseReference usersDb;
    private FirebaseAuth firebaseAuth;
    private String currentUId;

    private String oppositeSex = "Male";
    private String userSex = "Female";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUId = firebaseAuth.getCurrentUser().getUid();

        checkUserSex();
        listUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, R.layout.item_cards, listUsers);
        InitializeSwipe();
    }

    private void InitializeSwipe() {
        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(userAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                listUsers.remove(0);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                User item = (User)dataObject;
                String userId = item.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(SwipeActivity.this, "NOPE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                User item = (User)dataObject;
                String userId = item.getUserId();
                usersDb.child(userId).child("connections").child("like").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(SwipeActivity.this, "LIKE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(SwipeActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUId).child("connections").child("like").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SwipeActivity.this, "Found a match!", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("match").child(currentUId).child("chat_id").setValue(key);
                    usersDb.child(currentUId).child("connections").child("match").child(dataSnapshot.getKey()).child("chat_id").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserSex() {
        DatabaseReference userDb = usersDb.child(currentUId);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        oppositeSex = dataSnapshot.child("sex_preference").getValue().toString();
                        getOppositeSexUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOppositeSexUsers() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() &&
                        !dataSnapshot.getKey().equals(currentUId) &&
                        !dataSnapshot.child("connections").child("nope").hasChild(currentUId) &&
                        !dataSnapshot.child("connections").child("like").hasChild(currentUId)) {

                    if (oppositeSex.equals("Both") || dataSnapshot.child("sex").getValue().toString().equals(oppositeSex)) {
                        int n = listUsers.size();
                        int i = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                             i = ThreadLocalRandom.current().nextInt(0, n + 1);
                        }
                        listUsers.add(i, new User(dataSnapshot.getKey(),
                                        dataSnapshot.child("name").getValue().toString(),
                                        dataSnapshot.child("sex").getValue().toString(),
                                        dataSnapshot.child("sex_preference").getValue().toString(),
                                        dataSnapshot.child("profile_image_url").getValue().toString()
                                )
                        );
                        userAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(SwipeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(SwipeActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(SwipeActivity.this, MatchesActivity.class);
        startActivity(intent);
    }
}
