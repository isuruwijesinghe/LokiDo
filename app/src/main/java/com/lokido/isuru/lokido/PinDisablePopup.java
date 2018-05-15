package com.lokido.isuru.lokido;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PinDisablePopup extends Activity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String appPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.2));

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        Button btn_disable = (Button) findViewById(R.id.btn_Disable);
        Button btn_exit = (Button) findViewById(R.id.btn_Exit);

        btn_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disable();
            }
            public void disable(){
                Intent intent = new Intent(PinDisablePopup.this, CustomPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                startActivity(intent);
                finish();
//                signout();
                return;

            }

        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }

        });
    }
    public void signout(){
        auth.signOut();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(currentTime);
        String formattedDateOnly = date.format(currentTime);
        SharedPreferences sharedPref = getSharedPreferences(appPref, MODE_PRIVATE);
        String name = sharedPref.getString("userName","Isuru");
        System.out.println(formattedDate+" "+name+" "+"Logged out.");
        String forDb = " "+name+" "+"Logged out.";
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("log").child(name).child(formattedDateOnly);
        myRef.child(formattedDate).setValue(forDb.toString());
        startActivity(new Intent(PinDisablePopup.this, LoginActivity.class));
    }
}
