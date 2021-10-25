package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PortfolioClientActivity extends AppCompatActivity {
    ImageButton backBtn;
    RecyclerView portfolioRV;

    ArrayList<ModelPortfolio> portfolioList;
    AdapterPortfolioClient adapterPortfolioClient;

    String pgID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_client);

        //get pgId of photographer from intent

        pgID = getIntent().getStringExtra("pgID");
        //init ui views
        backBtn= findViewById(R.id.backBtn);
        portfolioRV= findViewById(R.id.portfolioRv);

        //load all album
        loadAllAlbum(pgID);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadAllAlbum(String pgID) {

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
                adapterPortfolioClient = new AdapterPortfolioClient(PortfolioClientActivity.this,portfolioList);

                //setup adapter
                portfolioRV.setAdapter(adapterPortfolioClient);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}