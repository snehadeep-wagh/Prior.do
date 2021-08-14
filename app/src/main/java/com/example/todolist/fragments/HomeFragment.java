package com.example.todolist.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapter.NotesAdapter;
import com.example.todolist.Adapter.selectOne;
import com.example.todolist.BottomActivities.DetailActivity;
import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.todolist.BottomActivities.CreateNote.createDocKey;


public class HomeFragment extends Fragment {

    View inflateView;
    String mail;
    String name;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<NotesDataClass> listOfNotes = new ArrayList<NotesDataClass>();
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    String date;
    String queryDateString;
    Bundle bundle_;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflateView =  inflater.inflate(R.layout.fragment_home, container, false);

        queryDateString = getQueryString().trim();

        //bundel
        bundle_ = new Bundle();

        //Find view
        recyclerView = inflateView.findViewById(R.id.recycler_view_id);

        //Get today's date
        date = getTodaysDateWeek();

        if(date != null)
        {
            TextView todaysDate = inflateView.findViewById(R.id.todays_date_id);
            todaysDate.setText(date);
        }


        // Update name----
        UpdateUi();

//        progressBar = inflateView.findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        // -----------------todo FirestoreRecyclerAdapter obj ---------------------

        Query query = db.collection(String.format("/Users/%s/Notes", mail))
                .whereEqualTo("note_date", queryDateString)
                .orderBy("priority", Query.Direction.DESCENDING)
                .limit(50);

        //

        FirestoreRecyclerOptions<NotesDataClass> options = new FirestoreRecyclerOptions.Builder<NotesDataClass>()
                .setQuery(query, NotesDataClass.class)
                .build();

        //Interface
        selectOne interf = new selectOne() {
            @Override
            public void updateRadio(NotesDataClass model) {

                String key = createDocKey(model.note_date, model.note_start_time, model.note_end_time);

                db.collection(String.format("/Users/%s/Notes", mail))
                        .document(key)
                        .update("task_done", true);
            }

            @Override
            public void showDetails(NotesDataClass model) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Notes", model);
               startActivity(intent);
            }
        };

        notesAdapter = new NotesAdapter(options, getActivity(), interf);

        // Set Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notesAdapter);

        return inflateView;
    }

    private void UpdateUi() {
        SharedPreferences pref = this.getActivity().getSharedPreferences(MyConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        name = pref.getString("userName", "");
        mail = pref.getString("userMail", "");
        System.out.println("name: "+ name);

        TextView et = inflateView.findViewById(R.id.home_text_id);
        et.setText(name);
    }

    @Override
    public void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    private String getTodaysDateWeek() {
        Calendar cal = Calendar.getInstance();

        String[] week = {"Sun", "Mon", "Tue", "WED", "Thur", "Fri", "Sat"};
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String dateFormat = "today, " + cal.get(Calendar.DAY_OF_MONTH) + " " + month[cal.get(Calendar.MONTH)] + ", " + week[cal.get(Calendar.DAY_OF_WEEK)-1];

        return dateFormat;
    }

    private String getQueryString() {
        int todayDay, todayMonth, todayYear;

        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);
        todayMonth = cal.get(Calendar.MONTH) + 1;
        todayYear = cal.get(Calendar.YEAR);

        String query_date = todayDay + "/" + todayMonth + "/" + todayYear;
        System.out.println("query_date: " + query_date);
        System.out.println("is_equal_to: " + query_date.equals("12/8/2021"));

        return  query_date;
    }
}