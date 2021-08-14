package com.example.todolist.BottomActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    NotesDataClass note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = this.getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            note = (NotesDataClass) getIntent().getSerializableExtra("Notes"); //Obtaining data
        }


        //Back button
        binding.backButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String prio;
        if(note.priority == 1)
        {
            prio = "Low";

        }
        else if(note.priority == 2)
        {
            prio = "Medium";
        }
        else
        {
            prio = "High";
        }

        binding.detailsTaskNameId.setText(note.task_name);
        binding.detailsTaskDescriptionId.setText(note.task_description);
        binding.detailsStartTimeTextId.setText(note.note_start_time);
        binding.detailsEndTimeTextId.setText(note.note_end_time);
        binding.detailsCalendarTextId.setText(note.note_date);
        binding.detailsPriorityButtonId.setText(prio);
        binding.detailsCategoryButtonId.setText(note.category);
    }
}