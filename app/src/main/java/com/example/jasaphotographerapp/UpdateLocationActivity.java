package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdateLocationActivity extends AppCompatActivity {
    FirebaseUser user;
    String pgID;
    EditText updateLocationEt;
    Button updateBtn;
    ImageButton backBtn;
    private String locationID;
    private String locationName;
    private String condition = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);

        updateLocationEt = findViewById(R.id.updateLocationEt);
        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backBtn);

        pgID = getIntent().getStringExtra("pgID");
        locationID = getIntent().getStringExtra("locationID");
        locationName = getIntent().getStringExtra("locationName");

        updateLocationEt.setText(locationName);

        user = FirebaseAuth.getInstance().getCurrentUser();
        pgID = user.getUid();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateLocationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city= updateLocationEt.getText().toString().trim();

                //check if empty
                if(TextUtils.isEmpty(city)){

                    updateLocationEt.setError (" City is required");
                    return;
                }
                checkLocation(city);
            }
        });

    }
    private void checkLocation(String city) {
        //init db
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot ds: snapshot.getChildren()){

                        ModelLocation modelLocation = ds.getValue(ModelLocation.class);
                        condition="false";
                        if(modelLocation.getLocationName().equals(city)){

                            updateLocationEt.setError (" location already exist");
                            updateLocationEt.setTextColor(Color.parseColor("#FF0303"));

                            updateLocationEt.setFocusableInTouchMode(true);
                            updateLocationEt.setFocusable(true);
                            updateLocationEt.requestFocus();
                            break;

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "location available", Toast.LENGTH_SHORT).show();
                            updateLocationEt.setTextColor(Color.parseColor("#388E3C"));
                            updateLocationEt.setFocusableInTouchMode(false);
                            updateLocationEt.setFocusable(false);
                            updateLocationEt.setError (null);
                            condition="true";
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(condition.equals("true"))
            updateLocation(city);

    }

    private void updateLocation(String city) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("locationName", city);

        //update to db
        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("location");
        updateRef.child(locationID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(UpdateLocationActivity.this, PhotographerLocationActivity.class));
                onBackPressed();


            }
        });

    }

    private void locationDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("city:")
                .setItems(Constants.pgLocation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String city = Constants.pgLocation[which];
                        //set picked category
                        updateLocationEt.setText(city);
                    }
                })
                .show();

    }
}