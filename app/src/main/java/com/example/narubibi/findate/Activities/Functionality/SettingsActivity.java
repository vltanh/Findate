package com.example.narubibi.findate.Activities.Functionality;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQ_PROFILE_IMAGE = 1;

    private ImageView imageViewProfilePicture;
    private EditText editTextName, editTextPhone;
    private RadioGroup radioGroupPreference;
    private Button btnConfirm, btnBack;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDb;

    private String userId, name, sex, preference, phone, profileImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageViewProfilePicture = findViewById(R.id.profile_image);
        
        editTextName = findViewById(R.id.user_name);
        editTextPhone = findViewById(R.id.phone);
        
        radioGroupPreference = findViewById(R.id.radiogroup_preference);
        
        btnConfirm = findViewById(R.id.btn_confirm);
        btnBack = findViewById(R.id.btn_back);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();
        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_PROFILE_IMAGE);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });
    }

    private void getUserInfo() {
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        editTextName.setText(name);
                    }
                    if (map.get("sex") != null) {
                        sex = map.get("sex").toString();
                    }
                    if (map.get("sex_preference") != null) {
                        preference = map.get("sex_preference").toString();
                        HashMap sexPreferences = new HashMap() {
                            {
                                put("Male", R.id.radiobtn_male);
                                put("Female", R.id.radiobtn_female);
                                put("Both", R.id.radiobtn_both);
                            }
                        };
                        radioGroupPreference.check((int)sexPreferences.get(preference));
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        editTextPhone.setText(phone);
                    }
                    if (map.get("profile_image_url") != null) {
                        profileImageUrl = map.get("profile_image_url").toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(imageViewProfilePicture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        name = editTextName.getText().toString();
        phone = editTextPhone.getText().toString();
        
        int selectedId = radioGroupPreference.getCheckedRadioButtonId();
        final RadioButton selectedRadioBtn = findViewById(selectedId);
        if (selectedRadioBtn.getText() == null) { return; }
        preference = getUserPreference(selectedRadioBtn);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("sex_preference", preference);
        userDb.updateChildren(userInfo);

        if (resultUri != null) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("profile_image_url", downloadUrl.toString());
                    userDb.updateChildren(userInfo);

                    endActivity();
                }
            });
        }
        else {
            endActivity();
        }
    }

    private void endActivity() {
        Intent intent = new Intent(SettingsActivity.this, SwipeActivity.class);
        startActivity(intent);
        finish();
    }

    private String getUserPreference(RadioButton selectedRadioBtn) {
        return selectedRadioBtn.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PROFILE_IMAGE && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            imageViewProfilePicture.setImageURI(resultUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endActivity();
    }
}
