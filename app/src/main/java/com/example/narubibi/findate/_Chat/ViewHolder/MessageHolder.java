package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.Message;

public class MessageHolder extends ChatObjectHolder {
    TextView messageText;

    MessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        messageText.setOnClickListener(this);
    }

    void bind(Message message) {
        super.bind(message);
        messageText.setText(message.getMessage());
    }
}
