package com.example.practica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    ImageView imageProfile;
    ProgressDialog progressDialog;
    private  static final int CAMERA_REQUEST = 100;
    private static  final int IMAGE_PICK_CAMERA_REQUEST = 400;

    String cameraPermission[];
    Uri imageUri;
    String profileOrCoverImage;
    MaterialButton editImage;

    MaterialButton btn_save;
    TextInputLayout tl_name, tl_age, tl_location, tl_phone;
    TextInputEditText te_name, te_age, te_location, te_phone;

    TextView name, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editImage = findViewById(R.id.edit_image);
        imageProfile = findViewById(R.id.profile_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);

        te_name = findViewById(R.id.text_edit_name);
        te_phone = findViewById(R.id.text_edit_phone);



        tl_name = findViewById(R.id.text_layout_name);
        tl_age = findViewById(R.id.text_layout_age);
        tl_location = findViewById(R.id.text_layout_location);
        tl_phone = findViewById(R.id.text_layout_phone);

        btn_save = findViewById(R.id.button_save);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Profile Picture");
                profileOrCoverImage = "image";
                showImagePicDialog();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_save.getText() == getResources().getString(R.string.btn_edit)){
                    btn_save.setText(R.string.btn_save);
                    tl_name.setEnabled(true);
                    tl_age.setEnabled(true);
                    tl_location.setEnabled(true);
                    tl_phone.setEnabled(true);
                }else{
                    name.setText(""+te_name.getText());
                    phone.setText(""+te_phone.getText());
                    tl_name.setEnabled(false);
                    tl_age.setEnabled(false);
                    tl_location.setEnabled(false);
                    tl_phone.setEnabled(false);
                    tl_name.requestFocus();
                    btn_save.setText(R.string.btn_edit);
                    Toast.makeText(MainActivity.this, "Informaci??n guardada",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected  void  onPause(){
        super.onPause();
        Glide.with(this).load(imageUri).into(imageProfile);
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch (requestCode){
            case CAMERA_REQUEST: {
                if(grantResult.length > 0) {
                    boolean cameraAccepted = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResult[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this,"Please enable camera and storage permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Something went wrong! try again...", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which ==0) {
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
            }
        });
        builder.create().show();
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void  requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST);
    }
/*
    public void ValidData(){
        if(te_name.getText().toString().equals("")){
            tl_name.setError("Completar campo");
        }else{
            tl_name.setError(null);
            tl_age.setError(null);
            tl_location.setError(null);
            tl_phone.setError(null);
        }
    }*/
}