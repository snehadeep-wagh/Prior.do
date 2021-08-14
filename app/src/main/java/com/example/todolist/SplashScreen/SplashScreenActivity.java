package com.example.todolist.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.todolist.Authentication.LoginActivity;
import com.example.todolist.MainActivity;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        @Nullable
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Intent intent;
        if(currentUser != null )
        {
            if(currentUser.isEmailVerified())
            {
                intent = new Intent(this, MainActivity.class);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please Verify your mail!", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, LoginActivity.class);
            }

        }
        else
        {
            Log.i("check", "Login: done");
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }
}