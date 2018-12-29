package com.example.narubibi.findate._Chat.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.Message;

public class StickerHolder extends ChatObjectHolder {
    ImageView stickerImg;

    StickerHolder(View itemView) {
        super(itemView);
        stickerImg = itemView.findViewById(R.id.text_message_body);
        stickerImg.setOnClickListener(this);
    }

    void bind(Message message) {
        super.bind(message);
        Glide.with(stickerImg.getContext()).load(message.getMessage()).into(stickerImg);
    }
}

