package com.example.address_book_v_10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;
public class Addcontact extends AppCompatActivity {

    final int GALLERY_REQUEST_CODE = 105;
    FirebaseFirestore fstore;
    FirebaseAuth auth;
    ImageView selectedImage, selectedImage1, selectedImage2, selectedImage3, upload;

    FirebaseStorage storage;

    StorageReference storageReference;
    ArrayList<Uri> contenturi = new ArrayList<Uri>();
    private TextInputLayout et_model,et_address, et_description, et_amount, et_phone_number, et_seaters, et_Car_Classification, et_color, et_power, et_year;
    RadioGroup rbGps, rbAirbags, rbParking, rbFuelType, rbBluetooth, rbair_conditioning, rbTransmission, rbpowerwindow, rb_bssenser, rbsroof, rbaids, rbcondition;
    RadioButton btn_Gps,btn_Airbags, btn_Parking, btn_FuelType, btn_Bluetooth, btn_air_conditioning, btn_Transmission, btn_powerwindow, btn__bssenser, btn_sroof, btn_aids, btn_condition;
    int photos = 0;
    TextInputLayout fname,lname,email,phone;
    ImageView image;
    Button add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);
        Log.d(TAG, "onCreate: ");
        fname=findViewById(R.id.add_fname);
        lname=findViewById(R.id.add_lname);
        email=findViewById(R.id.add_email);
        phone=findViewById(R.id.add_phone);
        image =(ImageView)findViewById(R.id.add_image);
        add_btn=findViewById(R.id.submit_post);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fstore = FirebaseFirestore.getInstance();
                final String f = fname.getEditText().getText().toString().trim();
                final String l = lname.getEditText().getText().toString().trim();
                final String e = email.getEditText().getText().toString().trim();
                final String p = phone.getEditText().getText().toString().trim();
                if (f.isEmpty()) {
                    Toast.makeText(Addcontact.this, "Please Enter FirstName", Toast.LENGTH_LONG).show();
                    return;
                }else if (l.isEmpty()) {
                    Toast.makeText(Addcontact.this, "Please Enter LastName", Toast.LENGTH_LONG).show();
                    return;
                }else if (e.isEmpty()) {
                    Toast.makeText(Addcontact.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }if (p.isEmpty()) {
                    Toast.makeText(Addcontact.this, "Please Enter Phone", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    final ProgressDialog pd;
                    pd = new ProgressDialog(Addcontact.this);
                    pd.setMessage("Loading...");
                    pd.show();

                auth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = auth.getCurrentUser();
                String uid = firebaseUser.getUid();
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("UserID", uid);
                userMap.put("fname", f);
                userMap.put("lname", l);
                userMap.put("email", e);
                userMap.put("phone", p);
                fstore.collection("Info").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Demoooooooo1o1", "onSuccess: "+documentReference.getId());
                        uploadImage((String) documentReference.getId());
                        Toast.makeText(Addcontact.this, " Post added Successfully ", Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String Error = e.getMessage();
                        Toast.makeText(Addcontact.this, " Error:" + Error, Toast.LENGTH_SHORT).show();
                    }
                });
                }


            }
        });

}
    private void selectImage() {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Addcontact.this);
        builder1.setTitle("Add Photo!");
        builder1.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    contenturi.clear();

                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    gallery.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(gallery, ""), GALLERY_REQUEST_CODE);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder1.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ClipData clipdata = data.getClipData();

                if (clipdata != null) {
                    photos = clipdata.getItemCount();
                    if (clipdata.getItemCount() > 4) {
                        Toast.makeText(this, "Please select only four items", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
                        ClipData.Item item = clipdata.getItemAt(i);
                        contenturi.add(item.getUri());
                    }
                } else {
                    contenturi.add(data.getData());
                    photos = 1;
                }
            }
        }
    }

    private void uploadImage(String id) {
        storageReference = FirebaseStorage.getInstance().getReference();
        for (int j = 0; j < contenturi.size(); j++) {

            StorageReference ref = storageReference.child("profiles").child(id + "/" + j);
            ref.putFile(contenturi.get(j))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Addcontact.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }
}