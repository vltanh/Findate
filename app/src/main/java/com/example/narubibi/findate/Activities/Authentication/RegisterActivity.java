package com.example.narubibi.findate.Activities.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/findate.appspot.com/o/ProfileImages%2FProfile-Fallback-01-01.png?alt=media&token=15756510-9c2f-42ea-8d07-e7f794d5d190";
    private Button btnRegister;
    private EditText editTextEmail, editTextPassword, editTextName;

    private RadioGroup radioGroupGender;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        btnRegister = findViewById(R.id.register_btn_register);

        editTextName = findViewById(R.id.register_edittext_name);
        editTextEmail = findViewById(R.id.register_edittext_email);
        editTextPassword = findViewById(R.id.register_edittext_password);

        radioGroupGender = findViewById(R.id.radiogroup_gender);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                final RadioButton selectedRadioBtn = findViewById(selectedId);
                if (selectedRadioBtn.getText() == null) { return; }

                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();
                final String name = editTextName.getText().toString();
                final String sex = getUserSex(selectedRadioBtn);
                final String preference = sex.equals("Male") ? "Female" : "Male";

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
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
}
