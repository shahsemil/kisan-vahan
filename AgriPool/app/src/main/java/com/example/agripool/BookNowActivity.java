package com.example.agripool;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class BookNowActivity extends AppCompatActivity {

    private static final String TAG ="Transport_details";
    private EditText pickup,deadline;
    private DatePickerDialog.OnDateSetListener date,date1;
    private Button book;

    private EditText destination;
    private EditText source;
    private EditText type;
    private EditText quantity;
    private CheckBox checkBox;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Make a Booking");

        source = (EditText) findViewById(R.id.source);
        destination = (EditText) findViewById(R.id.destination);
        type = (EditText) findViewById(R.id.croptype);
        quantity = (EditText) findViewById(R.id.quantity);
        progressDialog = new ProgressDialog(this);

        pickup = findViewById(R.id.pickupdate);
        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BookNowActivity.this,
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
                pickup.setText(date11);
            }
        };
        deadline= findViewById(R.id.deadlinedate);
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BookNowActivity.this,
                        android.R.style.Theme_Black, date1,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        date1 = (datePicker, year, month, day) -> {
            month=month+1;
            Log.d(TAG, "onDateSet: mm/dd/yyy: " +month+"/"+day+"/"+year);
            String date12 =day+"/"+month+"/"+year;
            deadline.setText(date12);
        };

        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String src = source.getText().toString().trim();
                final String dest = destination.getText().toString().trim();
                final String croptype = type.getText().toString().trim();
                final String quant = quantity.getText().toString().trim();
                final String pick = pickup.getText().toString().trim();
                final String dead = deadline.getText().toString().trim();
                final String cooling;
                final double lat,dlat;
                final double lng,dlng;
                final double price;
                checkBox = (CheckBox) findViewById(R.id.checkBox);


                if(TextUtils.isEmpty(src)){
                    source.setError("Please enter source address");
                    source.requestFocus();
                }
                if(TextUtils.isEmpty(dest)){
                    destination.setError("Please enter destination address");
                    destination.requestFocus();
                }
                if(TextUtils.isEmpty(croptype)){
                    type.setError("Please enter type of crop");
                    type.requestFocus();
                }
                if(TextUtils.isEmpty(quant)){
                    quantity.setError("Please enter the quantity");
                    quantity.requestFocus();
                }
                if(TextUtils.isEmpty(pick)){
                    pickup.setError("Please select pickup date");
                    pickup.requestFocus();
                }
                if(TextUtils.isEmpty(dead)){
                    deadline.setError("Please enter deadline date");
                    deadline.requestFocus();
                }
                if(checkBox.isChecked()){
                    cooling = "yes";
                }else {
                    cooling = "no";
                }
                
                // Price computation
                if(cooling == "yes" && Integer.parseInt(quant) < 100) {
                    price = 70 * Integer.parseInt(quant);
                } else if(cooling == "no" && Integer.parseInt(quant) < 100) {
                    price = 50 * Integer.parseInt(quant);
                } else if(cooling == "yes" && Integer.parseInt(quant) > 100) {
                    price = 80 * Integer.parseInt(quant);
                } else {
                    price = 60 * Integer.parseInt(quant);
                }

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> address = geocoder.getFromLocationName(src, 1);
                    Address address1 = address.get(0);
                    lat = address1.getLatitude();
                    lng = address1.getLongitude();

                    List<Address> addresses = geocoder.getFromLocationName(dest,1);
                    Address addr = addresses.get(0);
                    dlat = addr.getLatitude();
                    dlng = addr.getLongitude();
                    final String orderID = Common.user_name + Integer.toString(Common.count);;
                    Common.count = Common.count + 1;
                    //Toast.makeText(getApplicationContext(),Common.count.toString() ,Toast.LENGTH_LONG ).show();
                    progressDialog.setMessage("Booking Order...");
                    progressDialog.show();
                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            db.child("Farmer").child(Common.user_name).child("count").setValue(Common.count);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            String e = databaseError.getMessage();
                            Toast.makeText(getApplicationContext(), e, Toast.LENGTH_SHORT).show();
                        }
                    });
                    final DatabaseReference mydb = FirebaseDatabase.getInstance().getReference("Orders");
                    mydb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            mydb.child(orderID).child("id").setValue(orderID);
                            mydb.child(orderID).child("src_lat").setValue(lat);
                            mydb.child(orderID).child("src_lng").setValue(lng);
                            mydb.child(orderID).child("dest_lat").setValue(dlat);
                            mydb.child(orderID).child("dest_lng").setValue(dlng);
                            mydb.child(orderID).child("crop").setValue(croptype);
                            mydb.child(orderID).child("quantity").setValue(quant);
                            mydb.child(orderID).child("pick_date").setValue(pick);
                            mydb.child(orderID).child("dead_date").setValue(dead);
                            mydb.child(orderID).child("refrigerator").setValue(cooling);
                            mydb.child(orderID).child("status").setValue("pending");
                            progressDialog.dismiss();

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}