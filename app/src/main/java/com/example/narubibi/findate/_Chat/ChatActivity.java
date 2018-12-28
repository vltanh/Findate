package com.example.narubibi.findate._Chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.narubibi.findate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private RecyclerView recyclerView;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private EditText textMessage;
    private Button btnSendMessage, btnSendSticker;

    private ArrayList<Message> listMessages = new ArrayList<>();

    private String chatId;
    private String sentId;

    private String receivedId;
    private String receivedName;
    private String receivedImgUrl;

    private DatabaseReference userDb, chatDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receivedId = getIntent().getStringExtra("matchId");
        receivedName = getIntent().getStringExtra("matchName");
        receivedImgUrl = getIntent().getStringExtra("imgUrl");

        sentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(sentId).child("connections").child("match").child(receivedId).child("chat_id");
        chatDb = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();

        recyclerView = findViewById(R.id.reycler_view_messages);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(ChatActivity.this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(ChatActivity.this, getMessages());
        recyclerView.setAdapter(messageAdapter);

        textMessage = findViewById(R.id.edittext_chatbox);
        btnSendMessage = findViewById(R.id.button_chatbox_send);
        btnSendSticker = findViewById(R.id.button_chatbox_sticker);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        btnSendSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSticker();
            }
        });
    }

    private void sendSticker() {
        String message = "https://firebasestorage.googleapis.com/v0/b/findate.appspot.com/o/Stickers%2Fqoobee_001.gif?alt=media&token=2fbf0389-0ab9-4546-b370-9c5967a1b282";
        if (!message.isEmpty()) {
            DatabaseReference newMessageDb = chatDb.push();
            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("sent_id", sentId);
            newMessage.put("timestamp", dateFormat.format(Calendar.getInstance().getTime()));
            newMessage.put("content", message);
            newMessage.put("type", "sticker");
            newMessageDb.setValue(newMessage);
        }
        textMessage.setText(null);
    }

    private void sendMessage() {
        String message = textMessage.getText().toString();
        if (!message.isEmpty()) {
            DatabaseReference newMessageDb = chatDb.push();
            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("sent_id", sentId);
            newMessage.put("timestamp", dateFormat.format(Calendar.getInstance().getTime()));
            newMessage.put("content", message);
            newMessageDb.setValue(newMessage);
        }
        textMessage.setText(null);
    }

    private void getChatId() {
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    chatDb = chatDb.child(chatId);
                    fetchMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchMessages() {
        chatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = null;
                    String sentUserId = null;
                    String sentTime = null;

                    if (dataSnapshot.child("content").getValue() != null) {
                        message = dataSnapshot.child("content").getValue().toString();
                    }
                    if (dataSnapshot.child("sent_id").getValue() != null) {
                        sentUserId = dataSnapshot.child("sent_id").getValue().toString();
                    }
                    if (dataSnapshot.child("timestamp").getValue() != null) {
                        sentTime = dataSnapshot.child("timestamp").getValue().toString();
                    }

                    if (message != null && sentUserId != null && sentTime != null) {
                        boolean currentUser = sentUserId.equals(sentId);
                        if (currentUser) {
                            if (dataSnapshot.child("type").getValue() != null) {
                                if (dataSnapshot.child("type").getValue().equals("sticker")) {
                                    listMessages.add(0, new SentSticker(sentUserId, message, sentTime));
                                }
                            }
                            else {
                                listMessages.add(0, new Message(sentUserId, message, sentTime));
                            }
                        }
                        else {
                            if (dataSnapshot.child("type").getValue() != null) {
                                if (dataSnapshot.child("type").getValue().equals("sticker")) {
                                    listMessages.add(0, new ReceivedSticker(sentUserId, receivedName, receivedImgUrl, message, sentTime));
                                }
                            }
                            else {
                                listMessages.add(0, new ReceivedMessage(sentUserId, receivedName, receivedImgUrl, message, sentTime));
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
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

    private ArrayList<Message> getMessages() {
        return listMessages;
    }
}
