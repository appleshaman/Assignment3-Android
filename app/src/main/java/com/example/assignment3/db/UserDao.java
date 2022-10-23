package com.example.assignment3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT EXISTS(SELECT username FROM user WHERE username = :user)")
    int ifExists(String user);

    @Query("SELECT user.password FROM user WHERE username = :user AND password = :pass")
    int ifMatch(String user, String pass);

}
