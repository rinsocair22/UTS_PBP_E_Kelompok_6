package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotographerDetailsActivity extends AppCompatActivity {

    //declare ui views
    ImageButton backBtn, callBtn,filterPackageBtn,reviewBtn,btnAlbum;
    ImageView pgIv;
    TextView pgNameTv,pgEmailTv, pgPhoneNoTv, pgTypeTv,filterPackageTv,pgLocationTv,pgLocation2Tv;
    EditText searchPackageEt;
    RecyclerView packageRv;
    RatingBar ratingBar;
    String location1,location2;

    String userID, pgName,pgEmail,pgPhoneNo,pgType,pgIcon;

    private String pgID;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    private ArrayList <ModelPackage> packageList;
    private AdapterPackageClient adapterPackageClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_details);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        pgIv = findViewById(R.id.pgIcon);
        pgNameTv = findViewById(R.id.pgNameTv);
        pgEmailTv = findViewById(R.id.pgEmailTv);
        pgPhoneNoTv = findViewById(R.id.pgPhoneNoTv);
        pgTypeTv = findViewById(R.id.pgTypeTv);
        callBtn = findViewById(R.id.callBtn);
        searchPackageEt = findViewById(R.id.searchPackageEt);
        filterPackageBtn = findViewById(R.id.filterPackageBtn);
        filterPackageTv = findViewById(R.id.filterPackageTv);
        packageRv = findViewById(R.id.packageRv);
        reviewBtn = findViewById(R.id.reviewBtn);
        ratingBar = findViewById(R.id.ratingBar);
        btnAlbum = findViewById(R.id.btnAlbum);
        pgLocationTv = findViewById(R.id.pgLocationTv);


        //get pgId of photographer from intent

        pgID = getIntent().getStringExtra("pgID");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        loadMyInfo();
        loadPgDetails();
        loadPgPackages();
        loadReviews();//avg rating, set on rating bar

        //search
        searchPackageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterPackageClient.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go previous activity
                onBackPressed();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        filterPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotographerDetailsActivity.this);
                builder.setTitle("Choose Type:")
                        .setItems(Constants.packageType1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected= Constants.packageType1[which];
                                filterPackageTv.setText(selected);
                                if(selected.equals("All")){
                                    loadPgPackages();
                                }
                                else {
                                    //load filtered
                                    adapterPackageClient.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

            //handle  reviewBtn click, open reviews activity
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass pg id to show its reviews
                Intent intent = new Intent(PhotographerDetailsActivity.this,PhotographerReviewActivity.class);
                intent.putExtra("pgID",pgID);
                startActivity(intent);
            }
        });

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass pg id to show its reviews
                Intent intent = new Intent(PhotographerDetailsActivity.this,PortfolioClientActivity.class);
                intent.putExtra("pgID",pgID);
                startActivity(intent);
            }
        });


    }
    private float ratingSum = 0;
    private void loadReviews() {
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
        ratingRef.orderByChild("pgID").equalTo(pgID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding data into it

                        ratingSum=0;
                        for(DataSnapshot reviewSnapshot:snapshot.getChildren()){
                            ModelReview modelReview = reviewSnapshot.getValue(ModelReview.class);
                            float rating = Float.parseFloat(modelReview.getRateValue());
                            ratingSum = ratingSum+rating;


                        }


                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(pgPhoneNo))));
        Toast.makeText(this,""+pgPhoneNo, Toast.LENGTH_SHORT).show();
    }

    //load info client
    private void loadMyInfo() {

        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("client");
        clientRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelClient clientProfile = snapshot.getValue(ModelClient.class);

                if(clientProfile!=null){
                    String name = clientProfile.name;
                    String email = clientProfile.email;
                    String phoneNo = clientProfile.phoneNo;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //load photographer detail
    private void loadPgDetails() {

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");

        pgRef.child(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPhotographer pgModel = snapshot.getValue(ModelPhotographer.class);
                if(pgModel!=null){

                     pgName = pgModel.getName();
                     pgEmail = pgModel.getEmail();
                     pgPhoneNo = pgModel.getPhoneNo();
                     pgType = pgModel.getType();
                     pgIcon = pgModel.getPgIcon();

                     //set data to ui
                    pgNameTv.setText(pgName);
                    pgEmailTv.setText(pgEmail);
                    pgPhoneNoTv.setText(pgPhoneNo);
                    pgTypeTv.setText(pgType);

                    try{
                        Picasso.get().load(pgIcon).placeholder(R.drawable.ic_name).into(pgIv);
                    }
                    catch (Exception e) {
                        pgIv.setImageResource(R.drawable.ic_name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ArrayList<String> locationList;
        locationList = new ArrayList<>();

        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
        locationRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            locationList.clear();
                for(DataSnapshot pgSnapshot:snapshot.getChildren()){
                    locationList.add(pgSnapshot.child("locationName").getValue(String.class));

                }

                for(int i = 0; i < locationList.size(); i++) {
                    pgLocationTv.append(locationList.get(i)+" ");

                }
                pgLocationTv.append("]");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void loadPgPackages() {
        //init list
        packageList = new ArrayList<>();

        //get all package
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        packageRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before adding items
                packageList.clear();
                for(DataSnapshot packageSnapshot:snapshot.getChildren()){
                    ModelPackage modelPackage = packageSnapshot.getValue(ModelPackage.class);
                    packageList.add(modelPackage);
                }

                //setup adapter
                adapterPackageClient = new AdapterPackageClient(PhotographerDetailsActivity.this,packageList);
                //set adapter
                packageRv.setAdapter(adapterPackageClient);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}