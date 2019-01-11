package com.example.narubibi.findate.Activities.Authentication;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.narubibi.findate.Activities.MainActivity;
import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class VerificationActivity extends AppCompatActivity {
    private Button btnResend;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ProgressBar progressBar;
    private TextView tvTimeCounter;
    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar)findViewById(R.id.progressBar_verification);

        tvTimeCounter = (TextView)findViewById(R.id.tv_time_count_show);
        btnResend = (Button)findViewById(R.id.btn_resend_verification);


        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth.getCurrentUser().reload();
                if(firebaseAuth.getCurrentUser().isEmailVerified()) {

                    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }
            }

        };

        waitForVerification();




    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser().isEmailVerified()){
            sendtoMainActivity();
        }
    }



    private void sendtoMainActivity() {
        Intent mainIntent = new Intent(VerificationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        return;
    }

    private void waitForVerification() {
        btnResend.setEnabled(false);
        myCountDownTimer = new MyCountDownTimer(60*1000, 1000);
        myCountDownTimer.start();
    }

    public void reSendEmail() {

        btnResend.setAllCaps(false);
        btnResend.setTextSize(25);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.getCurrentUser().reload();
                if(!firebaseAuth.getCurrentUser().isEmailVerified()){
                    firebaseAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Resend successfully",
                                                Toast.LENGTH_LONG).show();
                                        waitForVerification();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),  task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else{
                    sendtoMainActivity();
                }
            }
        });



    }


    public class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/1000);
            progressBar.setProgress(progressBar.getMax() - progress);
            String timeCountShow = Integer.toString(progress);
            tvTimeCounter.setText(timeCountShow);

            firebaseAuth.getCurrentUser().reload();
            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                sendtoMainActivity();
                myCountDownTimer.cancel();

            }
        }

        @Override
        public void onFinish() {
            btnResend.setEnabled(true);
            tvTimeCounter.setText("Resent!");

            reSendEmail();
        }
    }
}
