package com.example.agripool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private Button submit;
    private EditText uname;
    FirebaseAuth mAuth;
    private TextView signup;
    private Toolbar toolbar;
    private EditText password;
    private RadioGroup radiogrp;
    private RadioButton radiobtn;
    private String ph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Log In");
//        setSupportActionBar(toolbar);

        uname = (EditText) findViewById(R.id.uid);
        submit = (Button) findViewById(R.id.submit);
        password = (EditText) findViewById(R.id.pass);
        radiogrp = (RadioGroup) findViewById(R.id.radio);



        signup = (TextView) findViewById(R.id.signup);
        signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected = radiogrp.getCheckedRadioButtonId();
                radiobtn = (RadioButton) findViewById(selected);
                final String type = (String) radiobtn.getText();
                final String pwd = password.getText().toString().trim();
                final String unm = uname.getText().toString().trim();



                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/"+type);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(unm).exists()) {

                            String pass = dataSnapshot.child(unm).child("pass").getValue(String.class);
                            String user = dataSnapshot.child(unm).child("user").getValue(String.class);


                            //Toast.makeText(getApplicationContext(),count ,Toast.LENGTH_LONG ).show();
                            ph = dataSnapshot.child(unm).child("phone").getValue(String.class);
                            if(pass.equals(pwd) && unm.equals(user)){
                                Toast.makeText(getApplicationContext(),"Login Successfull" , Toast.LENGTH_SHORT).show();
                                String code = "91";
                                String phone = ph.trim();
                                /*
                                if(phone.isEmpty() || phone.length() < 10){
                                    editTextMobile.setError("Valid Phone number is required");
                                    editTextMobile.requestFocus();
                                    return;
                                }*/
//                                String phoneNumber = "+" + code + phone;
//                                Intent intent = new Intent(getApplicationContext(),VerifyPhone.class);
//                                intent.putExtra("phonenumber", phoneNumber);
//                                startActivity(intent);
                                if(type.equals("Farmer")){
                                    Common.aadhaar = dataSnapshot.child(unm).child("aadhaar").getValue(String.class);
                                    Common.count = dataSnapshot.child(unm).child("count").getValue(Integer.class);
                                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                    intent.putExtra("user", unm);
                                    intent.putExtra("phone", phone);
                                    startActivity(intent);
                                    finish();
                                }
                                if(type.equals("Driver")) {
                                    Common.aadhaar = dataSnapshot.child(unm).child("aadhaar").getValue(String.class);
                                    Common.drive = dataSnapshot.child(unm).child("dlic").getValue(String.class);
                                    Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);
                                    intent.putExtra("user", unm);
                                    intent.putExtra("phone", phone);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            return;
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Username not found" , Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String e = databaseError.getMessage();
                        Toast.makeText(getApplicationContext(), e, Toast.LENGTH_SHORT).show();
                    }
                });

                //
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open Signup activity

                Intent startIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startIntent);
            }
        });

    }
}
