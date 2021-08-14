package com.example.todolist.BottomActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.MainActivity;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityCreateNoteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import static com.example.todolist.Adapter.NotesAdapter.createNotificationChannel;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding binding;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    String taskName;
    String taskDescription;
    int priority;
    String category;
    String date = "date";
    String startTime = "startTime";
    String endTime = "endTime";
    Boolean remindMe;
    int cyear, cmonth, cday;
    int shour, smin, ehour, emin;
    Intent MainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        createNotificationChannel(this);

        //Initialize intent
        MainIntent = new Intent(this, MainActivity.class);

        //Click on calender
        binding.calendarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
            }
        });

        //Click on StartTime
        binding.startTimeTextId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartTime();
            }
        });

        //Click on EndTime
        binding.endTimeTextId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndTime();
            }
        });

        //Click on Create Button -
        binding.createButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskName = binding.notesTaskNameId.getText().toString();
                taskDescription = binding.notesTaskDescriptionId.getText().toString();

                priority = getPriority();
                category = getCategory();

                //Check if Remind me is selected
                remindMe = binding.remindMeId.isChecked();

                System.out.println("Task name: " + taskName +
                        " Task Desc: " + taskDescription +
                        " Priority: " + priority +
                        " Category: " + category +
                        "Remind me: " + remindMe +
                        "Date: " + date
                );

                //Check if any section is empty -
                if(isNullOrEmpty(taskName) || isNullOrEmpty(taskDescription))
                {
                    Toast.makeText(getApplicationContext(), "Task name or description cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(priority == 0)
                {
                    Toast.makeText(getApplicationContext(), "Select Priority", Toast.LENGTH_SHORT).show();
                }
                else if(category == "abc")
                {
                    Toast.makeText(getApplicationContext(), "Select Category", Toast.LENGTH_SHORT).show();
                }
                else if(date == "date")
                {
                    Toast.makeText(getApplicationContext(), "Choose date for the task", Toast.LENGTH_SHORT).show();
                }
                else if(startTime == "startTime" || endTime == "endTime")
                {
                    Toast.makeText(getApplicationContext(), "Choose time for the task", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SharedPreferences pref = getSharedPreferences(MyConstants.MYPREFERENCES, MODE_PRIVATE);
                    String mail = pref.getString("userMail", "");

                    // todo Create Collection ----
                    NotesDataClass note = new NotesDataClass(taskName, taskDescription, priority, remindMe, startTime, endTime, date, category);
                    createNote(note, mail);
                }

                startActivity(MainIntent);
            }
        });

        //Back button
        binding.backButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void createNote(NotesDataClass note, String mail) {

        String key = createDocKey(date, startTime, endTime);

        db.collection("Users")
                .document(mail)
                .collection("Notes")
                .document(key)
                .set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Note created successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something want wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // This function gives us the key
    public static String createDocKey(String date, String startTime, String endTime)
    {
        date = date.replaceAll("/", "");
        System.out.println("date: " + date);
        String key = date + startTime + endTime;
        Log.i("KEY__", key);
        return key;
    }


    // --------------------------------------------------------------------------------


    private Boolean isNullOrEmpty(String val)
    {
        return (val == null) || (val.isEmpty());
    }


    private void getDate()
    {
        Calendar calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        cday = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dPD = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = (dayOfMonth) + "/" + (month+1) + "/" + year;
                binding.calendarTextId.setText(date);
            }
        }, cyear, cmonth, cday);

        dPD.show();
    }

    private void getStartTime()
    {
        Calendar calendar = Calendar.getInstance();
        shour = calendar.get(Calendar.HOUR_OF_DAY);
        smin = calendar.get(Calendar.MINUTE);

        TimePickerDialog tPD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime = hourOfDay + ":" + minute;
                binding.startTimeTextId.setText(startTime);
            }
        }, shour, smin, android.text.format.DateFormat.is24HourFormat(this));

        tPD.show();
    }

    private void getEndTime()
    {
        Calendar calendar = Calendar.getInstance();
        ehour = calendar.get(Calendar.HOUR_OF_DAY);
        emin = calendar.get(Calendar.MINUTE);

        TimePickerDialog tPD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime = hourOfDay + ":" + minute;
                binding.endTimeTextId.setText(endTime);
            }
        }, ehour, emin, android.text.format.DateFormat.is24HourFormat(this));

        tPD.show();
    }

    private int getPriority()
    {
        RadioButton radioButton;
        RadioGroup radioGroup = binding.priorityGroupId;
        int prio = 0;

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        
        if(selectedId == R.id.radio_low_id)
        {
            prio = 1;
        }
        else if(selectedId == R.id.radio_med_id)
        {
            prio = 2;
        }
        else if(selectedId == R.id.radio_high_id)
        {
            prio = 3;
        }
        
        return prio;
    }

    private String getCategory()
    {
        RadioButton radioButton;
        RadioGroup radioGroup = binding.categoryGroupId;
        String cat = "abc";

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        if(selectedId == R.id.radio_work_id)
        {
            cat = "Work";
        }
        else if(selectedId == R.id.radio_family_id)
        {
            cat = "Family";
        }
        else if(selectedId == R.id.radio_school_id)
        {
            cat = "School";
        }

        return cat;
    }
}