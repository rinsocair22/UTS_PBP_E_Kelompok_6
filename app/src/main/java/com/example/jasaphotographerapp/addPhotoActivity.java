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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class addPhotoActivity extends AppCompatActivity {

    ImageButton backBtn;
    ImageView photo1IV,photo2IV,photo3IV,photo4IV,photo5IV,photo6IV;
    EditText photoNameET,photoInfoET;
    Button btnAddPhoto;
    String condition="",pgID;

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
        setContentView(R.layout.activity_add_photo);



        //init ui views
        backBtn= findViewById(R.id.backBtn);
        photo1IV= findViewById(R.id.photo1IV);
        photo2IV= findViewById(R.id.photo2IV);
        photo3IV= findViewById(R.id.photo3IV);
        photo4IV= findViewById(R.id.photo4IV);
        photo5IV= findViewById(R.id.photo5IV);
        photo6IV= findViewById(R.id.photo6IV);
        photoNameET= findViewById(R.id.photoNameET);
        photoInfoET= findViewById(R.id.photoInfoET);
        btnAddPhoto= findViewById(R.id.btnAddPhoto);

        Intent data = getIntent();
        pgID = data.getStringExtra("pgID");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //init permission arrays
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

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


    }

     String pfName, pfInfo;
    private void inputData() {


        //1)input data
        pfName = photoNameET.getText().toString().trim();
        pfInfo = photoInfoET.getText().toString().trim();
        //2) validate data
        if(TextUtils.isEmpty(pfName)){
            Toast.makeText(this,"Name required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pfInfo)){
            Toast.makeText(this,"Description required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri2==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri3==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri4==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri5==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(image_uri6==null){
            Toast.makeText(this,"Please insert all images ", Toast.LENGTH_SHORT).show();
            return;
        }
        addPhoto();
    }

    private void addPhoto() {

        //3) add data to db
        progressDialog.setMessage("Uploading ..");
        progressDialog.show();

        //init db
        DatabaseReference pfRef = FirebaseDatabase.getInstance().getReference("portfolio");
        String pfID = pfRef.push().getKey();

        //setup data
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("pfID", pfID);
        hashMap.put("pgID", pgID);
        hashMap.put("pfName",pfName);
        hashMap.put("pfInfo",pfInfo);
        //add to db
        pfRef.child(pfID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added to db
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Album Added",Toast.LENGTH_SHORT).show();
                        clearData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail to add
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
                            Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
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



       /* //upload image 2
        if(image_uri2!=null){

            String filePathAndName = "portfolio_images/"+""+ pfID+"2";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get url of uploaded image
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadImageUri2 = uriTask.getResult();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }*/

        /*//upload images 3
        if(image_uri3!=null){

            String filePathAndName = "portfolio_images/"+""+ pfID+"3";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get url of uploaded image
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadImageUri3 = uriTask.getResult();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }*/


    }

    private void clearData() {
        //clear data after uploading package
        photoNameET.setText("");
        photoInfoET.setText("");
        photo1IV.setImageResource(R.drawable.ic_add_photo);
        photo2IV.setImageResource(R.drawable.ic_add_photo);
        photo3IV.setImageResource(R.drawable.ic_add_photo);
        photo4IV.setImageResource(R.drawable.ic_add_photo);
        photo5IV.setImageResource(R.drawable.ic_add_photo);
        photo6IV.setImageResource(R.drawable.ic_add_photo);
        image_uri=null;
        image_uri2=null;
        image_uri3=null;
        image_uri4=null;
        image_uri5=null;
        image_uri6=null;
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