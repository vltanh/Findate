package com.example.narubibi.findate.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.narubibi.findate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyak.iconswitch.IconSwitch;

import java.util.HashMap;
import java.util.Map;

import io.apptik.widget.MultiSlider;

public class SettingActivity extends AppCompatActivity {
    private Toolbar settingToolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDb;

    private String userId, preference;

    private IconSwitch maleSwitch, femaleSwitch, showMeSwitch;
    private MultiSlider seekBarAge;
    private TextView textViewAgeMin, textViewAgeMax;

    private boolean isShowingMale, isShowingFemale, isShowingUser;
    private int minAge, maxAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingToolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        maleSwitch = findViewById(R.id.icon_switch_male);
        femaleSwitch = findViewById(R.id.icon_switch_female);
        showMeSwitch = findViewById(R.id.icon_switch_show);

        seekBarAge = findViewById(R.id.icon_seekbar_max_distance);
        textViewAgeMin = findViewById(R.id.tv_setting_min_age);
        textViewAgeMax = findViewById(R.id.tv_setting_max_age);

        maleSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                if (current == IconSwitch.Checked.LEFT) {
                    femaleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                    isShowingMale = false;
                    isShowingFemale = true;
                }
                else if (current == IconSwitch.Checked.RIGHT) {
                    isShowingMale = true;
                }
                saveUserSexPreference();
            }
        });

        femaleSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                if (current == IconSwitch.Checked.LEFT) {
                    maleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                    isShowingFemale = false;
                    isShowingMale = true;
                }
                else if (current == IconSwitch.Checked.RIGHT) {
                    isShowingFemale = true;
                }
                saveUserSexPreference();
            }
        });

        seekBarAge.setMin(13);
        seekBarAge.setMax(60);
        seekBarAge.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minAge = value;
                    textViewAgeMin.setText(String.valueOf(minAge));
                }
                else if (thumbIndex == 1) {
                    maxAge = value;
                    textViewAgeMax.setText(String.valueOf(maxAge));
                }
                saveUserAgePreference();
            }
        });

        showMeSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                isShowingUser = current == IconSwitch.Checked.RIGHT;
                saveUserVisibility();
            }
        });

        getUserInfo();
    }

    private void saveUserAgePreference() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("age_min", minAge);
        userInfo.put("age_max", maxAge);
        userDb.updateChildren(userInfo);
    }

    private void saveUserVisibility() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("visibility", isShowingUser);
        userDb.updateChildren(userInfo);
    }

    private void getUserInfo() {
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("sex_preference") != null) {
                        preference = map.get("sex_preference").toString();
                        if (preference.equals("Male")) {
                            maleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                        } else if (preference.equals("Female")) {
                            femaleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                        } else {
                            maleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                            femaleSwitch.setChecked(IconSwitch.Checked.RIGHT);
                        }
                    }
                    if (map.get("visibility") != null) {
                        isShowingUser = (boolean)map.get("visibility");
                        showMeSwitch.setChecked(isShowingUser ? IconSwitch.Checked.RIGHT : IconSwitch.Checked.LEFT);
                    }
                    if (map.get("age_min") != null) {
                        minAge = Integer.parseInt(map.get("age_min").toString());
                        seekBarAge.getThumb(0).setValue(minAge);
                    }
                    if (map.get("age_max") != null) {
                        maxAge = Integer.parseInt(map.get("age_max").toString());
                        seekBarAge.getThumb(1).setValue(maxAge);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserSexPreference() {
        if (isShowingMale && isShowingFemale) preference = "Both";
        else if (isShowingMale) preference = "Male";
        else if (isShowingFemale) preference = "Female";

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sex_preference", preference);
        userDb.updateChildren(userInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
