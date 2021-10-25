package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhoneNo;
    Button btnSave;
    String name,email,phoneNo;
    ImageButton backBtn;
    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etName= findViewById(R.id.fullname_update);
        etEmail= findViewById(R.id.email_update);
        etPhoneNo=findViewById(R.id.phoneNo_update);
        btnSave= findViewById(R.id.btnSave);
        backBtn= findViewById(R.id.backBtn);

        Intent data = getIntent();
        name = data.getStringExtra("name");
        email = data.getStringExtra("email");
        phoneNo = data.getStringExtra("phoneNo");

        etName.setText(name);
        etEmail.setText(email);
        etPhoneNo.setText(phoneNo);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("client");
                userID = user.getUid();

                if(etName.getText().toString().isEmpty() ||etEmail.getText().toString().isEmpty()
                || etPhoneNo.getText().toString().isEmpty()){

                    Toast.makeText(UpdateProfileActivity.this,"Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                else{
                    final String email = etEmail.getText().toString();
                    user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String,Object> edited = new HashMap<>();
                            edited.put("email", email);
                            edited.put("name", etName.getText().toString());
                            edited.put("phoneNo", etPhoneNo.getText().toString());

                            reference.child(userID).updateChildren(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(UpdateProfileActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    finish();

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


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
}