package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.todolist.BottomActivities.AllNotesActivity;
import com.example.todolist.BottomActivities.CalendarActivity;
import com.example.todolist.BottomActivities.CreateNote;
import com.example.todolist.BottomActivities.SearchActivity;
import com.example.todolist.Profile.ProfileActivity;
import com.example.todolist.databinding.ActivityMainBinding;
import com.example.todolist.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

import static com.example.todolist.Adapter.NotesAdapter.createNotificationChannel;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Fragment f;
    Intent notificationIntent;
    Intent calendarIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        createNotificationChannel(this);

        notificationIntent = new Intent(this, AllNotesActivity.class);

        //Initialize intent
        Intent searchIntent = new Intent(this, SearchActivity.class);

        calendarIntent = new Intent(this, CalendarActivity.class);

        binding.searchId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(searchIntent);
            }
        });

        // todo remove
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_id, new HomeFragment()).commit();


        Intent intent = new Intent(this, ProfileActivity.class);
        Intent floatingButtonIntent = new Intent(this, CreateNote.class);
        //When fav Icon is clicked
        binding.floatingActionButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(floatingButtonIntent);
            }
        });

        //BottomNavigation
        binding.navigationId.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.Home:
                    {
                        f = new HomeFragment();
                        break;
                    }
                    case R.id.Dashboard:
                    {
                        startActivity(intent);
                        break;
                    }
                    case R.id.Notification:
                    {
                        startActivity(notificationIntent);
                        break;
                    }
                    case R.id.Calendar:
                    {
                        startActivity(calendarIntent);
                        break;
                    }
                }

                if(f != null)
                {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_id, f)
                            .commit();
                }

                return true;
            }
        });
    }

}