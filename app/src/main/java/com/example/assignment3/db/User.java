package com.example.assignment3.db;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String username;
    public String password;
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

}
