package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.ReceivedSticker;

public class ReceivedStickerHolder extends StickerHolder {
    TextView nameText;
    ImageView profileImage;

    ReceivedStickerHolder(View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.text_message_name);
        profileImage = itemView.findViewById(R.id.image_message_profile);
    }

    void bind(ReceivedSticker message) {
        super.bind(message);
        nameText.setText(message.getUserName());
        Glide.with(profileImage.getContext()).load(message.getProfileImageUrl()).into(profileImage);
    }
}
