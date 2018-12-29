package com.example.narubibi.findate._Chat;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.transitionseverywhere.TransitionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

    private class ChatObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Message message;

        ConstraintLayout constraintLayout;
        TextView timeText;

        ChatObjectHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.transition_viewgroup);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            this.message = message;
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

    private class MessageHolder extends ChatObjectHolder {
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

    private class SentMessageHolder extends MessageHolder {
        SentMessageHolder(View itemView) {
            super(itemView);
        }
    }

    private class ReceivedMessageHolder extends MessageHolder {
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
            Glide.with(context).load(message.getProfileImageUrl()).into(profileImage);
        }
    }

    private class StickerHolder extends ChatObjectHolder {
        ImageView stickerImg;

        StickerHolder(View itemView) {
            super(itemView);
            stickerImg = itemView.findViewById(R.id.text_message_body);
            stickerImg.setOnClickListener(this);
        }

        void bind(Message message) {
            super.bind(message);
            Glide.with(context).load(message.getMessage()).into(stickerImg);
        }
    }

    private class SentStickerHolder extends StickerHolder {
        SentStickerHolder(View itemView) {
            super(itemView);
        }
    }

    private class ReceivedStickerHolder extends StickerHolder {
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
            Glide.with(context).load(message.getProfileImageUrl()).into(profileImage);
        }
    }

    private class InvitationHolder extends ChatObjectHolder {
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

    private class ReceivedInvitationHolder extends InvitationHolder {
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
            Glide.with(context).load(message.getProfileImageUrl()).into(profileImage);

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
                Toast.makeText(context, btn.getText(), Toast.LENGTH_LONG).show();

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
            newDate.put("timestamp", dateFormat.format(Calendar.getInstance().getTime()));
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
            Toast.makeText(context, message.getChatId(), Toast.LENGTH_LONG).show();
            DatabaseReference invitationDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(message.getChatId()).child(message.getInvitationId());
            invitationDb.child("status").setValue(response);
        }
    }
}
