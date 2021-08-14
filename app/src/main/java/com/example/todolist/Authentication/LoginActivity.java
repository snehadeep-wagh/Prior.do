package com.example.todolist.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todolist.MainActivity;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Intent intent, MainIntent;
    private Dialog dialog;
    public FirebaseAuth UserAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String UserMail;
    String UserPass;
    private static String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Initialize RegisterActivity intent
        intent = new Intent(this, RegisterActivity.class);

        // Initialize MainActivity intent
        MainIntent = new Intent(this, MainActivity.class);

        // Initialize Dialog
        dialog = new Dialog(this);

        //Sign in button
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserMail = binding.loginUsernameId.getText().toString();
                UserPass = binding.loginPasswordId.getText().toString();

                if(UserMail.length() != 0 && UserPass.length() != 0)
                {
                    SignInTheUser(UserMail, UserPass);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Mail or Password cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // todo sharefprefs
                DocumentReference docref = db.collection("Users").document(UserMail);
                        docref.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        UserName = task.getResult().getString("name");
                                        SharedPreferences pref = getSharedPreferences(MyConstants.MYPREFERENCES, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();

                                        editor.putString("userName", UserName);
                                        editor.putString("userMail", UserMail);
                                        editor.commit();
                                    }
                                });
            }
        });


        //Redirect to Register Activity
        binding.loginRegisterId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        //Clicked on Forgot Button
        binding.loginForgotId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword();
            }
        });
    }

    private void SignInTheUser(String mail, String pass) {
        UserAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = task.getResult().getUser();
                            if(user.isEmailVerified())
                            {
                                Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(MainIntent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Please Verify your mail", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("ERRR: "+e.getMessage());
            }
        });
    }

    private void ForgotPassword()
    {
        // TODO: 10-08-2021 complete forgot password

        @Nullable
        final String[] Mail = new String[1];

        dialog.setContentView(R.layout.forgot_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button resetButton = dialog.findViewById(R.id.forgot_button_id);
        EditText mailId = dialog.findViewById(R.id.forgot_edittext_id);

        //on Reset Button Clicked
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Mail[0] = mailId.getText().toString();
                if(Mail[0] != null && !Mail[0].isEmpty())
                {
                    UserAuth.sendPasswordResetEmail(Mail[0])
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), String.format("Reset mail is sent to %s", Mail[0]), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Mail cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }
}