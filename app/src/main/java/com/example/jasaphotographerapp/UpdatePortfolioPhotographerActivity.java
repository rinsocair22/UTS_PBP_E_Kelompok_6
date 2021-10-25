package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class UpdatePortfolioPhotographerActivity extends AppCompatActivity {

    private EditText pfNameET,pfInfoET;
    private ImageView photo1IV,photo2IV,photo3IV,photo4IV,photo5IV,photo6IV;
    private ImageButton backBtn;
    private Button updateBtn;
    private String pfID,condition="";

    private ProgressDialog progressDialog;

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
    private Uri image_uri,image_uri2,image_uri3,image_uri4,image_uri5,image_uri6,
            downloadImageUri,downloadImageUri2,downloadImageUri3,downloadImageUri4,downloadImageUri5,downloadImageUri6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_portfolio_photographer);

        //init permission arrays
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pfNameET = findViewById(R.id.photoNameET);
        pfInfoET = findViewById(R.id.photoInfoET);
        photo1IV = findViewById(R.id.photo1IV);
        photo2IV = findViewById(R.id.photo2IV);
        photo3IV = findViewById(R.id.photo3IV);
        photo4IV = findViewById(R.id.photo4IV);
        photo5IV = findViewById(R.id.photo5IV);
        photo6IV = findViewById(R.id.photo6IV);
        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backBtn);

        //get data from intent
        pfID = getIntent().getStringExtra("pfID");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

       //set data
        loadPortfolioInfo();

        photo1IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="one";
                showImagePickDialog();
            }
        });

        photo2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="two";
                showImagePickDialog();
            }
        });

        photo3IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="three";
                showImagePickDialog();
            }
        });

        photo4IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="four";
                showImagePickDialog();
            }
        });

        photo5IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="five";
                showImagePickDialog();
            }
        });

        photo6IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                condition="six";
                showImagePickDialog();
            }
        });


        //handle back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go previous activity
                onBackPressed();
            }
        });

        //handle update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //flow:
                //1)input data
                //2)validate data
                //3) update data to db
                inputData();

            }
        });


    }
    private String pfName, pfInfo;
    private void inputData() {

        //1)input data
         pfName = pfNameET.getText().toString().trim();
         pfInfo = pfInfoET.getText().toString().trim();

        //2) validate data
        if(TextUtils.isEmpty(pfName)){
            Toast.makeText(this,"Name required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pfInfo)){
            Toast.makeText(this,"Description required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updatePortfolio();

    }

    private void updatePortfolio() {

        //3) add data to db
        progressDialog.setMessage("Updating ..");
        progressDialog.show();

        //setup data
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("pfName",pfName);
        hashMap.put("pfInfo",pfInfo);

        //init db
        DatabaseReference pfRef = FirebaseDatabase.getInstance().getReference("portfolio");
        pfRef.child(pfID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update success
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Album Updated...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //update failed
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        //upload image 1
        if(image_uri!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"1";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()){

                                HashMap<String,Object> hashPic1 = new HashMap<>();
                                hashPic1.put("pfLink1",""+downloadImageUri);
                                pfRef.child(pfID).updateChildren(hashPic1);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        //upload pic 2
        if(image_uri2!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"2";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri2 = uriTask.getResult();
                            if (uriTask.isSuccessful()){

                                HashMap<String,Object> hashPic2 = new HashMap<>();
                                hashPic2.put("pfLink2",""+downloadImageUri2);
                                pfRef.child(pfID).updateChildren(hashPic2);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    //upload pic 3
        if(image_uri3!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"3";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri3)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri3 = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String,Object> hashPic3 = new HashMap<>();
                                hashPic3.put("pfLink3",""+downloadImageUri3);
                                pfRef.child(pfID).updateChildren(hashPic3);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        //upload pic 4
        if(image_uri4!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"4";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri4)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri4 = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String,Object> hashPic4 = new HashMap<>();
                                hashPic4.put("pfLink4",""+downloadImageUri4);
                                pfRef.child(pfID).updateChildren(hashPic4);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        //upload pic 5
        if(image_uri5!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"5";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri5)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri5 = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String,Object> hashPic5 = new HashMap<>();
                                hashPic5.put("pfLink5",""+downloadImageUri5);
                                pfRef.child(pfID).updateChildren(hashPic5);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        //upload pic 6
        if(image_uri6!=null){
            String filePathAndName = "portfolio_images/"+""+ pfID+"6";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri6)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            downloadImageUri6 = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String,Object> hashPic6 = new HashMap<>();
                                hashPic6.put("pfLink6",""+downloadImageUri6);
                                pfRef.child(pfID).updateChildren(hashPic6);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void loadPortfolioInfo() {

        DatabaseReference pfRef = FirebaseDatabase.getInstance().getReference("portfolio");
        pfRef.child(pfID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelPortfolio pfModel = snapshot.getValue(ModelPortfolio.class);
                if(pfModel!=null){

                    String pfName = pfModel.getPfName();
                    String pfInfo = pfModel.getPfInfo();
                    String pfLink1 = pfModel.getPfLink1();
                    String pfLink2 = pfModel.getPfLink2();
                    String pfLink3 = pfModel.getPfLink3();
                    String pfLink4 = pfModel.getPfLink4();
                    String pfLink5 = pfModel.getPfLink5();
                    String pfLink6 = pfModel.getPfLink6();

                    pfNameET.setText(pfName);
                    pfInfoET.setText(pfInfo);

                    try{

                        Picasso.get().load(pfLink1).fit().placeholder(R.drawable.ic_add_photo).into(photo1IV);
                        Picasso.get().load(pfLink2).fit().placeholder(R.drawable.ic_add_photo).into(photo2IV);
                        Picasso.get().load(pfLink3).fit().placeholder(R.drawable.ic_add_photo).into(photo3IV);
                        Picasso.get().load(pfLink4).fit().placeholder(R.drawable.ic_add_photo).into(photo4IV);
                        Picasso.get().load(pfLink5).fit().placeholder(R.drawable.ic_add_photo).into(photo5IV);
                        Picasso.get().load(pfLink6).fit().placeholder(R.drawable.ic_add_photo).into(photo6IV);

                    }
                    catch(Exception e){

                        photo1IV.setImageResource(R.drawable.ic_add_photo);
                        photo2IV.setImageResource(R.drawable.ic_add_photo);
                        photo3IV.setImageResource(R.drawable.ic_add_photo);
                        photo4IV.setImageResource(R.drawable.ic_add_photo);
                        photo5IV.setImageResource(R.drawable.ic_add_photo);
                        photo6IV.setImageResource(R.drawable.ic_add_photo);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showImagePickDialog() {
        //option to display dialog
        String[] options = {"Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item clicks
                        if(which==0){
                            //gallery clicked
                            if(checkStoragePermission()){
                                //permission granted
                                pickFromGallery();

                            }
                            else{
                                //permission not granted
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();

    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

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

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    //handle image pick results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode== RESULT_OK){
            if(requestCode== IMAGE_PICK_GALLERY_CODE){
                //image picked from gallery

                //save picked image uri

                if(condition.equalsIgnoreCase("one")){
                    image_uri=data.getData();
                    //set image
                    Picasso.get().load(image_uri).fit().placeholder(R.drawable.ic_add_photo).into(photo1IV);
                }
                else if(condition.equalsIgnoreCase("two")){
                    image_uri2=data.getData();
                    //set image
                    Picasso.get().load(image_uri2).fit().placeholder(R.drawable.ic_add_photo).into(photo2IV);
                }
                else if(condition.equalsIgnoreCase("three")){
                    image_uri3=data.getData();
                    //set image
                    Picasso.get().load(image_uri3).fit().placeholder(R.drawable.ic_add_photo).into(photo3IV);
                }
                else if(condition.equalsIgnoreCase("four")){
                    image_uri4=data.getData();
                    //set image
                    Picasso.get().load(image_uri4).fit().placeholder(R.drawable.ic_add_photo).into(photo4IV);
                }
                else if(condition.equalsIgnoreCase("five")){
                    image_uri5=data.getData();
                    //set image
                    Picasso.get().load(image_uri5).fit().placeholder(R.drawable.ic_add_photo).into(photo5IV);
                }
                else if(condition.equalsIgnoreCase("six")){
                    image_uri6=data.getData();
                    //set image
                    Picasso.get().load(image_uri6).fit().placeholder(R.drawable.ic_add_photo).into(photo6IV);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}