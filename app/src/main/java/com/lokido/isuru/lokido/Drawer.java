package com.lokido.isuru.lokido;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageButton btnLock;

    private EditText textLockName, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    String appPref;
    Boolean lockbuttonStatus;
    int clickListener = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nav_email = user.getEmail();
        //check lock status
        checkLockStatus();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Drawer.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnLock = (ImageButton) findViewById(R.id.btnLock);

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener == 0){
                    checkStatus();
                }else{
                    lock();
                }


            }
            public void checkStatus(){
                SharedPreferences sharedPref = getSharedPreferences(appPref,MODE_PRIVATE);
                Boolean lockbuttonStatus = sharedPref.getBoolean("lockbuttonStatus", false);
                System.out.println("lock Button Status :"+lockbuttonStatus);

                if (lockbuttonStatus == true) {
                    btnLock.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    clickListener = 1;
                } else {
                    Intent x = new Intent(Drawer.this, FingerprintActivity.class);
                    startActivity(x);

                }

            }
            public void lock(){
                System.out.println("Came to lock mehod");
                SharedPreferences sharedPref = getSharedPreferences(appPref,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("lockbuttonStatus",false);
                editor.apply();
                btnLock.getBackground().setColorFilter(getResources().getColor(R.color.btn_lock), PorterDuff.Mode.SRC_IN);
                clickListener = 0;
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.textViewUserEmail);
        //set user email in navigation bar
        nav_user.setText(nav_email);
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            System.out.println("signout is working");
            signout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(this, Drawer.class);
            startActivity(i);
            return true;
        } else if (id == R.id.nav_logbook) {

            return true;

        } else if (id == R.id.nav_map) {
            checkLocationPermission();
            finish();
            return true;
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(this, UserSettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkLocationPermission() {
//
        if (ActivityCompat.checkSelfPermission(Drawer.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Drawer.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Drawer.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            // Write you code here if permission already given.
            Intent intent = new Intent(Drawer.this, MapsActivity.class);
            startActivity(intent);
        }
    }

    public void checkLockStatus() {
        btnLock = (ImageButton) findViewById(R.id.btnLock);
        SharedPreferences sharedPref = getSharedPreferences(appPref, MODE_PRIVATE);
        Boolean lockbuttonStatus = sharedPref.getBoolean("lockbuttonStatus", false);
        System.out.println("lock Button Status :" + lockbuttonStatus);

        if (lockbuttonStatus == true) {
            btnLock.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            clickListener = 1;
        }
    }

    public void signout() {

        auth.signOut();
        startActivity(new Intent(Drawer.this, LoginActivity.class));
    }

}
