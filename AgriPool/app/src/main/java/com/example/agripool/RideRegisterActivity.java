package com.example.agripool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.agripool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class RideRegisterActivity extends AppCompatActivity {

    private Button btn;
    private static final String TAG ="Transport_details";
    private DatePickerDialog.OnDateSetListener date;
    private EditText startdate,dest,src,cap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_register);

        btn = (Button)findViewById(R.id.submt);
        //dest = (EditText) findViewById(R.id.destin);
        src = (EditText) findViewById(R.id.stsrc);
        cap = (EditText) findViewById(R.id.capacity);
        startdate = findViewById(R.id.start);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RideRegisterActivity.this,
                        android.R.style.Theme_Black, date,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " +month+"/"+day+"/"+year);
                String date11 =day+"/"+month+"/"+year;
                startdate.setText(date11);
            }
        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String stdate = startdate.getText().toString().trim();
                //final String des = dest.getText().toString().trim();
                final String sr = src.getText().toString().trim();
                final String capp = cap.getText().toString().trim();
                final double lat,dlat;
                final double lng,dlng;
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> address = geocoder.getFromLocationName(sr, 1);
                    Address address1 = address.get(0);
                    lat = address1.getLatitude();
                    lng = address1.getLongitude();

//                    List<Address> addresses = geocoder.getFromLocationName(des,1);
//                    Address addr = addresses.get(0);
//                    dlat = addr.getLatitude();
//                    dlng = addr.getLongitude();

                    //Toast.makeText(getApplicationContext(),Common.count.toString() ,Toast.LENGTH_LONG ).show();
                    final DatabaseReference mdb = FirebaseDatabase.getInstance().getReference("Users/Driver");
                    mdb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mdb.child(Common.user_name).child("rides").child("start_date").setValue(stdate);
                            mdb.child(Common.user_name).child("rides").child("src_lat").setValue(lat);
                            mdb.child(Common.user_name).child("rides").child("src_lng").setValue(lng);
                           // mdb.child(Common.user_name).child("rides").child("dest_lat").setValue(dlat);
                           // mdb.child(Common.user_name).child("rides").child("dest_lng").setValue(dlng);
                            mdb.child(Common.user_name).child("rides").child("capacity").setValue(capp);

                            Intent intent = new Intent(getApplicationContext(),MapsDriverActivity.class);
                            intent.putExtra("lat",lat);
                            intent.putExtra("lng",lng);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            String e = databaseError.getMessage();
                            Toast.makeText(getApplicationContext(), e, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }




            }
        });
    }
}
