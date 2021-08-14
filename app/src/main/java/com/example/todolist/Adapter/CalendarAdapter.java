package com.example.todolist.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CalendarAdapter extends FirestoreRecyclerAdapter<NotesDataClass, CalendarAdapter.CalendarViewHolder> {

    Context context;

    public CalendarAdapter(@NonNull FirestoreRecyclerOptions<NotesDataClass> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CalendarViewHolder holder, int position, @NonNull NotesDataClass model) {
        holder.task_name.setText(model.task_name);
        holder.startTime.setText(model.note_start_time);
        holder.endTime.setText(model.note_end_time);
        holder.date.setText(model.note_date);

    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item_view, parent, false);

        return new CalendarViewHolder(view);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        TextView startTime;
        TextView endTime;
        TextView date;
        TextView task_name;
        RelativeLayout relativeLayout;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            startTime = itemView.findViewById(R.id.calendar_start_time_id);
            endTime = itemView.findViewById(R.id.calendar_end_time_id);
            date = itemView.findViewById(R.id.calendar_date_id);
            task_name = itemView.findViewById(R.id.calendar_task_name_id);
            relativeLayout = itemView.findViewById(R.id.relative_id);
        }
    }
}

