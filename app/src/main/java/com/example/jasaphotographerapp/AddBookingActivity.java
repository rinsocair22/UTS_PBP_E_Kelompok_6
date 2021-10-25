package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddBookingActivity extends AppCompatActivity {

    ImageView packageIv;
    ImageButton backBtn;
    int check=0;
    EditText etPackageName,etPackagePrice,etBookingDescription,etBookingLocation,etDate,etTime;
    Button btnAddBooking;
    private ProgressDialog progressDialog;
    String packageID,packageName,packagePrice,pgID,packageIcon,bookDate,bookTime,bookTime2,bookDescription,bookLocation,checkDate="false";
    int tHour,tMinute;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        //init ui views
        etPackageName= findViewById(R.id.etPackageName);
        packageIv = findViewById(R.id.packageIcon);
        etPackagePrice=findViewById(R.id.etPackagePrice);
        etBookingDescription = findViewById(R.id.etBookingDescription);
        etBookingLocation = findViewById(R.id.etBookingLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnAddBooking = findViewById(R.id.btnAddBooking);
        backBtn = findViewById(R.id.backBtn);

        //init calander
        Calendar calendar = Calendar.getInstance();
        //get year
        int year = calendar.get(Calendar.YEAR);
        //get month
        int month = calendar.get(Calendar.MONTH);
        //get day
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        //get data of package from intent
        packageID = getIntent().getStringExtra("packageID");
        packageName = getIntent().getStringExtra("packageName");
        packagePrice = getIntent().getStringExtra("packagePrice");
        packageIcon = getIntent().getStringExtra("packageIcon");
        pgID = getIntent().getStringExtra("pgID");


        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //load info package to book
        loadPackageDetails();
        //




        //pick date
        etDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
               //init date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddBookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //store date in string
                        bookDate = dayOfMonth+"/"+(month+1)+"/"+year;
                        //set date on edit text
                        etDate.setText(bookDate);



                    }
                },year,month,day);

                //disable past date
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+28800000);
                //show date picker dialog

                datePickerDialog.show();


            }

        });

        //pick time
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //init time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddBookingActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                //init hour and minute
                                tHour = hourOfDay;
                                tMinute = minute;

                                //store hour and time in string
                                bookTime2 = tHour+":"+tMinute;

                                //init 24 hours time format
                                SimpleDateFormat t24hour = new SimpleDateFormat("HH:mm");
                                try{
                                    Date date = t24hour.parse(bookTime2);
                                    SimpleDateFormat f12Hour = new SimpleDateFormat("hh:mm aa");
                                    //set selected time
                                    etTime.setText(f12Hour.format(date));
                                    bookTime = f12Hour.format(date);
                                }
                                catch(ParseException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 12,0,false);

                //set transparent background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //Display previous selected time
                timePickerDialog.updateTime(tHour,tMinute);
                //show dialog
                timePickerDialog.show();
            }
        });

        btnAddBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow
                //input data
                //validate data
                //add data to db
                inputData();

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go previous activity
                onBackPressed();
            }
        });

    }

    private void inputData() {

        //input data
        bookDescription = etBookingDescription.getText().toString().trim();
        bookLocation = etBookingLocation.getText().toString().trim();

        //2) validate data
        if(TextUtils.isEmpty(bookDescription)){
            etBookingDescription.setError("Booking description is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookLocation)){
            etBookingLocation.setError("Booking location is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookDate)){
            etDate.setError("Booking date is Required.");
            return;
        }

        if(TextUtils.isEmpty(bookTime)){
            etTime.setError("Booking time is Required.");
            return;
        }


        checkDate();





    }

    private void checkDate() {

        //init db
        DatabaseReference bookDateRef = FirebaseDatabase.getInstance().getReference("booking");
        bookDateRef.orderByChild("pgID").equalTo(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists()){

                    for(DataSnapshot bookingSnapshot:snapshot.getChildren()){
                        ModelBooking modelBooking = bookingSnapshot.getValue(ModelBooking.class);
                        checkDate="false";
                        if(modelBooking.getBookDate().equals(bookDate)&&modelBooking.getBookStatus().equalsIgnoreCase("approved")){

                                    etDate.setTextColor(Color.parseColor("#FF0303"));

                                    etDate.setFocusableInTouchMode(true);
                                    etDate.setFocusable(true);
                                    etDate.setError("Date booked");
                                    etDate.requestFocus();

                                    //etDate.setText("");
                                    //Toast.makeText(AddBookingActivity.this,"Date already been booked", Toast.LENGTH_SHORT).show();
                                    break;

                        }
                        else
                        {
                            etDate.setTextColor(Color.parseColor("#388E3C"));
                            checkDate="true";
                            etDate.setFocusableInTouchMode(false);
                            etDate.setFocusable(false);
                            etDate.setError (null);

                        }

                    }
                }
                else
                {
                    etDate.setTextColor(Color.parseColor("#388E3C"));
                    checkDate="true";
                    etDate.setFocusableInTouchMode(false);
                    etDate.setFocusable(false);
                    etDate.setError (null);
                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if(checkDate.equalsIgnoreCase("true")){
            addBooking();
        }
    }

    private void addBooking() {
        //3) add data to db

        progressDialog.setMessage("Booking..");
        progressDialog.show();

        //init db
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");

        String bookID =bookingRef.push().getKey();
        String bookStatus = "pending";

        //setup data
        HashMap<String, Object> hashMap = new HashMap <>();

        hashMap.put("bookID", bookID);
        hashMap.put("bookDate",bookDate);
        hashMap.put("bookTime",bookTime);
        hashMap.put("bookDescription", bookDescription);
        hashMap.put("bookLocation", bookLocation);
        hashMap.put("pgID", pgID);
        hashMap.put("clientID", fAuth.getCurrentUser().getUid());
        hashMap.put("packageID",packageID);
        hashMap.put("bookStatus",bookStatus);
        hashMap.put("bookPrice",packagePrice);

        //add to db
        bookingRef.child(bookID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added to db
                        progressDialog.dismiss();
                        Toast.makeText(AddBookingActivity.this,"Booking Successful", Toast.LENGTH_SHORT).show();
                        clearData();

                        //checkDate="false";
                        startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //fail to add
                        progressDialog.dismiss();
                        Toast.makeText(AddBookingActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void clearData() {
        //clear data after uploading package
        etBookingDescription.setText("");
        etBookingLocation.setText("");
        etDate.setText("");
        etTime.setText("");
    }

    private void loadPackageDetails() {
        //set data to view
        etPackageName.setText(packageName);
        etPackagePrice.setText(packagePrice);

        try {
            Picasso.get().load(packageIcon).placeholder(R.drawable.ic_add_photo).into(packageIv);
        }
        catch(Exception e){
            packageIv.setImageResource(R.drawable.ic_add_photo);
        }

    }
}