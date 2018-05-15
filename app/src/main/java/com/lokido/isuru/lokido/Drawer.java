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
import android.widget.Toast;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageButton btnLock;

    private EditText textLockName, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

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
        database = FirebaseDatabase.getInstance();


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
            Intent intent = new Intent(Drawer.this, CustomPinActivity.class);
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
                    Date currentTime = Calendar.getInstance().getTime();
                    String name = sharedPref.getString("userName","Isuru");
                    String address = sharedPref.getString("userAddress","address");
                    System.out.println(currentTime+" "+name+" unlocked the PadLock in "+address);
                    btnLock.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    clickListener = 1;
                } else {
                    //goes to the pin
                    intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                    startActivity(intent);

                }

            }
            public void lock(){
                System.out.println("Came to lock mehod");
                SharedPreferences sharedPref = getSharedPreferences(appPref,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("lockbuttonStatus",false);
                editor.apply();
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(currentTime);
                String formattedDateOnly = date.format(currentTime);
                String name = sharedPref.getString("userName","Isuru");
                String address = sharedPref.getString("userAddress","address");
                System.out.println(formattedDate+" "+name+" locked the PadLock in "+address);
                String forDb = " "+name+" locked the PadLock in "+address;
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("log").child(name).child(formattedDateOnly);
                myRef.child(formattedDate).setValue(forDb.toString());
                Toast.makeText(Drawer.this, getString(R.string.btnLock), Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(Drawer.this,PinDisablePopup.class));
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
        String passcode = sharedPref.getString("Pin","1234");
        System.out.println("Pin is  :" + passcode);
        System.out.println("lock Button Status :" + lockbuttonStatus);
        //Db
        String name = sharedPref.getString("userName","Isuru");
        System.out.println("Name :"+name);
        String pin = sharedPref.getString("Pin","1234");
        System.out.println("Pin is :"+pin);
//        myRef = database.getReference("users").child(name);
//        myRef.child("pin").setValue(pin.toString());

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
