package com.example.narubibi.findate.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.narubibi.findate.Activities.Functionality.SwipeActivity;
import com.example.narubibi.findate.R;
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

public class FindFragment extends Fragment {

    FloatingActionButton btnLike;

    private UserAdapter userAdapter;
    private ArrayList<User> listUsers;

    private DatabaseReference usersDb;
    private FirebaseAuth firebaseAuth;
    private String currentUId;

    private String oppositeSex = "Male";
    private String userSex = "Female";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        btnLike = view.findViewById(R.id.btn_like);

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLike.setRippleColor(getResources().getColor(R.color.white, getActivity().getTheme()));
            }
        });

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUId = firebaseAuth.getCurrentUser().getUid();

        checkUserSex();
        listUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), R.layout.item_cards, listUsers);
        InitializeSwipe(view);

        return view;

    }

    private void InitializeSwipe(View view) {
        SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);

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
                Toast.makeText(getContext(), "NOPE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                User item = (User)dataObject;
                String userId = item.getUserId();
                usersDb.child(userId).child("connections").child("like").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(getContext(), "LIKE", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
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

                        String img_url;
                        if (dataSnapshot.child("swipe_image_url").getValue() != null) {
                            img_url = dataSnapshot.child("swipe_image_url").getValue().toString();
                        } else {
                            img_url = dataSnapshot.child("profile_image_url").getValue().toString();
                        }

                        listUsers.add(i, new User(dataSnapshot.getKey(),
                                        dataSnapshot.child("name").getValue().toString(),
                                        dataSnapshot.child("sex").getValue().toString(),
                                        dataSnapshot.child("sex_preference").getValue().toString(),
                                        img_url
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

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUId).child("connections").child("like").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "Found a match!", Toast.LENGTH_LONG).show();

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
}
