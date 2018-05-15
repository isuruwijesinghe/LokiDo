package com.lokido.isuru.lokido;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class CustomPinActivity extends AppLockActivity {
    String appPref;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    public void showForgotDialog() {
        Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(this,
                res.getString(R.string.activity_dialog_title),
                res.getString(R.string.activity_dialog_accept));
        builder.content(res.getString(R.string.activity_dialog_content));
        builder.negativeText(res.getString(R.string.activity_dialog_decline));

        //Set theme
        builder.darkTheme(false);
        builder.typeface(Typeface.SANS_SERIF);
        builder.positiveColor(res.getColor(R.color.light_blue_500)); // int res, or int colorRes parameter versions available as well.
        builder.negativeColor(res.getColor(R.color.light_blue_500));
        builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
        builder.titleAlignment(BaseDialog.Alignment.CENTER);
        builder.buttonAlignment(BaseDialog.Alignment.CENTER);
        builder.setButtonStacking(false);

        //Set text sizes
        builder.titleTextSize((int) res.getDimension(R.dimen.activity_dialog_title_size));
        builder.contentTextSize((int) res.getDimension(R.dimen.activity_dialog_content_size));
        builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
        builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

        //Build the dialog.
        CustomDialog customDialog = builder.build();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClick() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog.
        customDialog.show();
    }
    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(getApplicationContext(), "Please Enter The Correct Pin !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Toast.makeText(getApplicationContext(), "Success !!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = getSharedPreferences(appPref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("lockbuttonStatus",true);
        editor.apply();
        saveToLog();
        startActivity(new Intent(CustomPinActivity.this, Drawer.class));
        finish();
    }
    public void saveToLog(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(currentTime);
        String formattedDateOnly = date.format(currentTime);
        SharedPreferences sharedPref = getSharedPreferences(appPref,MODE_PRIVATE);
        String name = sharedPref.getString("userName","Isuru");
        String address = sharedPref.getString("userAddress","address");
        System.out.println(formattedDate+" "+name+" unlocked the PadLock in "+address);
        String forDb = " "+name+" unlocked the PadLock in "+address;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("log").child(name).child(formattedDateOnly);
        myRef.child(formattedDate).setValue(forDb.toString());
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//you can override this method to change the pin length from the default 4
    }
}
