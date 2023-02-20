package com.example.assignment3.db;
import android.content.Context;
public class UserDb {
    private static UserDatabase db;

    public static void setDb(UserDatabase db) {
        if(UserDb.db == null){
            UserDb.db = db;
        }

    }

    public static UserDatabase getDb() {
        return db;
    }
}
