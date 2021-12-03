package com.example.address_book_v_10;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    public TextInputLayout Email, Password;
    Button login,CreateAccount;
    SharedPreferences sp;
    private FirebaseUser curUser;

    private FirebaseAuth auth;
    FirebaseFirestore fStore;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        CreateAccount=findViewById(R.id.create);
//
        auth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });
//login.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        Intent i = new Intent(getApplicationContext(),Addcontact.class);
//                            startActivity(i);
//    }
//});
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getEditText().getText().toString();
                String pwd = Password.getEditText().getText().toString();


                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USEREmailID", email);
                editor.putString("USERPassword", pwd);
                editor.commit();
                Log.v("tagvv", " hello1");
                System.out.println(email + "" + pwd);
                if (email.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Fill The Form", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Loading...");
                pd.show();
                Log.v("tagvv", " hello2");
                auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.v("tagvv", " hello3");
                            curUser = auth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), Contacts.class);
                            startActivity(i);
                        }
                        else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(getApplicationContext(), "Email not exist!", Toast.LENGTH_LONG).show();
                                Email.getEditText().getText().clear();
                                Password.getEditText().getText().clear();
                                Email.setError("Email not exist!");
                                Email.requestFocus();
                                pd.dismiss();
                                return;
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getApplicationContext(), "Wrong Credential!", Toast.LENGTH_LONG).show();
                                Password.getEditText().getText().clear();
                                Email.requestFocus();
                                pd.dismiss();
                                return;
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        }
                    }
                });
            }
        });

    }
}