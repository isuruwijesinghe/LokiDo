package com.lokido.isuru.lokido;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String child = inputName.getText().toString();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                myRef = database.getReference("users").child(child);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                myRef.child("username").setValue(inputName.getText().toString());
                myRef.child("email").setValue(inputEmail.getText().toString());
                myRef.child("password").setValue(inputPassword.getText().toString());
//                userChangeListner();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignupActivity.this, Drawer.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void creatuser(String name,String email,String password){

//        myRef.child("username").setValue(name.getText().toString());
//        myRef.child("email").setValue(email.getText().toString());
//        myRef.child("password").setValue(password.getText().toString());
    }
//    private void userChangeListner(){
//        myRef.child(child).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                child user = dataSnapshot.getValue(child);
//                System.out.println("User Name :"+child);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("Failed to read User "+databaseError.toException());
//            }
//        });
//    }
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    // UI references.
//    private EditText mnameText;
//    private EditText memailText;
//    private EditText mpasswordText;
//    private Button msignupButton;
//    private TextView mloginLink;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
////        ButterKnife.inject(this);
//
//        msignupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signup();
//            }
//        });
//
//        mloginLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Finish the registration screen and return to the Login activity
//                finish();
//            }
//        });
//    }
//
//    public void signup() {
//        Log.d(TAG, "Signup");
//
//        if (!validate()) {
//            onSignupFailed();
//            return;
//        }
//
//        msignupButton.setEnabled(false);
//
//        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();
//
//        String name = mnameText.getText().toString();
//        String email = memailText.getText().toString();
//        String password = mpasswordText.getText().toString();
//
//        // TODO: Implement your own signup logic here.
//
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
//    }
//
//
//    public void onSignupSuccess() {
//        msignupButton.setEnabled(true);
//        setResult(RESULT_OK, null);
//        finish();
//    }
//
//    public void onSignupFailed() {
//        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
//
//        msignupButton.setEnabled(true);
//    }
//
//    public boolean validate() {
//        boolean valid = true;
//
//        String name = mnameText.getText().toString();
//        String email = memailText.getText().toString();
//        String password = mpasswordText.getText().toString();
//
//        if (name.isEmpty() || name.length() < 3) {
//            mnameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            mnameText.setError(null);
//        }
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            memailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            memailText.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            mpasswordText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            mpasswordText.setError(null);
//        }
//
//        return valid;
//    }
//}
