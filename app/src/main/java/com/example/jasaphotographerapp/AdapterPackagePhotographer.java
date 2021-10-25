package com.example.jasaphotographerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPackagePhotographer extends RecyclerView.Adapter<AdapterPackagePhotographer.HolderPackagePhotographer> implements Filterable {



    private Context context;
    public ArrayList<ModelPackage> packageList, filterList;
    private FilterPackage filter;

    public AdapterPackagePhotographer(Context context, ArrayList<ModelPackage> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.filterList=packageList;
    }

    @NonNull
    @Override
    public HolderPackagePhotographer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_package_photographer,parent,false);
        return new HolderPackagePhotographer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPackagePhotographer holder, int position) {
    //get data
        final ModelPackage modelPackage = packageList.get(position);
        String packageID = modelPackage.getPackageID();
        String pgID = modelPackage.getPgID();
        String packageName = modelPackage.getPackageName();
        String packagePrice = modelPackage.getPackagePrice();
        String packageType = modelPackage.getPackageType();
        String packageDescription = modelPackage.getPackageDescription();
        String packageIcon = modelPackage.getPackageIcon();

        //set data
        holder.packageNameTv.setText(packageName);
        holder.packageTypeTv.setText(packageType);
        holder.packageDescriptionTv.setText(packageDescription);
        holder.packagePriceTv.setText("Rp"+packagePrice);

        try{
            Picasso.get().load(packageIcon).placeholder(R.drawable.ic_add_photo).into(holder.packageIconIv);
        }
        catch (Exception e){
            holder.packageIconIv.setImageResource(R.drawable.ic_add_photo);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks, show item details (in bottom sheet)
                detailsBottomSheet(modelPackage);//here modelPackage contains details of cliked product

            }
        });

    }

    private void detailsBottomSheet(ModelPackage modelPackage) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflat view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_package_details_photographer, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);




        //innit views of bottomsheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView packageIconIv = view.findViewById(R.id.packageIconIv);
        TextView packageNameTv = view.findViewById(R.id.packageNameTv);
        TextView packagePriceTv = view.findViewById(R.id.packagePriceTv);
        TextView packageTypeTv = view.findViewById(R.id.packageTypeTv);
        TextView packageDescriptionTv = view.findViewById(R.id.packageDescriptionTv);

        //get data

        final String packageID = modelPackage.getPackageID();
        String pgID = modelPackage.getPgID();
        String packageName = modelPackage.getPackageName();
        String packagePrice = modelPackage.getPackagePrice();
        String packageType = modelPackage.getPackageType();
        String packageDescription = modelPackage.getPackageDescription();
        String packageIcon = modelPackage.getPackageIcon();

        //set data
        packageNameTv.setText(packageName);
        packagePriceTv.setText("Rp" + packagePrice);
        packageDescriptionTv.setText(packageDescription);
        packageTypeTv.setText(packageType);

        try {
            Picasso.get().load(packageIcon).placeholder(R.drawable.ic_add_photo).into(packageIconIv);
        } catch (Exception e) {
            packageIconIv.setImageResource(R.drawable.ic_add_photo);
        }

        //show dialog
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                //open edit package activity, pass id of package
                Intent intent = new Intent(context, UpdatePackageActivity.class);
                intent.putExtra("packageID",packageID);
                context.startActivity(intent);


            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete"+" "+packageName+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            //delete
                                deleteBtn(packageID); //product id
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });



    }

    private void deleteBtn(String packageID) {
        //delete package using its id

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        packageRef.child(packageID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //package deleted
                        Toast.makeText(context, "Package deleted", Toast.LENGTH_SHORT).show();
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
        return packageList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterPackage(this,filterList);
        }
        return filter;
    }


    class HolderPackagePhotographer extends RecyclerView.ViewHolder{

        //hold views of recyleview
        private ImageView packageIconIv;
        private TextView packageNameTv, packagePriceTv,packageTypeTv,packageDescriptionTv;

        public HolderPackagePhotographer(@NonNull View itemView) {
            super(itemView);

            packageIconIv = itemView.findViewById(R.id.packageIconIv);
            packageNameTv = itemView.findViewById(R.id.packageNameTv);
            packagePriceTv=itemView.findViewById(R.id.packagePriceTv);
            packageTypeTv=itemView.findViewById(R.id.packageTypeTv);
            packageDescriptionTv=itemView.findViewById(R.id.packageDescriptionTv);



        }
    }
}
