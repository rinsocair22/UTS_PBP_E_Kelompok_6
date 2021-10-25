package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PortfolioPhotographerActivity extends AppCompatActivity {

    ImageButton backBtn;
    RecyclerView portfolioRV;

    ArrayList<ModelPortfolio> portfolioList;
    AdapterPortfolioPhotographer adapterPortfolioPhotographer;


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    String pgID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_photographer);

        //init ui views
        backBtn= findViewById(R.id.backBtn);
        portfolioRV= findViewById(R.id.portfolioRv);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        pgID = user.getUid();

        //load all album
        loadAllAlbum();



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadAllAlbum() {

        portfolioList = new ArrayList<>();

        //get all album
        DatabaseReference pfRef = FirebaseDatabase.getInstance().getReference("portfolio");
        pfRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             //before getting reset arraylist
                portfolioList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){

                    ModelPortfolio modelPortfolio = ds.getValue(ModelPortfolio.class);
                    portfolioList.add(modelPortfolio);
                }
                //setup adapter
                adapterPortfolioPhotographer = new AdapterPortfolioPhotographer(PortfolioPhotographerActivity.this,portfolioList);

                //setup adapter
                portfolioRV.setAdapter(adapterPortfolioPhotographer);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}