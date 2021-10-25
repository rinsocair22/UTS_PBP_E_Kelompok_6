package com.example.jasaphotographerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPortfolioPhotographer extends RecyclerView.Adapter<AdapterPortfolioPhotographer.HolderPortfolioPhotographer> {

    private Context context;
    public ArrayList<ModelPortfolio> portfolioList;

    public AdapterPortfolioPhotographer(Context context, ArrayList<ModelPortfolio> portfolioList) {
        this.context = context;
        this.portfolioList = portfolioList;
    }

    @NonNull
    @Override
    public HolderPortfolioPhotographer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflat layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_portfolio_photographer,parent,false);


        return new HolderPortfolioPhotographer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPortfolioPhotographer holder, int position) {

        //get data
        ModelPortfolio modelPortfolio = portfolioList.get(position);
        String pfID = modelPortfolio.getPfID();
        String pfInfo = modelPortfolio.getPfInfo();
        String pfName = modelPortfolio.getPfName();
        String pgID = modelPortfolio.getPgID();
        String pfLink1 = modelPortfolio.getPfLink1();
        String pfLink2 = modelPortfolio.getPfLink2();
        String pfLink3 = modelPortfolio.getPfLink3();
        String pfLink4 = modelPortfolio.getPfLink4();
        String pfLink5 = modelPortfolio.getPfLink5();
        String pfLink6 = modelPortfolio.getPfLink6();

        //set data
        holder.pfNameTv.setText(pfName);
        holder.pfInfoTv.setText(pfInfo);
         //upload all picture from storage
        try{

            Picasso.get().load(pfLink1).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo1IV);
            Picasso.get().load(pfLink2).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo2IV);
            Picasso.get().load(pfLink3).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo3IV);
            Picasso.get().load(pfLink4).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo4IV);
            Picasso.get().load(pfLink5).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo5IV);
            Picasso.get().load(pfLink6).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo6IV);

        }
        catch(Exception e){

            holder.photo1IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo2IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo3IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo4IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo5IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo6IV.setImageResource(R.drawable.ic_add_photo);

        }

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update album
                Intent intent = new Intent(context, UpdatePortfolioPhotographerActivity.class);
                intent.putExtra("pfID",pfID);
                context.startActivity(intent);

            }
        });
    //handle delete button when clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete "+pfName+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteBtn(pfID); //portfolio id
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
    }

        //delete album
    private void deleteBtn(String pfID) {

        DatabaseReference portfolioRef = FirebaseDatabase.getInstance().getReference("portfolio");
        portfolioRef.child(pfID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //portfolio deleted
                        Toast.makeText(context, "album deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //package fail to delete
                        Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return portfolioList.size();
    }

    class HolderPortfolioPhotographer extends RecyclerView.ViewHolder{

        //views of  recycleView

        private TextView pfNameTv,pfInfoTv;
        private ImageView photo1IV,photo2IV,photo3IV,photo4IV,photo5IV,photo6IV;
        private ImageButton deleteBtn,editBtn;


        public HolderPortfolioPhotographer(@NonNull View itemView) {
            super(itemView);

            pfNameTv = itemView.findViewById(R.id.pfNameTv);
            pfInfoTv = itemView.findViewById(R.id.pfInfoTv);
            photo1IV = itemView.findViewById(R.id.photo1IV);
            photo2IV = itemView.findViewById(R.id.photo2IV);
            photo3IV = itemView.findViewById(R.id.photo3IV);
            photo4IV = itemView.findViewById(R.id.photo4IV);
            photo5IV = itemView.findViewById(R.id.photo5IV);
            photo6IV = itemView.findViewById(R.id.photo6IV);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }
}
