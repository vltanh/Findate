package com.example.narubibi.findate.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {
    private Toolbar editInfoToolbar;

    private TextView edBirth;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private String userId, name, birthday, phone, about, job, workplace, swipeImageUrl;
    private ImageView imageViewSwipeImage;
    private EditText editTextName, editTextPhone, editTextAboutYou, editTextJob, editTextSchoolCompany;
    private Button btnConfirm, btnBack;
    private FloatingActionButton btnAddImage;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDb;

    private DatePickerDialog datePickerDialog;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        editInfoToolbar = findViewById(R.id.edit_info_toolbar);
        setSupportActionBar(editInfoToolbar);
        getSupportActionBar().setTitle("Edit Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edBirth = (TextView) findViewById(R.id.tv_birth_set);

        edBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePickerDialog == null) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    datePickerDialog = new DatePickerDialog(
                            EditInfoActivity.this,
                            AlertDialog.THEME_HOLO_DARK,
                            dateSetListener,
                            year, month, day);
                }
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                edBirth.setText(date);
            }
        };

        imageViewSwipeImage = findViewById(R.id.iv_swipe_image);

        editTextName = findViewById(R.id.ed_edit_info_name);
        editTextPhone = findViewById(R.id.ed_edit_info_phone);
        editTextAboutYou = findViewById(R.id.ed_edit_info_about);
        editTextJob = findViewById(R.id.ed_edit_info_job_title);
        editTextSchoolCompany = findViewById(R.id.ed_edit_info_school);

        btnConfirm = findViewById(R.id.btn_edit_info_update);
        btnBack = findViewById(R.id.btn_edit_info_cancel);
        btnAddImage = findViewById(R.id.btn_plus_image);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .setAspectRatio(4, 5)
                        .setMinCropWindowSize(200, 250)
                        .start(EditInfoActivity.this);
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
                    if (map.get("birthday") != null) {
                        birthday = map.get("birthday").toString();
                        edBirth.setText(birthday);

                        String[] dateArray = birthday.split("/");

                        int year = Integer.parseInt(dateArray[2]);
                        int month = Integer.parseInt(dateArray[0]);
                        int date = Integer.parseInt(dateArray[1]);

                        datePickerDialog = new DatePickerDialog(
                                EditInfoActivity.this,
                                AlertDialog.THEME_HOLO_DARK,
                                dateSetListener,
                                year, month - 1, date);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        editTextPhone.setText(phone);
                    }
                    if (map.get("job") != null) {
                        job = map.get("job").toString();
                        editTextJob.setText(job);
                    }
                    if (map.get("about") != null) {
                        about = map.get("about").toString();
                        editTextAboutYou.setText(about);
                    }
                    if (map.get("workplace") != null) {
                        workplace = map.get("workplace").toString();
                        editTextSchoolCompany.setText(workplace);
                    }
                    if (map.get("swipe_image_url") != null) {
                        swipeImageUrl = map.get("swipe_image_url").toString();
                        Glide.with(imageViewSwipeImage.getContext()).load(swipeImageUrl).into(imageViewSwipeImage);
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
        birthday = edBirth.getText().toString();
        phone = editTextPhone.getText().toString();
        job = editTextJob.getText().toString();
        about = editTextAboutYou.getText().toString();
        workplace = editTextSchoolCompany.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("birthday", birthday);
        userInfo.put("phone", phone);
        userInfo.put("job", job);
        userInfo.put("about", about);
        userInfo.put("workplace", workplace);
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
                    userInfo.put("swipe_image_url", downloadUrl.toString());
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
//        Intent intent = new Intent(EditInfoActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageViewSwipeImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                endActivity();
                break;
        }
        return true;
    }
}
