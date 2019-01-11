package com.example.narubibi.findate.Activities.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    //private Toolbar toolbar;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    private EditText edEmail;
    private Button btnResetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        /*toolbar = (Toolbar)findViewById(R.id.reset_pass_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ahihi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        progressBar = findViewById(R.id.resetPass_progressBar);


        firebaseAuth = FirebaseAuth.getInstance();

        edEmail = findViewById(R.id.ed_reset_pass_email);
        btnResetPass = findViewById(R.id.btn_reset_password);

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(edEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    Intent loginIntent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                }
                                else {
                                    Log.e("Error reset email: ", task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }
}
