package com.example.assignment3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);
    @Delete
    void deleteUser(User user);
    @Query("DELETE FROM USER")
    void deleteAllUser();
    @Query("SELECT count(*) from user")
    int initiate();
    @Query("SELECT EXISTS(SELECT username FROM user WHERE username = :user)")
    int ifExists(String user);
    @Query("SELECT EXISTS(SELECT user.password FROM user WHERE username = :user AND password = :pass)")
    int ifMatch(String user, String pass);




}
