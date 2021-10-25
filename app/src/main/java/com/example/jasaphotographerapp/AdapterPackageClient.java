package com.example.jasaphotographerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPackageClient extends RecyclerView.Adapter<AdapterPackageClient.HolderPackageClient> implements Filterable {



    private Context context;
    public ArrayList<ModelPackage> packageList, filterList;
    private FilterPackageClient filter;

    public AdapterPackageClient(Context context, ArrayList<ModelPackage> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.filterList = packageList;
    }

    @NonNull
    @Override
    public HolderPackageClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_package_client,parent, false);


        return new HolderPackageClient(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPackageClient holder, int position) {

            //get data
        ModelPackage modelPackage = packageList.get(position);
        String packageID = modelPackage.getPackageID();
        String pgID = modelPackage.getPgID();
        String packageName = modelPackage.getPackageName();
        String packagePrice = modelPackage.getPackagePrice();
        String packageType = modelPackage.getPackageType();
        String packageDescription = modelPackage.getPackageDescription();
        String packageIcon = modelPackage.getPackageIcon();

        //set data

        holder.packageNameTv.setText(packageName);
        holder.packageTypeTv.setText("Type:"+" "+packageType);
        holder.packageDescriptionTv.setText("Info:"+""+packageDescription);
        holder.packagePriceTv.setText("Rp"+packagePrice);

        try{
            Picasso.get().load(packageIcon).placeholder(R.drawable.ic_add_photo).into(holder.packageIconIv);
        }
        catch (Exception e){
            holder.packageIconIv.setImageResource(R.drawable.ic_add_photo);
        }
        //click booking -> addbooking
        holder.bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add booking
                Intent intent = new Intent(context,AddBookingActivity.class);
                intent.putExtra("packageName", packageName);
                intent.putExtra("packagePrice", packagePrice);
                intent.putExtra("packageID", packageID);
                intent.putExtra("pgID", pgID);
                intent.putExtra("packageDescription", packageDescription);
                intent.putExtra("packageIcon", packageIcon);
                context.startActivity(intent);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    @Override
    public Filter getFilter() {

        if(filter== null){
            filter = new FilterPackageClient(this,filterList);
        }
        return filter;
    }

    class HolderPackageClient extends RecyclerView.ViewHolder{

        //uid views
        ImageView packageIconIv;
        Button bookingBtn;
        TextView packageNameTv,packagePriceTv, packageTypeTv, packageDescriptionTv;


        public HolderPackageClient(@NonNull View itemView) {
            super(itemView);

            //init ui views
            packageIconIv = itemView.findViewById(R.id.packageIconIv);
            bookingBtn = itemView.findViewById(R.id.bookingBtn);
            packageNameTv = itemView.findViewById(R.id.packageNameTv);
            packagePriceTv = itemView.findViewById(R.id.packagePriceTv);
            packageTypeTv = itemView.findViewById(R.id.packageTypeTv);
            packageDescriptionTv = itemView.findViewById(R.id.packageDescriptionTv);
        }
    }
}
