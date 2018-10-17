package com.alphatech.www.nfcbusinesscardlocal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class updatePicture extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    private User person;
    //firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ProgressDialog mProgressDialog;
    private FirebaseUser firebaseUser;
    //variables
    private Uri filepath;
    private ImageView imageView;
    private Button selection,upload,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_picture);
        mProgressDialog=new ProgressDialog(this);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference();
        imageView=(ImageView)findViewById(R.id.image_selected);
        selection=(Button) findViewById(R.id.btn_select_image);
        upload=(Button) findViewById(R.id.btn_upload_image);
        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(updatePicture.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(updatePicture.this);
                }
                builder.setTitle("About to upload image.")
                        .setMessage("Are you sure you want to update your image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImage();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Toast.makeText(updatePicture.this, "Upload cancelled.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mProgressDialog.setMessage("Loading your details...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person=dataSnapshot.getValue(User.class);
                mProgressDialog.dismiss();


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Unable to load details, try again.",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }
    private void fileChooser()
    {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            filepath=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                if(bitmap.getWidth()>bitmap.getHeight())
                {
                    Bitmap altBitmap=Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(),false);
                    imageView.setImageBitmap(altBitmap);
                }
                else
                {
                    imageView.setImageBitmap(bitmap);
                }
                Toast.makeText(getApplicationContext(), "Image Selected.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Unable to load image",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(){
        if(filepath!=null)
        {
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image....");
            progressDialog.show();
            StorageReference Ref = storageReference.child("images/"+firebaseUser.getUid()+".jpg");
            Ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            person.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                            progressDialog.dismiss();
                            Toast.makeText(updatePicture.this,"Photo uploaded",Toast.LENGTH_SHORT).show();
                            databaseReference.setValue(person);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(updatePicture.this,"Picture not uploaded.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage((int)progress+"% Uploaded...");
                }
            });
        }
        else{
            Toast.makeText(this, "No image to upload.", Toast.LENGTH_SHORT).show();
        }
    }

    private void previous()
    {
     onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
