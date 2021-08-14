package com.example.todolist.DataClasses;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class NotesDataClass implements Serializable {
    public String task_name;
    public String task_description;
    public int priority;
    public Boolean remind_me;
    public String note_start_time;
    public String note_end_time;
    public String note_date;
    public String category;
    public String search;
    public Boolean task_done;

    public NotesDataClass()
    {
        task_name = "";
        task_description = "";
        priority = 0; // 1 - low, 2 - medium, 3 - high
        remind_me = false;
        note_date = "";
        category = "";
        note_start_time = "";
        note_end_time = "";
        task_done = false;
    }

    public String getTask_name() {
        return task_name;
    }

    public String getTask_description() {
        return task_description;
    }

    public int getPriority() {
        return priority;
    }

    public Boolean getRemind_me() {
        return remind_me;
    }

    public String getNote_start_time() {
        return note_start_time;
    }


    public String getSearch() {
        return search;
    }

    public String getNote_end_time() {
        return note_end_time;
    }

    public String getNote_date() {
        return note_date;
    }

    public Boolean getTask_done() {
        return task_done;
    }

    public String getCategory() {
        return category;
    }

    public NotesDataClass(String task_name, String task_description, int priority, Boolean remind_me, String note_start_time, String note_end_time, String note_date, String category) {

        this.task_name = task_name;
        this.task_description = task_description;
        this.priority = priority;
        this.remind_me = remind_me;
        this.note_start_time = note_start_time;
        this.note_end_time = note_end_time;
        this.note_date = note_date;
        this.category = category;
        this.task_done = false;
        this.search = task_name.toLowerCase();


    }
}
