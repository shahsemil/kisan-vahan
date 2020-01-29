package com.example.agripool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FarmerAadhaarActivity extends AppCompatActivity {

    private Button button;
    private EditText aadhaar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_aadhaar);

        button = (Button)findViewById(R.id.submit);
        aadhaar = (EditText) findViewById(R.id.aadhaar);
        final String unm = getIntent().getStringExtra("user");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num = aadhaar.getText().toString().trim();

                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if(num.length()>11) {
                                mDatabase.child("Farmer").child(unm).child("aadhaar").setValue(num);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),databaseError.getMessage() ,Toast.LENGTH_LONG ).show();
                    }
                });
            }
        });


    }
}
