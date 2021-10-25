package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotographerMainActivity extends AppCompatActivity {

    TextView nameTv, emailTv,phoneNoTv,filterPackageTv,pgLocationTv,pgLocation2Tv;
    EditText searchPackageEt;
    Button btnAddPackage;
    ImageButton filterPackageBtn,btnAddPhoto,btnAlbum,reviewBtn,locationBtn;
    RelativeLayout packageRl;
    RecyclerView packageRv;
    ImageView profileIv;


    FirebaseAuth fAuth;
    ProgressDialog progressDialog;
    FirebaseUser user;
    ArrayList<ModelPackage> packageList;

    AdapterPackagePhotographer adapterPackagePhotographer;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_main);

        nameTv= findViewById(R.id.nameTv);
        emailTv= findViewById(R.id.emailTv);
        phoneNoTv= findViewById(R.id.phoneNoTv);
        btnAddPackage = findViewById(R.id.btnAddPackage);
        searchPackageEt= findViewById(R.id.searchPackageEt);
        filterPackageBtn= findViewById(R.id.filterPackageBtn);
        filterPackageTv=findViewById(R.id.filterPackageTv);
        packageRl=findViewById(R.id.rl_3);
        packageRv=findViewById(R.id.packageRv);
        profileIv=findViewById(R.id.pgIcon);
        btnAddPhoto=findViewById(R.id.btnAddPhoto);
        btnAlbum=findViewById(R.id.btnAlbum);
        reviewBtn=findViewById(R.id.reviewBtn);
        locationBtn=findViewById(R.id.locationBtn);
        pgLocationTv=findViewById(R.id.pgLocationTv);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        fAuth= FirebaseAuth.getInstance();


        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // load info profile
        loadMyInfo();

        //load info package
        loadAllPackage();

        //handle locationBtn cick,open add location activity
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass pg id to new location activity
                Intent intent = new Intent(PhotographerMainActivity.this,PhotographerLocationActivity.class);
                startActivity(intent);
            }
        });

        //handle  reviewBtn click, open reviews activity
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass pg id to show its reviews
                Intent intent = new Intent(PhotographerMainActivity.this,PhotographerReviewActivity.class);
                intent.putExtra("pgID",userID);
                startActivity(intent);
            }
        });

        //add photo for album
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open add photo activity
                Intent i = new Intent(v.getContext(),addPhotoActivity.class);
                i.putExtra("pgID",userID);
                startActivity(i);
            }
        });

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open album list
                startActivity(new Intent(getApplicationContext(),PortfolioPhotographerActivity.class));

            }
        });

        //search
        searchPackageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterPackagePhotographer.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filterPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotographerMainActivity.this);
                builder.setTitle("Choose Type:")
                        .setItems(Constants.packageType1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              //get selected item
                              String selected= Constants.packageType1[which];
                              filterPackageTv.setText(selected);
                              if(selected.equals("All")){
                                  loadAllPackage();
                              }
                              else {
                                  //load filtered
                                  loadFilteredPackage(selected);
                              }
                            }
                        })
                .show();
            }
        });


        btnAddPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add package activity
                startActivity(new Intent(getApplicationContext(), AddPackageActivity.class));
            }
        });


        //initialize and assign value navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

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
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePhotographerActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;}
        });



    }

    private void loadMyInfo() {

        DatabaseReference pgRef= FirebaseDatabase.getInstance().getReference("photographer");

        pgRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameTv.setText(snapshot.child("name").getValue().toString());
                emailTv.setText(snapshot.child("email").getValue().toString());
                phoneNoTv.setText(snapshot.child("phoneNo").getValue().toString());

                try{
                    Picasso.get().load(snapshot.child("pgIcon").getValue().toString()).placeholder(R.drawable.ic_name).into(profileIv);
                }
                catch (Exception e) {
                    profileIv.setImageResource(R.drawable.ic_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened", Toast.LENGTH_LONG).show();

            }
        });

       /* ArrayList<String> locationList;
        locationList = new ArrayList<>();
        pgLocationTv.setText("");

        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear();
                for(DataSnapshot pgSnapshot:snapshot.getChildren()){

                    locationList.add(pgSnapshot.child("locationName").getValue(String.class));
                }

                for(int i = 0; i < locationList.size(); i++) {
                    pgLocationTv.append(locationList.get(i)+" ");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/




        ArrayList<String> locationList;
        locationList = new ArrayList<>();

        //init db
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                locationList.clear();
                pgLocationTv.setText("");
                for(DataSnapshot pgSnapshot:snapshot.getChildren()){
                    locationList.add(pgSnapshot.child("locationName").getValue(String.class));

                }
                for(int i = 0; i < locationList.size(); i++) {
                    pgLocationTv.append(locationList.get(i)+",");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void loadFilteredPackage(String selected) {

        packageList= new ArrayList<>();
        //get all package
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        //nanti ubah P kecik
        packageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //before getting reset list
                packageList.clear();
                for(DataSnapshot packageSnapshot:snapshot.getChildren()){

                    //if selected type matches package category then add into list

                        ModelPackage modelPackage = packageSnapshot.getValue(ModelPackage.class);
                        if( modelPackage.getPgID().equals(userID)&&modelPackage.getPackageType().equals(selected))
                        packageList.add(modelPackage);


                }
                //setup adapter
                adapterPackagePhotographer= new AdapterPackagePhotographer(PhotographerMainActivity.this,packageList);
                //set adapter
                packageRv.setAdapter(adapterPackagePhotographer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAllPackage() {

        packageList= new ArrayList<>();

        //get all package
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        //nanti ubah P kecik
        packageRef.orderByChild("pgID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //before getting reset list
                packageList.clear();
                for(DataSnapshot packageSnapshot:snapshot.getChildren()){
                    ModelPackage modelPackage = packageSnapshot.getValue(ModelPackage.class);
                    packageList.add(modelPackage);
                }
                //setup adapter
                adapterPackagePhotographer= new AdapterPackagePhotographer(PhotographerMainActivity.this,packageList);
                //set adapter
                packageRv.setAdapter(adapterPackagePhotographer);
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