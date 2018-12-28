package com.example.narubibi.findate.Activities.Authentication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.narubibi.findate.Activities.Functionality.SwipeActivity;
import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polyak.iconswitch.IconSwitch;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/findate.appspot.com/o/ProfileImages%2FProfile-Fallback-01-01.png?alt=media&token=15756510-9c2f-42ea-8d07-e7f794d5d190";

    private AnimationDrawable animationDrawable;
    private AssetManager assetManager;

    ProgressBar progressBar;
    Handler handler = new Handler();

    private Button btnRegister;
    private EditText editTextEmail, editTextPassword, editTextName;

    private IconSwitch switchGender;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = (ProgressBar)findViewById(R.id.sign_progress);

        assetManager = this.getApplicationContext().getAssets();
        LinearLayout container = (LinearLayout) findViewById(R.id.linearLayout_signup);
        animationDrawable = (AnimationDrawable) container.getBackground();
        animationDrawable.setEnterFadeDuration(100);
        animationDrawable.setExitFadeDuration(1000);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, SwipeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        btnRegister = findViewById(R.id.btn_signup);

        editTextName = findViewById(R.id.ed_signup_name);
        editTextEmail = findViewById(R.id.ed_signup_email);
        editTextPassword = findViewById(R.id.ed_signup_password);

        switchGender = findViewById(R.id.icon_switch_gender);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);

                IconSwitch.Checked selectedGender = switchGender.getChecked();

                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();
                final String name = editTextName.getText().toString();
                final String sex = selectedGender == IconSwitch.Checked.LEFT ? "Female" : "Male";
                final String preference = sex.equals("Male") ? "Female" : "Male";

                try {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(ProgressBar.GONE);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Error! Can't register!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String userId = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDB = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Users")
                                                .child(userId);

                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("name", name);
                                        userInfo.put("sex", sex);
                                        userInfo.put("sex_preference", preference);
                                        userInfo.put("profile_image_url", DEFAULT_PROFILE_IMAGE_URL);
                                        currentUserDB.updateChildren(userInfo);
                                    }
                                }
                            });
                }
                catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "Error! Can't register!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    @NonNull
    private String getUserSex(RadioButton selectedRadioBtn) {
        return selectedRadioBtn.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }
}
