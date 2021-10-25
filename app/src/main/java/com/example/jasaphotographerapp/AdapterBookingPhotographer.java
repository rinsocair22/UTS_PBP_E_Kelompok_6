package com.example.jasaphotographerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBookingPhotographer extends RecyclerView.Adapter<AdapterBookingPhotographer.HolderBookingPhotographer> implements Filterable {

    private Context context;
    public ArrayList<ModelBooking> bookingList, filterList;
    private FilterBooking filter;

    public AdapterBookingPhotographer(Context context, ArrayList<ModelBooking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.filterList = bookingList;
    }



    @NonNull
    @Override
    public HolderBookingPhotographer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_booking_photographer,parent, false);
        return new HolderBookingPhotographer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBookingPhotographer holder, int position) {


        String statusPending="pending";
        String statusCancel="cancelled";
        String statusComplete = "completed";
        String statusReject="rejected";
        //get data
        ModelBooking modelBooking = bookingList.get(position);
        String bookID = modelBooking.getBookID();
        String clientID = modelBooking.getClientID();
        String pgID = modelBooking.getPgID();
        String bookDate = modelBooking.getBookDate();
        String bookDescription = modelBooking.getBookDescription();
        String bookLocation = modelBooking.getBookLocation();
        String bookTime = modelBooking.getBookTime();
        String bookStatus = modelBooking.getBookStatus();
        String bookPrice = modelBooking.getBookPrice();
        String packageID = modelBooking.getPackageID();

        //set data
        holder.bookDateTv.setText(bookDate);
        holder.bookDescriptionTv.setText(bookDescription);
        holder.bookLocationTv.setText(bookLocation);
        holder.bookTimeTv.setText(bookTime);
        holder.bookPriceTv.setText("Rp"+bookPrice);
        holder.bookIconIv.setImageResource(R.drawable.ic_booking2);

        //if pending

        if(bookStatus.equals(statusPending)){

            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#FDD835"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_yellow);
        }


        else if(bookStatus.equals(statusReject)){

            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#FF0303"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_red);
        }
        else if(bookStatus.equals(statusCancel)){

            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#FF0303"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_red);
            holder.updateBtn.setVisibility(View.GONE);
        }
        else if(bookStatus.equals(statusComplete))
        {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#388E3C"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_green);
            holder.updateBtn.setVisibility(View.GONE);
        }
        else //approve
        {

            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#388E3C"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_green);
        }


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this booking?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteBooking(bookID); //product id
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
        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle client info button click, show client details (in bottom sheet)
                clientBottomSheet(clientID);
            }
        });

        holder.packageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle package info when button click, show package details (in bottom sheet)
                packageBottomSheet(packageID);
            }
        });

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle update booking status when button click, show booking details (in bottom sheet)
                Intent intent = new Intent(context,UpdateBookingPhotographerActivity.class);
                intent.putExtra("bookDate", bookDate);
                intent.putExtra("bookDescription", bookDescription);
                intent.putExtra("bookLocation", bookLocation);
                intent.putExtra("bookTime", bookTime);
                intent.putExtra("bookStatus", bookStatus);
                intent.putExtra("bookPrice", bookPrice);
                intent.putExtra("bookID", bookID);
                context.startActivity(intent);

            }
        });




    }

    private void deleteBooking(String bookID) {

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.child(bookID).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //booking deleted
                        Toast.makeText(context, "Booking deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //package fail to delete
                Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void packageBottomSheet(String packageID) {

        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflat view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_booking_detail_package, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //innit views of bottomshee

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageView packageIconIv = view.findViewById(R.id.packageIconIv);
        TextView packageNameTv = view.findViewById(R.id.packageNameTv);
        TextView packagePriceTv = view.findViewById(R.id.packagePriceTv);
        TextView packageTypeTv = view.findViewById(R.id.packageTypeTv);
        TextView packageDescriptionTv = view.findViewById(R.id.packageDescriptionTv);

        //get data
        DatabaseReference packageRef = FirebaseDatabase.getInstance().getReference("package");
        packageRef.child(packageID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPackage modelPackage = snapshot.getValue(ModelPackage.class);
                if(modelPackage!= null){
                    //get data
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //show dialog
        bottomSheetDialog.show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });
    }

    private void clientBottomSheet(String clientID) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflat view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_booking_detail_client, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //innit views of bottomsheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        TextView clientNameTv = view.findViewById(R.id.clientNameTv);
        TextView clientEmailTv = view.findViewById(R.id.clientEmailTv);
        TextView clientPhoneNoTv = view.findViewById(R.id.clientPhoneNoTv);

        //get data
        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("client");
        clientRef.child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelClient modelClient = snapshot.getValue(ModelClient.class);
                if(modelClient!= null){
                    //get data
                    String name = modelClient.getName();
                    String email = modelClient.getEmail();
                    String phoneNo = modelClient.getPhoneNo();

                    //set data

                    clientNameTv.setText(name);
                    clientEmailTv.setText(email);
                    clientPhoneNoTv.setText(phoneNo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });

        //show dialog
        bottomSheetDialog.show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });



    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    @Override
    public Filter getFilter() {

        if(filter==null){
            filter = new FilterBooking(this,filterList);
        }
         return filter;
    }

    class HolderBookingPhotographer extends RecyclerView.ViewHolder{

        //holds views of recyleview
        private ImageView bookIconIv;
        private TextView bookDateTv,bookTimeTv,bookLocationTv,bookDescriptionTv,bookPriceTv,bookStatusTv;
        private Button detailBtn,updateBtn,packageBtn;
        private ImageButton deleteBtn;

        public HolderBookingPhotographer(@NonNull View itemView) {
            super(itemView);

            bookIconIv = itemView.findViewById(R.id.bookIconIv);
            bookDateTv = itemView.findViewById(R.id.bookDateTv);
            bookTimeTv = itemView.findViewById(R.id.bookTimeTv);
            bookLocationTv = itemView.findViewById(R.id.bookLocationTv);
            bookDescriptionTv = itemView.findViewById(R.id.bookDescriptionTv);
            bookPriceTv = itemView.findViewById(R.id.bookPriceTv);
            bookStatusTv = itemView.findViewById(R.id.bookStatusTv);
            detailBtn = itemView.findViewById(R.id.detailBtn);
            updateBtn = itemView.findViewById(R.id.updateBtn);
            packageBtn = itemView.findViewById(R.id.packageBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);



        }
    }
}
