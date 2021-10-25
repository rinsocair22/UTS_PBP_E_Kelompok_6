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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPackageActivity extends AppCompatActivity {

    EditText etPackageName, etPackagePrice,etPackageType,etPackageDescription;
    Button btnAddPackage;
    ImageView packageIcon;
    ImageButton backBtn;

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

     FirebaseAuth fAuth;
     DatabaseReference reference;
     FirebaseDatabase rootNode;

    private ProgressDialog progressDialog;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        etPackageName= findViewById(R.id.etPackageName);
        etPackagePrice=findViewById(R.id.etPackagePrice);
        etPackageType = findViewById(R.id.etPackageType);
        etPackageDescription = findViewById(R.id.etPackageDescription);
        btnAddPackage = findViewById(R.id.btnAddPackage);
        packageIcon= findViewById(R.id.packageIcon);
        backBtn= findViewById(R.id.backBtn);

        fAuth=FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //init permission arrays
        cameraPermission= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        packageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog to pick image
                showImagePickDialog();
            }
        });


        etPackageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick type
                categoryDialog();
            }
        });

        btnAddPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow:
                //1)input data
                //2)validate data
                //3) add data to db
                inputData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private String packageName,packageDescription,packageType,packagePrice;

    private void inputData() {
        //1)input data
        packageName = etPackageName.getText().toString().trim();
        packagePrice = etPackagePrice.getText().toString().trim();
        packageType = etPackageType.getText().toString().trim();
        packageDescription = etPackageDescription.getText().toString().trim();

        //2) validate data
        if(TextUtils.isEmpty(packageName)){
            Toast.makeText(this,"Name required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(packagePrice)){
            Toast.makeText(this,"Price required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(packageType)){
            Toast.makeText(this,"Type required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(packageDescription)){
            Toast.makeText(this,"Package description required", Toast.LENGTH_SHORT).show();
            return;
        }


        addPackage();
    }

    private void addPackage() {

        //3) add data to db
        progressDialog.setMessage("Adding Package..");
        progressDialog.show();
        //init db
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("package");
        String packageID = reference.push().getKey();

        if(image_uri== null){
            //upload without image
            //setup data
            HashMap <String,Object> hashMap = new HashMap<>();
            hashMap.put("packageID", packageID);
            hashMap.put("packageName",packageName);
            hashMap.put("packagePrice",packagePrice);
            hashMap.put("packageType",packageType);
            hashMap.put("packageDescription",packageDescription);
            hashMap.put("packageIcon","");//no image,set empty
            hashMap.put("pgID", fAuth.getCurrentUser().getUid());
            //add to db
            reference.child(packageID).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(AddPackageActivity.this,"Package Added",Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //fail to add
                    progressDialog.dismiss();
                    Toast.makeText(AddPackageActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            //upload with image
            //name and path of image to be uploaded
            String filePathAndName = "package_images/"+""+ packageID;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded
                            //get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //url of image received,upload todb

                                //setup data
                                HashMap <String,Object> hashMap = new HashMap<>();
                                hashMap.put("packageID", packageID);
                                hashMap.put("packageName",packageName);
                                hashMap.put("packagePrice",packagePrice);
                                hashMap.put("packageType",packageType);
                                hashMap.put("packageDescription",packageDescription);
                                hashMap.put("packageIcon",""+downloadImageUri);
                                hashMap.put("pgID", fAuth.getCurrentUser().getUid());
                                //add to db
                                reference.child(packageID).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPackageActivity.this,"Package Added",Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //fail to add
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPackageActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPackageActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void clearData() {
        //clear data after uploading package
        etPackageName.setText("");
        etPackageDescription.setText("");
        etPackageType.setText("");
        etPackagePrice.setText("");
        packageIcon.setImageResource(R.drawable.ic_add_photo);
        image_uri=null;
    }
    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Package Type")
                .setItems(Constants.packageType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = Constants.packageType[which];
                        //set picked category
                        etPackageType.setText(category);
                    }
                })
                .show();


    }

    private void showImagePickDialog() {
        //option to display dialog
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
                packageIcon.setImageURI(image_uri);
            }
            else if (requestCode== IMAGE_PICK_CAMERA_CODE){
                //image picked from camera
                packageIcon.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}