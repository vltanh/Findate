package com.example.narubibi.findate.Activities.Authentication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.narubibi.findate.Activities.MainActivity;
import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polyak.iconswitch.IconSwitch;
import com.tooltip.Tooltip;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/findate.appspot.com/o/ProfileImages%2FProfile-Fallback-01-01.png?alt=media&token=15756510-9c2f-42ea-8d07-e7f794d5d190";

    private AnimationDrawable animationDrawable;
    private AssetManager assetManager;

    ProgressBar progressBar;
    Handler handler = new Handler();
    private Tooltip passToolTip;

    private Button btnRegister;
    private EditText editTextEmail, editTextPassword, editTextRePassword, editTextName;

    private IconSwitch switchGender;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String email, password, repassword, name, sex, preference;
    private boolean checkSign = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.sign_progress);

        assetManager = this.getApplicationContext().getAssets();
        LinearLayout container = findViewById(R.id.linearLayout_signup);
        animationDrawable = (AnimationDrawable) container.getBackground();
        animationDrawable.setEnterFadeDuration(100);
        animationDrawable.setExitFadeDuration(1000);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
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
        editTextRePassword = findViewById(R.id.ed_signup_confirm_password);
        switchGender = findViewById(R.id.icon_switch_gender);

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmailInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });

        editTextRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRePasswordInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                repassword = s.toString();
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });





        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(ProgressBar.VISIBLE);

                IconSwitch.Checked selectedGender = switchGender.getChecked();


                password = editTextPassword.getText().toString();
                sex = selectedGender == IconSwitch.Checked.LEFT ? "Female" : "Male";
                preference = sex.equals("Male") ? "Female" : "Male";

                checkEmailInput(email);
                checkNameInput(name);
                checkRePasswordInput(repassword);

                if(!repassword.equals(password)){
                    checkSign = false;
                    TextInputLayout repasswordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_repassword);
                    repasswordLayout.setError("Your password is not matched.");
                }

                PasswordValidator passwordValidator = new PasswordValidator();
                if(!passwordValidator.validate(password)){
                    TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_password);
                    passwordLayout.setError("Your password is not suitable.");
                    editTextPassword.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Tooltip.Builder builder = new Tooltip.Builder(v, R.style.Tooltip)
                                    .setCancelable(true)
                                    .setDismissOnClick(false)
                                    .setCornerRadius(10f)
                                    .setGravity(Gravity.BOTTOM)
                                    .setText(R.string.pass_warning);
                            builder.show();
                            return true;
                        }

                    });
                    checkSign = false;
                }
                else {
                    checkSign = true;
                }

                if(checkSign) {
                    try {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Error! Can't register!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                /*Toast.makeText(getApplicationContext(), "Registered successfully. Please check your email for verification",
                                                                        Toast.LENGTH_LONG).show();*/
                                                                /*Intent verificationIntent = new Intent(RegisterActivity.this, VerificationActivity.class);
                                                                startActivity(verificationIntent);
                                                                finish();*/


                                                            }
                                                            else {
                                                                Toast.makeText(getApplicationContext(),  task.getException().getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });


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
                                            userInfo.put("swipe_image_url", DEFAULT_PROFILE_IMAGE_URL);

                                            userInfo.put("birthday", "1/1/2000");
                                            userInfo.put("phone", "");
                                            userInfo.put("job", "");
                                            userInfo.put("workplace", "");

                                            userInfo.put("visibility", true);
                                            userInfo.put("age_min", 13);
                                            userInfo.put("age_max", 60);

                                            currentUserDB.updateChildren(userInfo);

                                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error! Can't register!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
            }
        });

    }

    /* void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.progressbar_icon)
                        .setContentTitle("Email Verification")
                        .setContentText("Please verify your email address.");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }*/

    private void checkEmailInput(String s) {
        if(TextUtils.isEmpty(s)){
            TextInputLayout emailLayout = (TextInputLayout)findViewById(R.id.textlayout_signup_email);
            emailLayout.setError("Your email is empty.");
            checkSign = false;
        }
        else {
            TextInputLayout emailLayout = (TextInputLayout)findViewById(R.id.textlayout_signup_email);
            emailLayout.setError(null);
        }
    }

    private void checkPasswordInput(String s){
        if(TextUtils.isEmpty(s)){
            TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_password);
            passwordLayout.setError("Your password is weak.");
            checkSign = false;
        }
        else{
            TextInputLayout passwordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_password);
            passwordLayout.setError(null);
            checkSign = true;
        }
    }

    private void checkRePasswordInput(String s){
        if(TextUtils.isEmpty(s)){
            TextInputLayout repasswordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_repassword);
            repasswordLayout.setError("Your repassword is empty.");
            checkSign = false;
        }
        else{
            TextInputLayout repasswordLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_repassword);
            repasswordLayout.setError(null);
            checkSign = true;
        }
    }

    private void checkNameInput(String s){
        if(TextUtils.isEmpty(s)){
            TextInputLayout nameLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_name);
            nameLayout.setError("Your name is empty.");
            checkSign = false;
        }
        else{
            TextInputLayout nameLayout = (TextInputLayout)findViewById(R.id.textLayout_signup_name);
            nameLayout.setError(null);
            checkSign = true;
        }
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
