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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import java.util.HashMap;

public class AdapterBookingClient extends RecyclerView.Adapter<AdapterBookingClient.HolderBookingClient> implements Filterable {

    private Context context;
    public ArrayList<ModelBooking> bookingList, filterList;
    private FilterBookingClient filter;

    public AdapterBookingClient(Context context, ArrayList<ModelBooking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.filterList = bookingList;
    }


    @NonNull
    @Override
    public AdapterBookingClient.HolderBookingClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_booking_client,parent, false);
        return new AdapterBookingClient.HolderBookingClient(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBookingClient.HolderBookingClient holder, int position) {


        String statusPending="pending";
        String statusReject="rejected";
        String statusCancel="cancelled";
        String statusComplete = "completed";
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



        //if pending

        if(bookStatus.equals(statusPending)){
            holder.writeReviewBtn.setVisibility(View.GONE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#FDD835"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_yellow);
        }
        else if(bookStatus.equals(statusCancel)|| bookStatus.equals(statusReject)){
            holder.writeReviewBtn.setVisibility(View.GONE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#FF0303"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_red);
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.updateBtn.setVisibility(View.GONE);
        }
        else if(bookStatus.equals(statusComplete))
        {
            holder.writeReviewBtn.setVisibility(View.VISIBLE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#388E3C"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_green);
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.bookReviewTv.setVisibility(View.VISIBLE);
            holder.updateBtn.setVisibility(View.GONE);
        }
        else  //approved
        {
            holder.writeReviewBtn.setVisibility(View.GONE);
            holder.bookStatusTv.setText(bookStatus);
            holder.bookStatusTv.setTextColor(Color.parseColor("#388E3C"));
            holder.bookIconIv.setImageResource(R.drawable.ic_booking_green);
        }


        //if dh review
        checkReview(bookID, holder);

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
                photographerBottomSheet(pgID);
            }
        });
        //handle write review click
        holder.writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReviewBottomSheet(modelBooking);
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
                Intent intent = new Intent(context,UpdateBookingClientActivity.class);
                intent.putExtra("bookDate", bookDate);
                intent.putExtra("bookDescription", bookDescription);
                intent.putExtra("bookLocation", bookLocation);
                intent.putExtra("bookTime", bookTime);
                intent.putExtra("bookStatus", bookStatus);
                intent.putExtra("bookPrice", bookPrice);
                intent.putExtra("bookID", bookID);
                intent.putExtra("pgID",pgID);
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

    private void checkReview(String bookID, HolderBookingClient holder) {

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("rating");
        pgRef.orderByChild("bookID").equalTo(bookID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.writeReviewBtn.setVisibility(View.GONE);
                    holder.writeReviewAdded.setVisibility(View.VISIBLE);
                    holder.bookReviewTv.setText("Review submitted");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writeReviewBottomSheet(ModelBooking modelBooking) {

        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflat view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_write_review, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //innit views of bottomsheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageView profileIv = view.findViewById(R.id.profileIv);
        TextView nameTv = view.findViewById(R.id.nameTv);
        TextView bookIDTV = view.findViewById(R.id.bookIDTv);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText reviewET = view.findViewById(R.id.reviewET);
        Button submitBtn = view.findViewById(R.id.submitBtn);
        //get data

        final String bookID = modelBooking.getBookID();
        String pgID = modelBooking.getPgID();
        String clientID = modelBooking.getClientID();

        //load pg info

        //get data
        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
        pgRef.child(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPhotographer modelPhotographer = snapshot.getValue(ModelPhotographer.class);
                if(modelPhotographer!= null){
                    //get data
                    String name = modelPhotographer.getName();
                    String pgIcon = modelPhotographer.getPgIcon();


                    //set data

                    nameTv.setText(name);

                    try {
                        Picasso.get().load(pgIcon).placeholder(R.drawable.ic_add_photo).into(profileIv);
                    } catch (Exception e) {
                        profileIv.setImageResource(R.drawable.ic_name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });

        //set data
        bookIDTV.setText("Booking ID:"+bookID);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //input data
                String rating =""+ratingBar.getRating();
                String review= reviewET.getText().toString().trim();

                //time of review
                String timestamp =""+System.currentTimeMillis();

                //init db
                DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");

                String rateID =ratingRef.push().getKey();

                //setup data in hashmap
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("pgID",pgID);
                hashMap.put("clientID",clientID);
                hashMap.put("bookID",bookID);
                hashMap.put("rateValue",rating);
                hashMap.put("rateComment",review);
                hashMap.put("timestamp",timestamp);
                hashMap.put("rateID",rateID);

                //add to db
                ratingRef.child(rateID).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(view.getContext(),"Review Added",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(view.getContext(),""+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });





            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });

        //show dialog
        bottomSheetDialog.show();

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

    private void photographerBottomSheet(String pgID) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflat view for bottomsheet
        View view = LayoutInflater.from(context).inflate(R.layout.bs_booking_detail_photographer, null);
        //set view to bottomsheet
        bottomSheetDialog.setContentView(view);

        //innit views of bottomsheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageView profileIv = view.findViewById(R.id.profileIv);
        TextView nameTv = view.findViewById(R.id.nameTv);
        TextView emailTv = view.findViewById(R.id.emailTv);
        TextView phoneNoTv = view.findViewById(R.id.phoneNoTv);
        TextView typeTv = view.findViewById(R.id.typeTv);

        //get data
        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
        pgRef.child(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPhotographer modelPhotographer = snapshot.getValue(ModelPhotographer.class);
                if(modelPhotographer!= null){
                    //get data
                    String name = modelPhotographer.getName();
                    String email = modelPhotographer.getEmail();
                    String phoneNo = modelPhotographer.getPhoneNo();
                    String type = modelPhotographer.getType();
                    String pgIcon = modelPhotographer.getPgIcon();


                    //set data

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneNoTv.setText(phoneNo);
                    typeTv.setText(type);

                    try {
                        Picasso.get().load(pgIcon).placeholder(R.drawable.ic_add_photo).into(profileIv);
                    } catch (Exception e) {
                        profileIv.setImageResource(R.drawable.ic_name);
                    }

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
            filter = new FilterBookingClient(this,filterList);
        }
        return filter;
    }

    class HolderBookingClient extends RecyclerView.ViewHolder{

        //holds views of recyleview
        private ImageView bookIconIv,writeReviewAdded;
        private ImageButton writeReviewBtn,deleteBtn;
        private TextView bookDateTv,bookTimeTv,bookLocationTv,bookDescriptionTv,bookPriceTv,bookStatusTv,bookReviewTv;
        private Button detailBtn,updateBtn,packageBtn;

        public HolderBookingClient(@NonNull View itemView) {
            super(itemView);

            writeReviewAdded = itemView.findViewById(R.id.writeReviewAdded);
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
            writeReviewBtn = itemView.findViewById(R.id.writeReviewBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            bookReviewTv = itemView.findViewById(R.id.bookReviewTv);


        }
    }

}
