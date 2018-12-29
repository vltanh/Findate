package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ReceivedInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.transitionseverywhere.TransitionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReceivedInvitationHolder extends InvitationHolder {
    TextView nameText;
    ImageView profileImage;

    boolean isShowingResponseBtn = false;

    Button btnAccept, btnReject;

    ReceivedInvitationHolder(View itemView) {
        super(itemView);

        nameText = itemView.findViewById(R.id.text_message_name);
        profileImage = itemView.findViewById(R.id.image_message_profile);

        btnAccept = itemView.findViewById(R.id.btn_Accept);
        btnReject = itemView.findViewById(R.id.btn_Reject);

        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);
    }

    void bind(ReceivedInvitation message) {
        super.bind(message);

        nameText.setText(message.getUserName());
        Glide.with(profileImage.getContext()).load(message.getProfileImageUrl()).into(profileImage);

        TransitionManager.beginDelayedTransition(constraintLayout);
        if (message.getStatus().equals("accepted")) {
            btnAccept.setClickable(false);
            btnReject.setClickable(false);
            btnAccept.setText("Accepted");
            btnReject.setBackgroundColor(0x2c3e50);
        } else if (message.getStatus().equals("rejected")) {
            btnAccept.setClickable(false);
            btnReject.setClickable(false);
            btnReject.setText("Rejected");
            btnAccept.setBackgroundColor(0x2c3e50);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button btn = (Button) v;

            TransitionManager.beginDelayedTransition(constraintLayout);
            if (btn.getText().equals("Accept")) {
                btnAccept.setClickable(false);
                btnReject.setClickable(false);
                btnAccept.setText("Accepted");
                btnReject.setBackgroundColor(0x2c3e50);
                acceptInvitation((ReceivedInvitation) message);
            } else if (btn.getText().equals("Reject")) {
                btnAccept.setClickable(false);
                btnReject.setClickable(false);
                btnReject.setText("Rejected");
                btnAccept.setBackgroundColor(0x2c3e50);
                setInvitationStatus((ReceivedInvitation)message, "rejected");
            }
        }
        else {
            isShowingResponseBtn = !isShowingResponseBtn;
            if (isShowingResponseBtn) {
                TransitionManager.beginDelayedTransition(constraintLayout);
                btnAccept.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            } else {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }
        }
    }

    private void acceptInvitation(ReceivedInvitation message) {
        final String acceptedId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String sentId = message.getUserId();

        setInvitationStatus(message, "accepted");

        DatabaseReference sentDateDb = FirebaseDatabase.getInstance().getReference().child("Users").child(sentId).child("date");
        DatabaseReference acceptedDateDb = FirebaseDatabase.getInstance().getReference().child("Users").child(acceptedId).child("date");

        String date = message.getTime();
        String place = message.getPlace();

        Map<String, Object> newDate = new HashMap<>();
        newDate.put("timestamp", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(Calendar.getInstance().getTime()));
        newDate.put("date", date);
        newDate.put("place", place);

        DatabaseReference newDateDb = sentDateDb.push();
        newDate.put("date_id", acceptedId);
        newDateDb.setValue(newDate);

        newDateDb = acceptedDateDb.push();
        newDate.put("date_id", sentId);
        newDateDb.setValue(newDate);
    }

    private void setInvitationStatus(ReceivedInvitation message, String response) {
        DatabaseReference invitationDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(message.getChatId()).child(message.getInvitationId());
        invitationDb.child("status").setValue(response);
    }
}
