package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference reference;
    String userID,fullname, email, phoneNo;
    TextView fullnameLabel,emailLabel,phoneNoLabel;
    Button btnUpdate,deleteBtn;
    private static final String TAG = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("client");
        userID = user.getUid();

        fullnameLabel   = findViewById(R.id.fullname_profile);
        emailLabel      = findViewById(R.id.email_profile);
        phoneNoLabel     = findViewById(R.id.phoneNo_profile);
        btnUpdate = findViewById(R.id.update);
        deleteBtn = findViewById(R.id.deleteBtn);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelClient clientProfile = snapshot.getValue(ModelClient.class);
                if(clientProfile!=null){
                     fullname = clientProfile.name;
                     email = clientProfile.email;
                     phoneNo = clientProfile.phoneNo;

                    fullnameLabel.setText(fullname);
                    emailLabel.setText(email);
                    phoneNoLabel.setText(phoneNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this,"Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });

        
        //update user

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),UpdateProfileActivity.class);
                i.putExtra("name",fullname);
                i.putExtra("email",email);
                i.putExtra("phoneNo",phoneNo);
                startActivity(i);
            }
        });

        //delete user
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show delete confirm dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete account client + all booking made by client
                                deleteData(userID);
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





        //initialize and assign value navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.profile);

        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.booking:
                        startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:

                        return true;
                }
                return false;}
        });
    }

    private void deleteData(String userID) {


        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking");
        bookingRef.orderByChild("clientID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookSnapshot: snapshot.getChildren()){

                    bookSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
        ratingRef.orderByChild("clientID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot rateSnapshot: snapshot.getChildren()){

                    rateSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        
        reference.child(userID).removeValue(); //remove from client table
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Account Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                finish();
            }
        }); // delete user

    }
}