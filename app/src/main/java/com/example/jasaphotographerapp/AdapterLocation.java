package com.example.jasaphotographerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterLocation extends RecyclerView.Adapter<AdapterLocation.HolderLocation> {


    private Context context;
    public ArrayList<ModelLocation> locationList;

    public AdapterLocation(Context context, ArrayList<ModelLocation> locationsList) {
        this.context = context;
        this.locationList = locationsList;
    }



    @NonNull
    @Override
    public HolderLocation onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_location, parent, false);

        return new HolderLocation(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLocation holder, int position) {

        //get data
        ModelLocation modelLocation = locationList.get(position);
        String locationID = modelLocation.getLocationID();
        String locationName = modelLocation.getLocationName();
        String pgID = modelLocation.getPgID();

        //set data

        holder.locationEt.setText(locationName);

        holder.locationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog(holder);
            }
        });

        //click change
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UpdateLocationActivity.class);
                intent.putExtra("locationID", locationID);
                intent.putExtra("pgID", pgID);
                intent.putExtra("locationName", locationName);
                context.startActivity(intent);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete confirm dialog
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete"+" "+locationName+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteBtn(locationID); //product id
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

    private void deleteBtn(String locationID) {
        //delete location using its id


        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("location");
        deleteRef.child(locationID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //location deleted
                        Toast.makeText(context, "Location deleted", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //location fail to delete
                        Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateLocation(HolderLocation holder, String locationID, String pgID) {

        String locationName = holder.locationEt.getText().toString().trim();

       //check location sama
        //init db
        DatabaseReference pgLocationRef = FirebaseDatabase.getInstance().getReference("location");
        pgLocationRef.orderByChild("pgID").equalTo(pgID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot ds: snapshot.getChildren()){

                        ModelLocation modelLocation = ds.getValue(ModelLocation.class);

                        if(modelLocation.getLocationName().equals(locationName)){

                            holder.locationEt.setError (" location already exist");
                            return;

                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("locationName", locationName);

                        //update to db
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("location");
                        updateRef.child(locationID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                                holder.locationEt.setError(null);
                            }
                        });
                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void categoryDialog(HolderLocation holder) {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location:")
                .setItems(Constants.pgLocation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = Constants.pgLocation[which];
                        //set picked category
                        holder.locationEt.setText(category);
                    }
                })
                .show();


    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }


    class HolderLocation extends RecyclerView.ViewHolder {

        //uid views

        Button updateBtn;
        EditText locationEt;
        ImageButton deleteBtn;


        public HolderLocation(@NonNull View itemView) {
            super(itemView);

            //init ui views
            updateBtn = itemView.findViewById(R.id.updateBtn);
            locationEt = itemView.findViewById(R.id.locationEt);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }


    }
}
