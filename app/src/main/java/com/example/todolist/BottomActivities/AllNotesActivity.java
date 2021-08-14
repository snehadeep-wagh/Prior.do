package com.example.todolist.BottomActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapter.NotesAdapter;
import com.example.todolist.Adapter.selectOne;
import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivityAllNotesBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import static com.example.todolist.BottomActivities.CreateNote.createDocKey;

public class AllNotesActivity extends AppCompatActivity {

    String mail;
    String name;
    ActivityAllNotesBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String queryDateString;
    NotesAdapter allNotesAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onStop() {
        super.onStop();
        allNotesAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        allNotesAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_notes);

        binding.allNotesBackId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        Toast.makeText(getApplicationContext(), "Notification", Toast.LENGTH_SHORT).show();
        System.out.println("Notification_frag: [][][][][]");


        queryDateString = getQueryString().trim();

        //Find view
        recyclerView = binding.allnotesRecyclerViewId;


        // Update the name
        UpdateUi();

        // -----------------todo FirestoreRecyclerAdapter obj ---------------------

        Query query = db.collection(String.format("/Users/%s/Notes", mail))
                .orderBy("note_date", Query.Direction.DESCENDING)
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
                // todo show details activity
            }
        };

        allNotesAdapter = new NotesAdapter(options, this, interf);

        // Set Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(allNotesAdapter);


    }

    private void UpdateUi() {
        SharedPreferences pref = this.getSharedPreferences(MyConstants.MYPREFERENCES, Context.MODE_PRIVATE);
        name = pref.getString("userName", "");
        mail = pref.getString("userMail", "");
        System.out.println("name: "+ name);

        TextView et = binding.allnotesTextId;
        et.setText(name);
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