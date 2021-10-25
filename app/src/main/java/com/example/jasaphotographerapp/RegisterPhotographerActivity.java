package com.example.jasaphotographerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterPhotographerActivity extends AppCompatActivity implements LocationListener {

    EditText fullnameLabel, emailLabel, passwordLabel,phoneNoLabel,typeLabel,cityEt,city2Et;
    Button btnRegister;
    ImageView profileIv, backBtn;
    TextView btnLogin;
    FirebaseAuth fAuth;
    String uid;
    private String city,city2;
    ImageButton gpsBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    private ProgressDialog progressDialog;

    //permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private  static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private  static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permission arrays
    private String [] cameraPermission;
    private String [] storagePermission;
    private String[] locationPermissions;
    private double latitude, longitude;
    //image picked url
    private Uri image_uri;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photographer);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //hook to all xml element in activity_register_photographer.xml
        fullnameLabel    = findViewById(R.id.editTextName);
        emailLabel       = findViewById(R.id.editTextEmail);
        passwordLabel    = findViewById(R.id.editTextPassword);
        phoneNoLabel     = findViewById(R.id.editTextPhone);
        typeLabel        = findViewById(R.id.editTextType);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin    = findViewById(R.id.textViewRegister);
        profileIv = findViewById(R.id.profileIv);
        cityEt = findViewById(R.id.cityEt);
       /* city2Et    = findViewById(R.id.city2Et);*/
        gpsBtn = findViewById(R.id.gpsBtn);
        backBtn = findViewById(R.id.backBtn);

        fAuth = FirebaseAuth.getInstance();

        //init permission arrays
        cameraPermission= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        typeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pick type
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

        cityEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDialog();
            }
        });

       /* city2Et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location2Dialog();
            }
        });*/

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }
            }
        });

        //tekan button register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = emailLabel.getText().toString().trim();
                String password = passwordLabel.getText().toString().trim();
                String fullName = fullnameLabel.getText().toString();
                String phoneNo  = phoneNoLabel.getText().toString();
                String type  = typeLabel.getText().toString();
                city = cityEt.getText().toString().trim();
                /*city2 = city2Et.getText().toString().trim();*/


                //check if empty
                if(TextUtils.isEmpty(city)){

                    cityEt.setError (" City is required");
                    return;
                }
                /*if(TextUtils.isEmpty(city2)){

                    city2Et.setError (" City is required");
                    return;
                }*/
                if(TextUtils.isEmpty(email)){
                    emailLabel.setError (" Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordLabel.setError (" Password is required");
                    return;
                }

                if(password.length() < 6){
                    passwordLabel.setError("Password Must be atleast 6 Characters");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    fullnameLabel.setError (" Name is required");
                    return;
                }

                if(TextUtils.isEmpty(phoneNo)){
                    phoneNoLabel.setError (" Phone Number is required");
                    return;
                }
                if(TextUtils.isEmpty(type)){
                    typeLabel.setError (" Type is required");
                    return;
                }


                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //init db photographer
                            rootNode= FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("photographer");
                            uid = fAuth.getCurrentUser().getUid();

                            if(image_uri==null){
                                //save info without image
                                String pgIcon="";

                                //setup data
                                ModelPhotographer pg = new ModelPhotographer(fullName,email,phoneNo,password,type,uid,pgIcon);
                                //save to db

                                //create database pakai id
                                reference.child(fAuth.getCurrentUser().getUid()).setValue(pg);

                                Toast.makeText(RegisterPhotographerActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginPhotographerActivity.class));
                            }
                            else{
                                //save info with image

                                //name and path of image
                                String filePathAndName = "profile_images/"+""+uid;

                                //upload image
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                                storageReference.putFile(image_uri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                //get url of uploaded image
                                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!uriTask.isSuccessful());
                                                Uri downloadImageUri = uriTask.getResult();
                                                String pgIcon = ""+downloadImageUri; // url image

                                                if(uriTask.isSuccessful()){


                                                    //setup data
                                                    ModelPhotographer pg = new ModelPhotographer(fullName,email,phoneNo,password,type,uid,pgIcon);
                                                    //save to db

                                                    //create database pakai id
                                                    reference.child(fAuth.getCurrentUser().getUid()).setValue(pg);

                                                    Toast.makeText(RegisterPhotographerActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), LoginPhotographerActivity.class));

                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext()," "+e.getMessage(),Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                            //init db location 1
                            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("location");
                            String locationID =locationRef.push().getKey();

                            //setup data for location 1
                            HashMap<String, Object> hashMap = new HashMap <>();

                            hashMap.put("locationID", locationID);
                            hashMap.put("locationName",city);
                            hashMap.put("pgID",fAuth.getCurrentUser().getUid());

                            //add to db
                            locationRef.child(locationID).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //added to db
                                            //Toast.makeText(RegisterPhotographerActivity.this,"Location Added", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //fail to add
                                    Toast.makeText(RegisterPhotographerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        else {
                            Toast.makeText(RegisterPhotographerActivity.this, "Error Creating User!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPhotographerActivity.class));
            }
        });
    }


    private void locationDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("city:")
                .setItems(Constants.pgLocation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String city = Constants.pgLocation[which];
                        //set picked category
                        cityEt.setText(city);
                    }
                })
                .show();

    }
   
    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 10, this);
    }
    private boolean checkLocationPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestLocationPermission(){

        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude= location.getLongitude();

        findAddress();
    }

    private void findAddress() {

        //find city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try{
            addresses = geocoder.getFromLocation(latitude,longitude,1);

            String address = addresses.get(0).getAddressLine(0); //complete address
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set addresses
            cityEt.setText(city);


        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disable
        Toast.makeText(this,"Please enable location", Toast.LENGTH_SHORT).show();
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
                        typeLabel.setText(category);
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
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission allowed
                        detectLocation();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this,"Location permission is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
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