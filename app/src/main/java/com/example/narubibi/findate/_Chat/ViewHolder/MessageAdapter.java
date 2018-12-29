package com.example.narubibi.findate._Chat.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.Message;
import com.example.narubibi.findate._Chat.ReceivedInvitation;
import com.example.narubibi.findate._Chat.ReceivedMessage;
import com.example.narubibi.findate._Chat.ReceivedSticker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final int SENT_STICKER = 2;
    private static final int SENT_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 0;
    private static final int RECEIVED_STICKER = 3;
    private static final int RECEIVED_INVITATION = 4;
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
        } else if (viewType == RECEIVED_STICKER) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_received_sticker, viewGroup, false);
            return new ReceivedStickerHolder(view);
        } else if (viewType == RECEIVED_INVITATION) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_received_invitation, viewGroup, false);
            return new ReceivedInvitationHolder(view);
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
        } else if (viewHolder.getItemViewType() == RECEIVED_STICKER) {
            ((ReceivedStickerHolder) viewHolder).bind((ReceivedSticker) chatItem);
        } else if (viewHolder.getItemViewType() == RECEIVED_INVITATION) {
            ((ReceivedInvitationHolder) viewHolder).bind((ReceivedInvitation) chatItem);
        }
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }
}
