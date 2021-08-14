package com.example.todolist.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.todolist.Authentication.LoginActivity;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        Intent intent= new Intent(this, LoginActivity.class);
        //Logout
        binding.logoutId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(intent);
            }
        });

        // Update ui with name and mail
        updateFields();

        //OnBackButtonPressed
        binding.profileBackId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void updateFields() {
        SharedPreferences pref = getSharedPreferences(MyConstants.MYPREFERENCES, MODE_PRIVATE);

        String name = pref.getString("userName", "");
        String mail = pref.getString("userMail", "");

        System.out.println("name: "+name+"\n"+"mail: "+mail);

        if(name != null && !name.isEmpty() && mail != null && !mail.isEmpty())
        {
            binding.profileNameId.setText(name);
            binding.profileMailId.setText(mail);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }
}