package com.example.todolist.DataClasses;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.example.todolist.MainActivity;

import java.io.Serializable;

@Keep
public class ProfileDataClass implements Serializable {
    public  String name;
    public  String mail;

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public ProfileDataClass(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }
}
