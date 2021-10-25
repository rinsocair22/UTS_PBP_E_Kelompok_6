package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPhotographerActivity extends AppCompatActivity {

    TextInputLayout etEmail,etPassword;
    Button btnLogin;
    TextView btnCreate;
    FirebaseAuth fAuth;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_photographer);

        fAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.textViewRegister);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();

                //check if empty
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is Required.");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    etEmail.setError("Enter Valid Email Address");
                    etEmail.requestFocus();
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Password Must be atleast 6 Characters");
                    return;
                }

                DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");

                pgRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(LoginPhotographerActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), PhotographerMainActivity.class));
                                    }
                                    else{
                                        Toast.makeText(LoginPhotographerActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else{

                            Toast.makeText(LoginPhotographerActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterPhotographerActivity.class));
            }
        });

    }
}