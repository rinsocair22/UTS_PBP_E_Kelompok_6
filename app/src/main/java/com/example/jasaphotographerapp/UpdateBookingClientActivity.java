package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UpdateBookingClientActivity extends AppCompatActivity {

    ImageButton backBtn;
    EditText etStatus,etBookPrice,etBookingDescription,etBookingLocation,etDate,etTime;
    Button btnUpdateBooking;
    private ProgressDialog progressDialog;
    String bookID,bookDate,bookDescription,bookLocation,bookTime,bookStatus,bookPrice,bookTime2,pgID,checkDate="same";
    int tHour,tMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking_client);

        //init vui views
        etDate= findViewById(R.id.etDate);
        etBookingLocation=findViewById(R.id.etBookingLocation);
        etBookingDescription = findViewById(R.id.etBookingDescription);
        etBookPrice = findViewById(R.id.etBookPrice);
        etStatus = findViewById(R.id.etStatus);
        etTime = findViewById(R.id.etTime);
        backBtn = findViewById(R.id.backBtn);
        btnUpdateBooking = findViewById(R.id.btnUpdateBooking);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //get value from intent
        bookID = getIntent().getStringExtra("bookID");
        bookDate = getIntent().getStringExtra("bookDate");
        bookDescription = getIntent().getStringExtra("bookDescription");
        bookLocation = getIntent().getStringExtra("bookLocation");
        bookTime = getIntent().getStringExtra("bookTime");
        bookStatus = getIntent().getStringExtra("bookStatus");
        bookPrice = getIntent().getStringExtra("bookPrice");
        pgID = getIntent().getStringExtra("pgID");

        //init calander
        Calendar calendar = Calendar.getInstance();
        //get year
        int year = calendar.get(Calendar.YEAR);
        //get month
        int month = calendar.get(Calendar.MONTH);
        //get day
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //load booking info
        loadBookingDetails();

        //change status
        etStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick status
                statusDialog();

            }
        });

        //pick date
        etDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //init date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UpdateBookingClientActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //store date in string
                        bookDate = dayOfMonth+"/"+(month+1)+"/"+year;
                        //set date on edit text
                        etDate.setText(bookDate);
                        checkDate = "new";



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
                        UpdateBookingClientActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnUpdateBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow:
                //1)input data
                //2)validate data
                //3) update data to db
                inputData();
            }
        });

    }

    private void inputData() {
        //input data
        bookTime = etTime.getText().toString().trim();
        bookDate = etDate.getText().toString().trim();
        bookStatus=etStatus.getText().toString().trim();
        bookDescription = etBookingDescription.getText().toString().trim();
        bookLocation = etBookingLocation.getText().toString().trim();

        //2) validate data
        if(TextUtils.isEmpty(bookTime)){
            etTime.setError("Booking time is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookDate)){
            etDate.setError("Booking date is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookStatus)){
            etStatus.setError("Booking Status is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookDescription)){
            etBookingDescription.setError("Booking description is Required.");
            return;
        }
        if(TextUtils.isEmpty(bookLocation)){
            etBookingLocation.setError("Booking location is Required.");
            return;
        }

        if(checkDate.equalsIgnoreCase("new")){
            checkDate();
        }
        if(checkDate.equalsIgnoreCase("same")){
            updateBooking();
        }
        if(checkDate.equalsIgnoreCase("true")){
        updateBooking();
        }
    }

    private void updateBooking() {

        //show progress
        progressDialog.setMessage("Updating Booking...");
        progressDialog.show();

        //setup data in hashmap to update
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("bookTime",bookTime);
        hashMap.put("bookDate",bookDate);
        hashMap.put("bookStatus",bookStatus);
        hashMap.put("bookDescription",bookDescription);
        hashMap.put("bookLocation",bookLocation);

        //update to db
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.child(bookID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update success
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(UpdateBookingClientActivity.this,"Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //update failed
                progressDialog.dismiss();
                Toast.makeText(UpdateBookingClientActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void statusDialog() {

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change status to:")
                .setItems(Constants.bookingStatusClient, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        bookStatus = Constants.bookingStatusClient[which];
                        //set picked category
                        etStatus.setText(bookStatus);
                    }
                })
                .show();

    }

    private void loadBookingDetails() {

        //set data to view
        etBookingDescription.setText(bookDescription);
        etBookingLocation.setText(bookLocation);
        etBookPrice.setText(bookPrice);
        etDate.setText(bookDate);
        etTime.setText(bookTime);
        etStatus.setText(bookStatus);
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
    }

}