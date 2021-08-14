package com.example.todolist.BottomActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapter.CalendarAdapter;
import com.example.todolist.Adapter.CalendarInterface;
import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityCalendarBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements DatePickerListener {

    ActivityCalendarBinding binding;
    String mail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CalendarAdapter calendarAdapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);

        //Update date -
        date = getTodaysDateWeek();

        if(date != null)
        {
            binding.calendarDateTextId.setText(date);
        }


        recyclerView = binding.calendarRecyclerViewId;
        getMail();

        binding.calendarBackId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Date Picker
        datePickerFun();

        //Calendar Instance
        Calendar cal = Calendar.getInstance();

        //Call Adapter for first time
        callRecyclerAdapter(getQueryString(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));

    }

    void callRecyclerAdapter(String dateForQuery)
    {
//        System.out.println("SIIIzeMail: " + mail);
        Query query = db.collection("Users").document(mail).collection("Notes")
                .whereEqualTo("note_date", dateForQuery)
                .orderBy("note_start_time", Query.Direction.ASCENDING)
                .limit(50);


       query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            int count = task.getResult().getDocuments().size();
                            if(count == 0)
                            {
                                Toast.makeText(CalendarActivity.this, "Tasks Not Found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        //Options
        FirestoreRecyclerOptions<NotesDataClass> options = new FirestoreRecyclerOptions.Builder<NotesDataClass>()
                .setQuery(query, NotesDataClass.class)
                .build();

        //Interface
        CalendarInterface interf = new CalendarInterface() {

            @Override
            public void showDetails(NotesDataClass model) {
                //todo show data
            }
        };

        calendarAdapter = new CalendarAdapter(options, this);

        // Set Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
//        Toast.makeText(getApplicationContext(), dateSelected.toString(), Toast.LENGTH_SHORT).show();
        String q = getQueryString(dateSelected.getDayOfMonth(), dateSelected.getMonthOfYear(), dateSelected.getYear());

        calendarAdapter.stopListening();
        callRecyclerAdapter(q);
        calendarAdapter.startListening();

    }


    private void datePickerFun() {
        binding.datePicker
                .setListener((DatePickerListener) this)
                .setDays(120)
                .setOffset(7)
                .setDateSelectedColor(Color.DKGRAY)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.DKGRAY)
                .setTodayButtonTextColor(getColor(R.color.colorPrimary))
                .setTodayDateTextColor(getColor(R.color.colorPrimary))
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.DKGRAY)
                .setDayOfWeekTextColor(Color.DKGRAY)
                .setUnselectedDayTextColor(getColor(R.color.gray))
                .showTodayButton(false)
                .init();
    }

    private String getQueryString(int todayDay, int todayMonth, int todayYear) {

        String query_date = todayDay + "/" + todayMonth + "/" + todayYear;
        System.out.println("query_date: " + query_date);
        System.out.println("is_equal_to: " + query_date.equals("12/8/2021"));

        return  query_date;
    }

    @Override
    protected void onStop() {
        super.onStop();
        calendarAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        calendarAdapter.startListening();
    }

//    ProgressDialog startProgress()
//    {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setContentView(R.layout.loading_bar);
//        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        return progressDialog;
//    }

    void getMail()
    {
        SharedPreferences pref = this.getSharedPreferences(MyConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        mail = pref.getString("userMail", "");
    }

    private String getTodaysDateWeek() {
        Calendar cal = Calendar.getInstance();

        String[] week = {"Sun", "Mon", "Tue", "WED", "Thur", "Fri", "Sat"};
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String dateFormat = "today, " + cal.get(Calendar.DAY_OF_MONTH) + " " + month[cal.get(Calendar.MONTH)] + ", " + week[cal.get(Calendar.DAY_OF_WEEK)-1];

        return dateFormat;
    }

}