package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UpdateProfilePhotographerActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageView profileIv;
    EditText nameET,emailET,phoneNoET,typeET;
    Button btnSave;
    FirebaseUser user;

    String pgID;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private  static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private  static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permission arrays
    private String [] cameraPermission;
    private String [] storagePermission;
    //image picked url
    private Uri image_uri;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_photographer);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //init ui views

        nameET    = findViewById(R.id.editTextName);
        emailET       = findViewById(R.id.editTextEmail);
        phoneNoET     = findViewById(R.id.editTextPhone);
        typeET       = findViewById(R.id.editTextType);
        btnSave = findViewById(R.id.btnSave);
        profileIv = findViewById(R.id.profileIv);
        backBtn = findViewById(R.id.backBtn);

        //init permission arrays
        cameraPermission= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        pgID = user.getUid();

        loadMyInfo();

        typeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                showImagePickDialog();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfilePhotographerActivity.class));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();


            }
        });


    }
  private String name, email, phone, type;

    private void inputData() {

        progressDialog.setMessage("Updating..");
        progressDialog.show();

        //input data
        if(nameET.getText().toString().isEmpty() ||emailET.getText().toString().isEmpty()
                || phoneNoET.getText().toString().isEmpty() || typeET.getText().toString().isEmpty()){

            Toast.makeText(UpdateProfilePhotographerActivity.this,"Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        email = emailET.getText().toString();

        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateProfile();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfilePhotographerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void updateProfile() {

        if(image_uri == null){
            //update without image
            //setup data to update

            HashMap <String, Object> hashMap = new HashMap<>();
            hashMap.put("name", nameET.getText().toString());
            hashMap.put("email", email);
            hashMap.put("type",typeET.getText().toString());
            hashMap.put("phoneNo", phoneNoET.getText().toString());

            //update to db
            DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
            pgRef.child(pgID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //update successful
                    progressDialog.dismiss();
                    Toast.makeText(UpdateProfilePhotographerActivity.this,"Profile updated", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed update
                    progressDialog.dismiss();
                    Toast.makeText(UpdateProfilePhotographerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        else{
            //update with image

            //upload image first
            String filePathAndName = "profile_images/"+""+pgID;
            //get storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded, get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //image url received, uodate db

                                HashMap <String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", nameET.getText().toString());
                                hashMap.put("email", email);
                                hashMap.put("type",typeET.getText().toString());
                                hashMap.put("phoneNo", phoneNoET.getText().toString());
                                hashMap.put("pgIcon",""+downloadImageUri);


                                //update to db
                                DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");
                                pgRef.child(pgID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //update successful
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateProfilePhotographerActivity.this,"Profile updated", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed update
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateProfilePhotographerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateProfilePhotographerActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });




        }

    }

    private void loadMyInfo() {



        //load user info and set to views

        DatabaseReference pgRef = FirebaseDatabase.getInstance().getReference("photographer");

        pgRef.child(pgID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPhotographer pgModel = snapshot.getValue(ModelPhotographer.class);
                if(pgModel!=null){

                    String name = pgModel.getName();
                    String email = pgModel.getEmail();
                    String phoneNo = pgModel.getPhoneNo();
                    String type = pgModel.getType();
                    String pgIcon = pgModel.getPgIcon();

                    nameET.setText(name);
                    emailET.setText(email);
                    phoneNoET.setText(phoneNo);
                    typeET.setText(type);

                    try{
                        Picasso.get().load(pgIcon).placeholder(R.drawable.ic_name).into(profileIv);
                    }
                    catch (Exception e) {
                        profileIv.setImageResource(R.drawable.ic_name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type:")
                .setItems(Constants.typePg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = Constants.typePg[which];
                        //set picked category
                        typeET.setText(category);
                    }
                })
                .show();


    }
    private void showImagePickDialog() {
        //option to display in dialog
        String[] options = {"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item clicks
                        if(which==0){
                            //camera clicked
                            if(checkCameraPermission()){
                                //permission granted
                                pickFromCamera();
                            }
                            else{
                                //permission not granted, request
                                requestCameraPermission();
                            }
                        }
                        else {
                            //gallery clicked
                            if(checkStoragePermission()){
                                //permission granted
                                pickFromGallery();

                            }
                            else {
                                //permission not granted
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery(){
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        //intend to pick image from camera
        //using media store to pick high quality image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_image_Title");
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_image_Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }
    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&& storageAccepted){
                        //both permission granted
                        pickFromCamera();
                    }
                    else{
                        //both or none permission denied
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permission granted
                        pickFromGallery();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //handle image pick results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode== RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                //image picked from gallery

                //save picked image uri
                image_uri=data.getData();
                //set image
                profileIv.setImageURI(image_uri);
            }
            else if (requestCode== IMAGE_PICK_CAMERA_CODE){
                //image picked from camera
                profileIv.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}