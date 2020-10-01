package com.example.agripool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button contin;
    private TextView login;
    private EditText phone;
    private EditText passwd;
    private EditText uname;
    private EditText confpasswd;
    private RadioGroup radiogrp;
    private RadioButton radiobtn;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Sign Up");
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Sign Up");

        contin = (Button) findViewById(R.id.cont);
        login = (TextView) findViewById(R.id.log);
        login.setPaintFlags(login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phone = (EditText) findViewById(R.id.phone);
        uname = (EditText) findViewById(R.id.name);
        passwd = (EditText) findViewById(R.id.passwd);
        confpasswd = (EditText) findViewById(R.id.confpasswd);
        radiogrp = (RadioGroup) findViewById(R.id.radio);


        contin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "0", Toast.LENGTH_SHORT).show();
                registerUser();

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open login activity
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//doesnot allow the previous activity to lose its edit text contrnts
                startActivity(startIntent);
                finish();
            }
        });
    }

    private void registerUser() {
        //Toast.makeText(getApplicationContext(), "Call", Toast.LENGTH_SHORT).show();
        final String unm = uname.getText().toString().trim();
        final String phonestr = phone.getText().toString().trim();
        final String pass = passwd.getText().toString().trim();
        String confpass = confpasswd.getText().toString().trim();
        int selected = radiogrp.getCheckedRadioButtonId();
        radiobtn = (RadioButton) findViewById(selected);
        final String type = radiobtn.getText().toString().trim();
        //Toast.makeText(getApplicationContext(), "Call1", Toast.LENGTH_SHORT).show();


        if (TextUtils.isEmpty(unm)) {
            //phone no. is empty
            uname.setError("Please enter username");
            uname.requestFocus();
            //stopping the function execution further
            return;
        }
        if (TextUtils.isEmpty(phonestr)) {
            //phone no. is empty
            phone.setError("Please enter phone no.");
            phone.requestFocus();
            //stopping the function execution further
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            //password is empty
            //Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwd.setError("Please enter password");
            passwd.requestFocus();
            //stopping the function execution further
            return;
        }
        if (TextUtils.isEmpty(confpass)) {
            //password is empty
            //Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            confpasswd.setError("Please re-enter password");
            confpasswd.requestFocus();
            //stopping the function execution further
            return;
        }
        if (confpass.equals(pass)) {
            //if validations are ok
            //Register the user
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(type).child(unm).exists()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Username is already taken,Enter a different Username" , Toast.LENGTH_LONG).show();

                    } else {
                        progressDialog.dismiss();
                        if(radiobtn.getText().equals("Farmer")) {
                            //mDatabase.child("uname").setValue(unm);
                            mDatabase.child("Farmer").child(unm).child("user").setValue(unm);
                            mDatabase.child("Farmer").child(unm).child("phone").setValue(phonestr);
                            mDatabase.child("Farmer").child(unm).child("pass").setValue(pass);
                            mDatabase.child("Farmer").child(unm).child("count").setValue(0);
                            Intent intent = new Intent(getApplicationContext(),FarmerAadhaarActivity.class);
                            intent.putExtra("user", unm);
                            startActivity(intent);
                        }
                        else if(radiobtn.getText().equals("Driver")){
                            mDatabase.child("Driver").child(unm).child("user").setValue(unm);
                            mDatabase.child("Driver").child(unm).child("phone").setValue(phonestr);
                            mDatabase.child("Driver").child(unm).child("pass").setValue(pass);
                            Intent intent = new Intent(getApplicationContext(),DriverAadhaarActivity.class);
                            intent.putExtra("user", unm);
                            startActivity(intent);
                        }


                       // Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.getMessage() ,Toast.LENGTH_LONG ).show();

                }
            });
        }
    }
}
