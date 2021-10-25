package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private ProgressDialog progressDialog;
    private RelativeLayout pgRl;
    private RecyclerView pgRv;

    ImageButton filterLocationBtn, filterGpsBtn;
    private EditText searchPhotographerEt;

    private TextView filterPhotographerTv;

    private ArrayList<ModelPhotographer> pgList;
    private ArrayList<ModelLocation> locationList;
    private AdapterPhotographer adapterPhotographer;
    String userID,myLocation,selectedLocation;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 600;
    //permission array
    private String[] locationPermissions;
    private double latitude, longitude;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init permission arrays
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        pgRl = findViewById(R.id.pgRl);
        pgRv = findViewById(R.id.pgRv);
        searchPhotographerEt = findViewById(R.id.searchPhotographerEt);
        filterLocationBtn = findViewById(R.id.filterLocationBtn);
        filterPhotographerTv = findViewById(R.id.filterPhotographerTv);
        filterGpsBtn = findViewById(R.id.filterGpsBtn);
        


        //search user area
        filterGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }
            }
        });

        //search name
        searchPhotographerEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterPhotographer.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filterLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Location:")
                        .setItems(Constants.pgLocation2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.pgLocation2[which];

                                if (selected.equals("All")) {
                                    //load all
                                    filterPhotographerTv.setText("Showing" + " " + selected);
                                    loadPhotographer();
                                } else {
                                    //load filtered
                                    filterPhotographerTv.setText("Showing All Photographer in" + " " + selected);
                                    loadFilteredPhotographer(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        //show pg ui
        loadPhotographer();


        //initialize and assign value navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.booking:
                        startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //update every 10 min
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 10, this);


    }
    private boolean checkLocationPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestLocationPermission(){

        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }
    private void findAddress() {

        //find city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try{
            addresses = geocoder.getFromLocation(latitude,longitude,1);

            String address = addresses.get(0).getAddressLine(0); //complete address
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set addresses
            myLocation = city;
            Toast.makeText(this, ""+myLocation, Toast.LENGTH_SHORT).show();
            filterPhotographerTv.setText("Showing All Photographer in" + " " + myLocation);
            loadFilteredPhotographer(myLocation);


        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disable
        Toast.makeText(this,"Please enable location", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude= location.getLongitude();

        findAddress();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission allowed
                        detectLocation();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this,"Location permission is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadFilteredPhotographer(String selected) {

        pgList= new ArrayList<>();
        //get all location
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            //clear list before adding
                pgList.clear();

                for(DataSnapshot ds:snapshot.getChildren()){

                    String locationName = ds.child("locationName").getValue(String.class);
                    String pgID = ds.child("pgID").getValue(String.class);

                    if(selected.equals(locationName)){

                        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
                        pgRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot ds: snapshot.getChildren()){
                                    ModelPhotographer modelPg = ds.getValue(ModelPhotographer.class);
                                    selectedLocation = locationName;
                                    pgList.add(modelPg);
                                }

                                //setup adapter
                                adapterPhotographer = new AdapterPhotographer(MainActivity.this, pgList,selectedLocation);

                                //set adapter to recyleView
                                pgRv.setAdapter(adapterPhotographer);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    else{
                        pgList.clear();
                        //setup adapter
                        adapterPhotographer = new AdapterPhotographer(MainActivity.this, pgList, locationName);

                        //set adapter to recyleView
                        pgRv.setAdapter(adapterPhotographer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void loadPhotographer() {

        //init list

        pgList = new ArrayList<>();
        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");

        pgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //clear list before adding
                pgList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPhotographer modelPg = ds.getValue(ModelPhotographer.class);

                    pgList.add(modelPg);
                }

                //setup adapter
                adapterPhotographer = new AdapterPhotographer(MainActivity.this, pgList, selectedLocation);
                //set adapter to recyleView

                pgRv.setAdapter(adapterPhotographer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //button logout
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }



}