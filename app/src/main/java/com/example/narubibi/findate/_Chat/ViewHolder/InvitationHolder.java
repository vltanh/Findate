package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ReceivedInvitation;

public class InvitationHolder extends ChatObjectHolder {
    TextView messageText;

    InvitationHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        messageText.setOnClickListener(this);
    }

    void bind(ReceivedInvitation message) {
        super.bind(message);
        String text = "Date: " + message.getTime() +
                "\nPlace: " + message.getPlace() +
                "\n" + message.getMessage();
        messageText.setText(text);
    }
}
