package com.lokido.isuru.lokido;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseAuth;

public class PinDisablePopup extends Activity {

    private FirebaseAuth auth;

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
        startActivity(new Intent(PinDisablePopup.this, LoginActivity.class));
    }
}
