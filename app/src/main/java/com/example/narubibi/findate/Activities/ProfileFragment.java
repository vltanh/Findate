package com.example.narubibi.findate.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.narubibi.findate.Activities.Authentication.LoginActivity;
import com.example.narubibi.findate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final int REQ_PROFILE_IMAGE = 1;

    private ImageView imageViewProfilePicture;
    private TextView textViewName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDb;

    private String userId, userName, profileImageUrl;
    private Uri resultUri;

    FloatingActionButton btnSetting, btnEditInfo;
    Button btnLogout, btnDeleteAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnSetting = view.findViewById(R.id.btn_Settings);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getContext(), SettingActivity.class);
                getContext().startActivity(settingIntent);
            }
        });

        btnEditInfo = view.findViewById(R.id.btn_adjustProfile);
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editInfoIntent = new Intent(getContext(), EditInfoActivity.class);
                getContext().startActivity(editInfoIntent);
            }
        });

        btnLogout = view.findViewById(R.id.btn_logOut);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

        imageViewProfilePicture = view.findViewById(R.id.iv_accountImage);
        textViewName = view.findViewById(R.id.tv_accountName);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(300, 300)
                    .start(getContext(), ProfileFragment.this);
            }
        });

        return view;
    }

    private void getUserInfo() {
        userDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (dataSnapshot.getKey().equals("name")) {
                        userName = dataSnapshot.getValue().toString();
                        textViewName.setText(userName);
                    }
                    if (dataSnapshot.getKey().equals("profile_image_url")) {
                        profileImageUrl = dataSnapshot.getValue().toString();
                        Glide.with(getContext()).load(profileImageUrl).into(imageViewProfilePicture);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (getContext() != null && dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (dataSnapshot.getKey().equals("name")) {
                        userName = dataSnapshot.getValue().toString();
                        textViewName.setText(userName);
                    }
                    if (dataSnapshot.getKey().equals("profile_image_url")) {
                        profileImageUrl = dataSnapshot.getValue().toString();
                        Glide.with(getContext()).load(profileImageUrl).into(imageViewProfilePicture);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                //imageViewProfilePicture.setImageURI(resultUri);
                saveUserInformation();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void saveUserInformation() {
        if (resultUri != null) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
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
                    Toast.makeText(getContext(), "Error uploading!", Toast.LENGTH_LONG).show();
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
                }
            });
        }
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
