package com.example.narubibi.findate._Chat.ViewHolder;

import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.narubibi.findate.R;
import com.example.narubibi.findate._Chat.Message;
import com.transitionseverywhere.TransitionManager;

public class ChatObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
