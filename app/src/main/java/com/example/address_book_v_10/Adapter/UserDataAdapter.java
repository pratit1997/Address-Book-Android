package com.example.address_book_v_10.Adapter;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.address_book_v_10.Model.Data;
import com.example.address_book_v_10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder> {
    Context context;
    ArrayList<Data> list;


    public UserDataAdapter(Context context, ArrayList<Data> list) {
        this.context = context;
        this.list = list;
        Log.d(TAG, "on create"+list);
    }


    @NonNull
    @Override
    public UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new UserDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDataViewHolder holder, int position) {
        Data info = list.get(position);
        holder.Name.setText(info.getFirstName() + " " + info.getLastName());
        holder.Email.setText(info.getEmail());
        holder.Phone.setText(info.getPhone());

        Picasso.get().load(info.getImage()).fit().into(holder.profile_image);

        Log.d(TAG, "onBindViewHolder: " + info.getId());

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PostDelete(adminUserData.getId());
                FirebaseFirestore fstore = FirebaseFirestore.getInstance();

                DocumentReference docRef = fstore.collection("Info").document(info.getId());
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tagvv", "DocumentSnapshot successfully deleted!");
                        Toast.makeText(context, "User Deleted Successfully", Toast.LENGTH_SHORT).show();

                        PostDelete(info.getId());


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tagvv", "Error deleting document", e);
                            }
                        });
            }

        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    private void PostDelete(String id) {
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        CollectionReference docref = fstore.collection("Info");

// Create a query against the collection.
        Query query = docref.whereEqualTo("UserID", id);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        docref.document(document.getId()).delete();
                        Log.d(TAG, "onComplete: " + document.getId());
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }


    public class UserDataViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Email, Phone;
        ImageView delete_btn, profile_image;

        public UserDataViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.userName);
            Email = itemView.findViewById(R.id.userEmail);
            Phone = itemView.findViewById(R.id.userPhoneNumber);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
    }
}
