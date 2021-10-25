package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView bookingRv;
    private EditText searchBookingEt;
    ImageButton filterBookingBtn,filterDateBtn,filterPackageBtn;
    private TextView filterBookingTv;
    private ProgressDialog progressDialog;
    private ArrayList<ModelBooking> bookingList;
    ArrayList<ModelPackage> packageList;
    private AdapterBookingClient adapterBookingClient;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private static final String TAG = "Booking Activty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        bookingRv = findViewById(R.id.bookingRv);
        searchBookingEt = findViewById(R.id.searchBookingEt);
        filterBookingBtn = findViewById(R.id.filterBookingBtn);
        filterDateBtn = findViewById(R.id.filterDateBtn);
        filterBookingTv = findViewById(R.id.filterBookingTv);
        filterPackageBtn = findViewById(R.id.filterPackageBtn);

        loadAllBooking();


        //search by date
        filterDateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //init calander
                Calendar calendar = Calendar.getInstance();
                //get year
                int year = calendar.get(Calendar.YEAR);
                //get month
                int month = calendar.get(Calendar.MONTH);
                //get day
                int day = calendar.get(Calendar.DAY_OF_MONTH);



                //init date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //store date in string
                        String bookDate2 = dayOfMonth+"/"+(month+1)+"/"+year;
                        //set date on edit text
                        searchBookingEt.setText(bookDate2);



                    }
                },year,month,day);

                //show date picker dialog

                datePickerDialog.show();

            }
        });

        //search
        searchBookingEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterBookingClient.getFilter().filter(s);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //search by package
        filterPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Type of Package:")
                        .setItems(Constants.packageType1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.packageType1[which];
                                filterBookingTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadAllBooking();
                                }
                                else{
                                    //load filtered
                                    loadFilteredBookingPackage(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        filterBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setTitle("Check Status:")
                        .setItems(Constants.bookingStatus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.bookingStatus[which];
                                filterBookingTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadAllBooking();
                                }
                                else{
                                    //load filtered
                                    loadFilteredBooking(selected);
                                }
                            }
                        })
                        .show();
            }
        });













        

        //initialize and assign value navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.booking);

        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.booking:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;}
        });
    }



    private void loadFilteredBookingPackage(String selected) {

        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("booking");
        packageRef.orderByChild("clientID").equalTo(fAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();

                for(DataSnapshot ds:snapshot.getChildren()){

                   String packageID= ds.child("packageID").getValue().toString();

                    //get all package berkaitan booking user
                    DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
                    packageRef.orderByChild("packageID").equalTo(packageID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ps: snapshot.getChildren()){

                                ModelPackage modelPkg = ps.getValue(ModelPackage.class);
                                if(modelPkg.getPackageType().equals(selected)){

                                    ModelBooking modelBooking = ds.getValue(ModelBooking.class);
                                    bookingList.add(modelBooking);
                                }

                            }
                            //setup adapter
                            adapterBookingClient = new AdapterBookingClient(BookingActivity.this, bookingList);
                            //set adapter
                            bookingRv.setAdapter(adapterBookingClient);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void loadFilteredBooking(String selected) {
        //get all booking
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.orderByChild("clientID").equalTo(fAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        bookingList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){

                            String bookStatus =ds.child("bookStatus").getValue().toString();

                            //if selected category matches then add in list
                            if(selected.equals(bookStatus)){
                                ModelBooking modelBooking = ds.getValue(ModelBooking.class);
                                bookingList.add(modelBooking);
                            }

                        }
                        //setup adapter
                        adapterBookingClient = new AdapterBookingClient(BookingActivity.this, bookingList);
                        //set adapter
                        bookingRv.setAdapter(adapterBookingClient);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAllBooking() {

        bookingList = new ArrayList<>();
        //get all booking
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.orderByChild("clientID").equalTo(fAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        bookingList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelBooking modelBooking = ds.getValue(ModelBooking.class);
                            bookingList.add(modelBooking);
                        }
                        //setup adapter
                        adapterBookingClient = new AdapterBookingClient(BookingActivity.this, bookingList);
                        //set adapter
                        bookingRv.setAdapter(adapterBookingClient);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}