package com.example.jasaphotographerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPhotographer extends RecyclerView.Adapter<AdapterPhotographer.HolderPhotographer> implements Filterable {

    private Context context;
    public ArrayList<ModelPhotographer> pgList,filterList;
    private FilterPhotographer filter;
    private String selectedLocation;

    public AdapterPhotographer(Context context, ArrayList<ModelPhotographer> pgList, String selectedLocation) {
        this.context = context;
        this.pgList = pgList;
        this.filterList = pgList;
        this.selectedLocation=selectedLocation;
    }

    @NonNull
    @Override
    public HolderPhotographer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_photographer.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_photographer, parent, false);
        return new HolderPhotographer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPhotographer holder, int position) {
        //get data
        //nanti set location
        ModelPhotographer modelPg = pgList.get(position);
        String pgID = modelPg.getPgID();
        String pgName = modelPg.getName();
        String pgEmail = modelPg.getEmail();
        String pgPhoneNo = modelPg.getPhoneNo();
        String pgIcon = modelPg.getPgIcon();

        loadReviews(modelPg,holder);
        loadLocation(modelPg,holder);
        //loadLocation(modelPg,holder);

        //set data
        holder.pgNameTv.setText(pgName);
        holder.pgPhoneTv.setText(pgPhoneNo);
        holder.pgEmailTv.setText(pgEmail);
        //holder.pgLocationTv.setText(selectedLocation);

        //check picture
        try{
            Picasso.get().load(pgIcon).placeholder(R.drawable.ic_name).into(holder.pgIv);
        }
        catch(Exception e){
            holder.pgIv.setImageResource(R.drawable.ic_name);
        }

        //handle click listiner, show shop details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotographerDetailsActivity.class);
                intent.putExtra("pgID", pgID);
                context.startActivity(intent);
            }
        });



    }

   private void loadLocation(ModelPhotographer modelPg, HolderPhotographer holder) {

        String pgID = modelPg.getPgID();

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
                   holder.pgLocationTv.append(locationList.get(i)+" ");

               }
               holder.pgLocationTv.append("]");
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private float ratingSum = 0;
    private void loadReviews(ModelPhotographer modelPg, HolderPhotographer holder) {

        String pgID = modelPg.getPgID();
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

                        holder.ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pgList.size(); // return number of record
    }

    @Override
    public Filter getFilter() {

        if(filter==null){
            filter = new FilterPhotographer(this,filterList);
        }
        return filter;
    }

    //view holder
    class HolderPhotographer extends RecyclerView.ViewHolder {

        //ui views of row_pg.xml
        private ImageView pgIv,nextIv;
        private TextView pgNameTv,pgPhoneTv,pgEmailTv,pgLocationTv,pgLocation2Tv;
        private RatingBar ratingBar;

        public HolderPhotographer(@NonNull View itemView) {
            super(itemView);

            //init uid views
            pgIv = itemView.findViewById(R.id.pgIv);
            pgNameTv = itemView.findViewById(R.id.pgNameTv);
            pgPhoneTv = itemView.findViewById(R.id.pgPhoneTv);
            pgEmailTv = itemView.findViewById(R.id.pgEmailTv);
            nextIv = itemView.findViewById(R.id.nextIv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            pgLocationTv = itemView.findViewById(R.id.pgLocationTv);
            //pgLocation2Tv = itemView.findViewById(R.id.pgLocation2Tv);

        }
    }
}
