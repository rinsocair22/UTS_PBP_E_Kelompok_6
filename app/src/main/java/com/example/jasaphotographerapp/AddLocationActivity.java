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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddLocationActivity extends AppCompatActivity {
    FirebaseUser user;
    String pgID,condition="false";
    EditText addLocationEt;
    Button addBtn;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        addLocationEt = findViewById(R.id.addLocationEt);
        addBtn = findViewById(R.id.addBtn);
        backBtn = findViewById(R.id.backBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        pgID = user.getUid();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addLocationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationDialog();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String city= addLocationEt.getText().toString().trim();

                //check if empty
                if(TextUtils.isEmpty(city)){

                    addLocationEt.setError (" City is required");
                    return;
                }
                checkLocation(city);

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
                        addLocationEt.setText(city);
                    }
                })
                .show();

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

                            addLocationEt.setError (" location already exist");
                            addLocationEt.setTextColor(Color.parseColor("#FF0303"));
                            addLocationEt.setFocusableInTouchMode(true);
                            addLocationEt.setFocusable(true);
                            addLocationEt.requestFocus();
                            condition="false";
                            break;
                        }
                        else{
                            addLocationEt.setTextColor(Color.parseColor("#388E3C"));
                            addLocationEt.setFocusableInTouchMode(false);
                            addLocationEt.setFocusable(false);
                            addLocationEt.setError (null);
                            condition="true";

                        }
                    }

                }
                else if(!snapshot.exists()){

                    addLocationEt.setTextColor(Color.parseColor("#388E3C"));
                    addLocationEt.setFocusableInTouchMode(false);
                    addLocationEt.setFocusable(false);
                    addLocationEt.setError (null);
                    condition="true";

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(condition.equals("true")){
            addLocation(city);
        }


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
                Toast.makeText(AddLocationActivity.this, "Location Added", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), PhotographerLocationActivity.class));
                //onBackPressed();

                addLocationEt.setError (null);
                addLocationEt.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail to add
                Toast.makeText(AddLocationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}