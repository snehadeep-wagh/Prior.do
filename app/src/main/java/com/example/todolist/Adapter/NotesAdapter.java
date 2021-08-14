package com.example.todolist.Adapter;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DataClasses.NotesDataClass;
import com.example.todolist.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Calendar;

public class NotesAdapter extends FirestoreRecyclerAdapter<NotesDataClass, NotesAdapter.NotesViewHolder> {

    String prio = "";
    int col, catCol;
    selectOne intefce;
    Context context;
    Boolean showAllData;
    public NotesAdapter(@NonNull FirestoreRecyclerOptions<NotesDataClass> options, Context mcontext, selectOne interf) {
        super(options);
        context = mcontext;
        intefce = interf;

    }

    static class NotesViewHolder extends RecyclerView.ViewHolder
    {
        TextView priority, category, taskName, time, date;
        RadioButton radioButton;
        RelativeLayout relativeLayout;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            priority = itemView.findViewById(R.id.recycler_priority_id);
            category = itemView.findViewById(R.id.recycler_category_id);
            taskName = itemView.findViewById(R.id.recycler_taskname_id);
            time = itemView.findViewById(R.id.recycler_time_id);
            radioButton = itemView.findViewById(R.id.recycler_radio_button_id);
            date = itemView.findViewById(R.id.recycler_date_id);
            relativeLayout = itemView.findViewById(R.id.item_view_root_id);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull NotesDataClass model) {

        changeColor(holder, model);

        if(model.task_done)
        {
            holder.radioButton.setChecked(true);
        }

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intefce.updateRadio(model);
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intefce.showDetails(model);
            }
        });

        if(model.remind_me)
        {
            setAlarm( context,model.note_date, model.note_start_time);
        }
        else
        {
            cancelAlarm(context);
        }

        holder.priority.setText(prio);
        holder.category.setText(model.category);
        holder.time.setText(model.note_start_time);
        holder.date.setText(model.note_date);
        holder.taskName.setText(model.task_name);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view, parent, false);

        return new NotesViewHolder(view);
    }

    private void changeColor(NotesViewHolder holder, NotesDataClass model)
    {

        if(model.priority == 1)
        {
            prio = "Low";
            col = R.color.low_prio;

        }
        else if(model.priority == 2)
        {
            prio = "Medium";
            col = R.color.medium_prio;
        }
        else
        {
            prio = "High";
            col = R.color.high_prio;
        }

        if(model.category.equals("Family"))
        {
            catCol = R.color.family_cat;
        }
        else if(model.category.equals("School"))
        {
            catCol = R.color.school_cat;
        }
        else
        {
            catCol = R.color.work_cat;
        }


        //change background color
        LayerDrawable prioDrawable = (LayerDrawable)holder.priority.getBackground();
        LayerDrawable cateDrawable = (LayerDrawable)holder.category.getBackground();

        GradientDrawable prioShape = (GradientDrawable)prioDrawable.findDrawableByLayerId (R.id.priority_background_id);
        prioShape.setColor(ContextCompat.getColor(context, col));

        GradientDrawable catShape = (GradientDrawable)cateDrawable.findDrawableByLayerId (R.id.priority_background_id);
        catShape.setColor(ContextCompat.getColor(context, catCol));

    }

    public static void setAlarm(Context context, String date, String time)
    {
        Calendar cal = getTimeAndDate(time, date);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() ,AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }


    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

    }

    public static void createNotificationChannel(Context context)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "TODOListReminderChannel";
            String description = "Channel For Todo list";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TODOChannelId", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    public static Calendar getTimeAndDate(String time_, String date_)
    {
        Calendar calendar = Calendar.getInstance();

        int date, month, year;
        String s[] = (date_.trim()).split("/");
        date = Integer.parseInt(s[0]);
        month = Integer.parseInt(s[1]);
        year = Integer.parseInt(s[2]);

        int hour, min;
        String t[] = (time_.trim()).split(":");
        hour = Integer.parseInt(t[0]);
        min = Integer.parseInt(t[1]);

        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        System.out.println("GETSTRING" + date + month + year + hour + min);

        return calendar;
    }

}


