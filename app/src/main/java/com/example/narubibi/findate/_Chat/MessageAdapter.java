package com.example.narubibi.findate._Chat;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int SENT_STICKER = 2;
    private static final int SENT_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 0;
    private static final int RECEIVED_STICKER = 3;
    private ArrayList<Message> listMessages;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> listMessages) {
        this.listMessages = listMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return listMessages.get(position).getMessageType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == SENT_MESSAGE) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_sent_message, viewGroup, false);
            return new SentMessageHolder(view);
        } else if (viewType == RECEIVED_MESSAGE) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_received_message, viewGroup, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == SENT_STICKER) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_sent_sticker, viewGroup, false);
            return new SentStickerHolder(view);
        }
        else if (viewType == RECEIVED_STICKER) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_received_sticker, viewGroup, false);
            return new ReceivedStickerHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Message chatItem = listMessages.get(i);
        if (viewHolder.getItemViewType() == SENT_MESSAGE) {
            ((SentMessageHolder) viewHolder).bind(chatItem);
        } else if (viewHolder.getItemViewType() == RECEIVED_MESSAGE) {
            ((ReceivedMessageHolder) viewHolder).bind((ReceivedMessage)chatItem);
        } else if (viewHolder.getItemViewType() == SENT_STICKER) {
            ((SentStickerHolder) viewHolder).bind(chatItem);
        }
        else if (viewHolder.getItemViewType() == RECEIVED_STICKER) {
            ((ReceivedStickerHolder) viewHolder).bind((ReceivedSticker) chatItem);
        }
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Message message;

        ConstraintLayout constraintLayout;
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.transition_viewgroup);
            messageText = itemView.findViewById(R.id.text_message_body);
            messageText.setOnClickListener(this);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            this.message = message;
            messageText.setText(message.getMessage());
            timeText.setText(message.getTimestamp());
            timeText.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            timeText.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    timeText.setVisibility(View.GONE);
                }
            }, 1500);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Message message;

        ConstraintLayout constraintLayout;
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.transition_viewgroup);
            messageText = itemView.findViewById(R.id.text_message_body);
            messageText.setOnClickListener(this);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(ReceivedMessage message) {
            this.message = message;
            messageText.setText(message.getMessage());
            timeText.setText(message.getTimestamp());
            timeText.setVisibility(View.GONE);
            nameText.setText(message.getUserName());
            Glide.with(context).load(message.getProfileImageUrl()).into(profileImage);
        }

        @Override
        public void onClick(View v) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            timeText.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    timeText.setVisibility(View.GONE);
                }
            }, 1500);
        }
    }

    private class SentStickerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Message message;

        ConstraintLayout constraintLayout;
        TextView timeText;
        ImageView messageSticker;

        SentStickerHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.transition_viewgroup);
            messageSticker = itemView.findViewById(R.id.text_message_body);
            messageSticker.setOnClickListener(this);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            this.message = message;
            Glide.with(context).load(message.getMessage()).into(messageSticker);
            timeText.setText(message.getTimestamp());
            timeText.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            timeText.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    timeText.setVisibility(View.GONE);
                }
            }, 1500);
        }
    }

    private class ReceivedStickerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Message message;

        ConstraintLayout constraintLayout;
        TextView timeText, nameText;
        ImageView profileImage, messageSticker;

        ReceivedStickerHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.transition_viewgroup);
            messageSticker = itemView.findViewById(R.id.text_message_body);
            messageSticker.setOnClickListener(this);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(ReceivedSticker message) {
            this.message = message;
            Glide.with(context).load(message.getMessage()).into(messageSticker);
            timeText.setText(message.getTimestamp());
            timeText.setVisibility(View.GONE);
            nameText.setText(message.getUserName());
            Glide.with(context).load(message.getProfileImageUrl()).into(profileImage);
        }

        @Override
        public void onClick(View v) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            timeText.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    timeText.setVisibility(View.GONE);
                }
            }, 1500);
        }
    }
}
