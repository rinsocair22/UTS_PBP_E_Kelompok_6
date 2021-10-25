package com.example.jasaphotographerapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends  RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    private ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_review
        View view = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        //get data at position
        ModelReview modelReview = reviewArrayList.get(position);
        String pgID = modelReview.getPgID();
        String clientID = modelReview.getClientID();
        String rateID = modelReview.getRateID();
        String rateComment = modelReview.getRateComment();
        String rateValue = modelReview.getRateValue();
        String timestamp = modelReview.getTimestamp();

        //convert timestamp to proper format dd/mm/yyyy
        loadUserDetail(modelReview,holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat= DateFormat.format("dd/MM/yyyy",calendar).toString();
        //set data
        holder.ratingBar.setRating(Float.parseFloat(rateValue));
        holder.reviewTv.setText(rateComment);
        holder.dateTv.setText(dateFormat);
    }

    private void loadUserDetail(ModelReview modelReview, HolderReview holder) {

        String clientID = modelReview.getClientID();

        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("client");
        clientRef.child(clientID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelClient client = snapshot.getValue(ModelClient.class);


                        String name = client.getName();

                        //set data
                        holder.nameTv.setText(name);
                        holder.profileIv.setImageResource(R.drawable.ic_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size(); //return list size
    }

    //view holder class, hold/inits views of recycleView
    class HolderReview extends RecyclerView.ViewHolder{

        //ui views of layout row_review

        private ImageView profileIv;
        private TextView nameTv,dateTv,reviewTv;
        private RatingBar ratingBar;


        public HolderReview(@NonNull View itemView) {
            super(itemView);


            //init views of row_reviews
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
