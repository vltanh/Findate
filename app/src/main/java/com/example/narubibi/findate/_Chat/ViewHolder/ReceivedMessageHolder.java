package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ReceivedMessage;

public class ReceivedMessageHolder extends MessageHolder {
    TextView nameText;
    ImageView profileImage;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.text_message_name);
        profileImage = itemView.findViewById(R.id.image_message_profile);
    }

    void bind(ReceivedMessage message) {
        super.bind(message);
        nameText.setText(message.getUserName());
        Glide.with(profileImage.getContext()).load(message.getProfileImageUrl()).into(profileImage);
    }
}
