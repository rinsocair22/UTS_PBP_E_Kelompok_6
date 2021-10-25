package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilePhotographerActivity extends AppCompatActivity {

    ImageView profileIv;
    TextView  nameTv,emailTv,phoneNoTv,typeTv;
    Button btnUpdate,deleteBtn;
    FirebaseUser user;
    String pgID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photographer);

        //init ui views
        nameTv    = findViewById(R.id.nameTv);
        emailTv       = findViewById(R.id.emailTv);
        phoneNoTv     = findViewById(R.id.phoneNoTv);
        typeTv       = findViewById(R.id.typeTv);
        profileIv = findViewById(R.id.profileIv);
        btnUpdate = findViewById(R.id.btnUpdate);
        deleteBtn = findViewById(R.id.deleteBtn);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadMyInfo();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UpdateProfilePhotographerActivity.class));
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(ProfilePhotographerActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete account client + all booking made by client
                                deleteData(pgID);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel delete
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        //initialize and assign value navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.profile);

        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.booking:
                        startActivity(new Intent(getApplicationContext(), BookingPhotographerActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), PhotographerMainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        return true;
                }
                return false;}
        });
    }

    private void deleteData(String pgID) {
//delete booking
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookSnapshot: snapshot.getChildren()){

                    bookSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//delete rating
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
        ratingRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot rateSnapshot: snapshot.getChildren()){

                    rateSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
//delete package
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        packageRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot pcSnapshot: snapshot.getChildren()){

                    pcSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        //delete portfolio

        DatabaseReference portfolioRef = FirebaseDatabase.getInstance().getReference("portfolio");
        portfolioRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot pcSnapshot: snapshot.getChildren()){

                    pcSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
        pgRef.child(pgID).removeValue(); //remove from client table
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Account Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfilePhotographerActivity.this,HomeActivity.class));
                finish();
            }
        }); // delete user
    }

    private void loadMyInfo() {


        pgID = user.getUid();
        //load user info and set to views

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");

        pgRef.child(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPhotographer pgModel = snapshot.getValue(ModelPhotographer.class);
                if(pgModel!=null){

                    String name = pgModel.getName();
                    String email = pgModel.getEmail();
                    String phoneNo = pgModel.getPhoneNo();
                    String type = pgModel.getType();
                    String pgIcon = pgModel.getPgIcon();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneNoTv.setText(phoneNo);
                    typeTv.setText(type);

                    try{
                        Picasso.get().load(pgIcon).placeholder(R.drawable.ic_name).into(profileIv);
                    }
                    catch (Exception e) {
                        profileIv.setImageResource(R.drawable.ic_name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });


    }
}