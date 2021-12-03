package com.example.address_book_v_10;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.address_book_v_10.Adapter.UserDataAdapter;
import com.example.address_book_v_10.Model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Contacts<swipeContainer> extends AppCompatActivity {
FloatingActionButton add_btn;
    StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser curUser;
    String UserId;
    UserDataAdapter DataAdapter;
    RecyclerView Recycler;
    ArrayList<Data> A_List = new ArrayList<>();
 SearchView searchView;
    FirebaseFirestore fstore;
    FirebaseFirestore db;

//    swipeContainer = (SwipeRefreshLayout) findViewById(R.id.refresh_List);
//    // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh() {
//            A_List.clear();
//            getData();
//            swipeContainer.setRefreshing(false);
//        }
//    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        add_btn=findViewById(R.id.fab_btn);
        Recycler=findViewById(R.id.Data_rv);
        searchView=findViewById(R.id.search_bar);
        getData();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(),Addcontact.class);
                    startActivity(i);

            }
        });

    }
    private void getData() {
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Info").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = (String) document.getData().get("email");
                        String fname = (String) document.getData().get("fname");
                        String lname = (String) document.getData().get("lname");
                        String phone = (String) document.getData().get("phone");

                        String UserID=document.getId();
                        Log.d(TAG, "onComplete: "+UserID+" "+fname);
                        getImage(UserID,fname,lname,email,phone);

                        setDataAdapter();

                    }
                }
            }
        });

    } private void getImage(final String UserID,final String fname,final String lname,final String email,final String phone) {
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profiles/" + UserID + "/0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                A_List.add(new Data(UserID,fname,lname,email,phone, uri));
                Log.d(TAG, "onSuccess: "+uri);
                DataAdapter.notifyDataSetChanged();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("TAG", "image not got");
            }
        });
    }


    private void setDataAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        Recycler.setLayoutManager(layoutManager);
        DataAdapter = new UserDataAdapter(Contacts.this,A_List);
        Recycler.setAdapter(DataAdapter);
    }
}