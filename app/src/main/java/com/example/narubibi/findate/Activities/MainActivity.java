package com.example.narubibi.findate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.narubibi.findate.Activities.Authentication.LoginActivity;
import com.example.narubibi.findate.Activities.Authentication.RegisterActivity;
import com.example.narubibi.findate.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private BottomNavigationView mainBottomNavigation;

    private ProfileFragment profileFragment;
    private FindFragment findFragment;
    private MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("FINDATE");

        mainBottomNavigation = findViewById(R.id.mainBottomNav);

        profileFragment = new ProfileFragment();
        findFragment = new FindFragment();
        messageFragment = new MessageFragment();

        initializeFragment();

        mainBottomNavigation.setSelectedItemId(R.id.bottom_nav_find);

        mainBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);

                switch(menuItem.getItemId()){
                    case R.id.bottom_nav_profile:
                        replaceFragment(profileFragment, currentFragment);
                        return true;

                    case R.id.bottom_nav_find:
                        replaceFragment(findFragment, currentFragment);
                        return true;

                    case R.id.bottom_nav_message:
                        replaceFragment(messageFragment, currentFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragment == profileFragment){
            fragmentTransaction.hide(findFragment);
            fragmentTransaction.hide(messageFragment);
        }

        if(fragment == findFragment){

            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(messageFragment);

        }

        if(fragment == messageFragment){

            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(findFragment);

        }
        fragmentTransaction.show(fragment);

        fragmentTransaction.commit();
    }

    private void initializeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_framelayout, findFragment);
        fragmentTransaction.add(R.id.main_framelayout, profileFragment);
        fragmentTransaction.add(R.id.main_framelayout, messageFragment);

        fragmentTransaction.hide(profileFragment);
        fragmentTransaction.hide(messageFragment);

        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_login_btn:
                sendToLoginActivity();
                return true;

            case R.id.action_signup_btn:
                sendtoSignupActivity();
                return true;

            case R.id.action_settings_btn:
                sendtoSettingsActivity();
                return true;

            case R.id.action_logout_btn:
                logOut();
                return true;

            default:
                return false;
        }
    }

    private void logOut() {
    }

    private void sendtoSettingsActivity() {
    }

    private void sendtoSignupActivity() {
        Intent signupIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(signupIntent);
        finish();
    }

    private void sendToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
