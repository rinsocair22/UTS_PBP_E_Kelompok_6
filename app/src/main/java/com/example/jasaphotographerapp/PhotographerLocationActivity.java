package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotographerLocationActivity extends AppCompatActivity {

    ImageButton backBtn;
    String pgID;
    String locationID;
    private RecyclerView locationRv;
    private ArrayList<ModelLocation> locationList;
    private AdapterLocation adapterLocation;
    FirebaseUser user;
    EditText addLocationEt;
    Button addBtn;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_location);

        //init vui views
        backBtn = findViewById(R.id.backBtn);
        locationRv = findViewById(R.id.locationRv);
        addBtn = findViewById(R.id.addBtn);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        pgID = user.getUid();


        loadLocation();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), AddLocationActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void addLocation(String city) {

        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        String locationID =locationRef.push().getKey();

        //setup data for location 1
        HashMap<String, Object> hashMap = new HashMap <>();

        hashMap.put("locationID", locationID);
        hashMap.put("locationName",city);
        hashMap.put("pgID",pgID);

        //add to db
        locationRef.child(locationID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PhotographerLocationActivity.this, "Location Added", Toast.LENGTH_SHORT).show();

                addLocationEt.setError (null);
                addLocationEt.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail to add
                Toast.makeText(PhotographerLocationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkLocation(String city) {
        //init db
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot ds: snapshot.getChildren()){

                        ModelLocation modelLocation = ds.getValue(ModelLocation.class);

                        if(modelLocation.getLocationName().equals(city)){

                            addLocationEt.setError (" location already exist");
                            return;

                        }

                    }
                    Toast.makeText(PhotographerLocationActivity.this, "New Location Added", Toast.LENGTH_SHORT).show();
                    addLocation(city);

                }
                else{

                    addLocation(city);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void loadLocation() {
        locationList = new ArrayList<>();
        //init db
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                locationList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelLocation modelLocation = ds.getValue(ModelLocation.class);
                    locationList.add(modelLocation);
                }
                //setup adapter
                adapterLocation = new AdapterLocation(PhotographerLocationActivity.this, locationList);
                //set adapter
                locationRv.setAdapter(adapterLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      }



    }






