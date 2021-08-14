package com.example.todolist.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.todolist.DataClasses.ProfileDataClass;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.Serializable;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userName;
    private String userMail;
    private String userPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //on register button clicked
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = binding.registerNameId.getText().toString();
                userMail = binding.registerUsernameId.getText().toString();
                userPass = binding.registerPasswordId.getText().toString();

                if(!userPass.isEmpty() && !userName.isEmpty() && !userMail.isEmpty() && userMail != null && userName != null && userPass != null)
                {
                    createUser(userName, userMail, userPass);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "All sections are compulsory!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // on login button is clicked
        Intent intent = new Intent(this, LoginActivity.class);
        binding.loginRegisterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    private void createUser(String userName, String userMail, String userPass) {
        auth.createUserWithEmailAndPassword(userMail, userPass )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = task.getResult().getUser();

                            sentVerificationMail(user);

                            ProfileDataClass profile = new ProfileDataClass(userName, userMail);


                            //Create new user in database ------------
                            db.collection("Users")
                                    .document(userMail).set(profile);

                        }else
                        {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                // If already registered
                                Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_LONG).show();
                            }
                            if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                //if password not 'stronger'
                                Toast.makeText(getApplicationContext(), "Weak Password", Toast.LENGTH_LONG).show();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //If email are in incorret format
                                Toast.makeText(getApplicationContext(), "Check mail format", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sentVerificationMail(FirebaseUser user)
    {
        Intent intent = new Intent(this, LoginActivity.class);

        //Send Verification mail
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), String.format("Verification mail sent to %s", userMail), Toast.LENGTH_LONG).show();

                        //Sent to login page
                        startActivity(intent);
                    }
                });
    }

    private void CreateUserDatabase()
    {

    }


}