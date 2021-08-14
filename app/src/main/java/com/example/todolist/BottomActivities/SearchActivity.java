package com.example.todolist.BottomActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adapter.NotesAdapter;
import com.example.todolist.Adapter.selectOne;
import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.MyConstants;
import com.example.todolist.R;
import com.example.todolist.databinding.ActivitySearchBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

import static com.example.todolist.BottomActivities.CreateNote.createDocKey;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    FirestoreRecyclerAdapter adapter;
    String mail;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestoreRecyclerOptions<NotesDataClass> options;
    selectOne interf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        mail = getMail();

        recyclerView = this.findViewById(R.id.search_recycler_view_id);
        callRecyclerAdapter(query2(mail));
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.setAdapter(adapter);


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.stopListening();
                adapter = new NotesAdapter(options, SearchActivity.this, interf);
                callRecyclerAdapter(query1(query.toLowerCase(), mail));
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                recyclerView.setAdapter(adapter);
                adapter.startListening();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                callRecyclerAdapter(query1(newText.toLowerCase(), mail));
//                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
//                recyclerView.setAdapter(adapter);
                return false;
            }
        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    private void callRecyclerAdapter(Query query) {

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String sze = String.valueOf(queryDocumentSnapshots.getDocuments().size());
                System.out.println("sze: " + sze);
                Toast.makeText(SearchActivity.this, sze, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SearchActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                System.out.println("Failed listner: " + 0);
            }
        });

        options = new FirestoreRecyclerOptions.Builder<NotesDataClass>()
                .setQuery(query, NotesDataClass.class)
                .build();

        //Interface
        interf = new selectOne() {
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

        adapter = new NotesAdapter(options, this, interf);

        // Set Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    public Query query1(String queryText, String mail)
    {
        Query query = FirebaseFirestore.getInstance()
                .collection(String.format("/Users/%s/Notes", mail))
                .whereEqualTo("search", queryText)
                .limit(50)
                ;
        return query;
    }

    public Query query2(String mail)
    {
        Query query = FirebaseFirestore.getInstance()
                .collection(String.format("/Users/%s/Notes", mail))
                .orderBy("note_date", Query.Direction.DESCENDING)
                .limit(50);
        return query;
    }

    public String getMail()
    {
        SharedPreferences pref = getSharedPreferences(MyConstants.MYPREFERENCES, MODE_PRIVATE);
        String mail = pref.getString("userMail", "");
        return mail;
    }

}