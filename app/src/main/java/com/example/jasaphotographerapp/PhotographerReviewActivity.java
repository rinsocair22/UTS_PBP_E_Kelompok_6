package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotographerReviewActivity extends AppCompatActivity {

    private String pgID;
    //ui views
    ImageButton backBtn;
    ImageView profileIv;
    TextView  pgNameTv,rateTv;
    RatingBar ratingBar;
    RecyclerView reviewRV;
    ArrayList<ModelReview> reviewArrayList; // contain of all reviews
    AdapterReview adapterReview;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_review);


        //init ui views
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        pgNameTv = findViewById(R.id.pgNameTv);
        rateTv = findViewById(R.id.rateTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewRV = findViewById(R.id.reviewRV);

        //get pg id from intent
        pgID = getIntent().getStringExtra("pgID");

        firebaseAuth = FirebaseAuth.getInstance();
        loadPgDetail();//for pg name, image
        loadReviews(); // for reviews list, avg rating

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private float ratingSum = 0;

    private void loadReviews() {
        //init list
        reviewArrayList=new ArrayList<>();

        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
        ratingRef.orderByChild("pgID").equalTo(pgID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding data into it
                        reviewArrayList.clear();
                        ratingSum=0;
                        for(DataSnapshot reviewSnapshot:snapshot.getChildren()){
                            ModelReview modelReview = reviewSnapshot.getValue(ModelReview.class);
                            float rating = Float.parseFloat(modelReview.getRateValue());
                            ratingSum = ratingSum+rating;

                            reviewArrayList.add(modelReview);

                        }
                        //setup adapter
                        adapterReview = new AdapterReview(PhotographerReviewActivity.this, reviewArrayList);
                        //set to recycleview
                        reviewRV.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        rateTv.setText(String.format("%.2f", avgRating)+" out of ["+numberOfReviews+"] ordered completed!"); //eg 4.7[1]
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadPgDetail() {

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
        pgRef.child(pgID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelPhotographer pgModel = snapshot.getValue(ModelPhotographer.class);
                        String pgName = pgModel.getName();
                        String pgIcon = pgModel.getPgIcon();

                        pgNameTv.setText(pgName);
                        try{
                            Picasso.get().load(pgIcon).placeholder(R.drawable.ic_name).into(profileIv);

                        }
                        catch (Exception e){
                            profileIv.setImageResource(R.drawable.ic_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}