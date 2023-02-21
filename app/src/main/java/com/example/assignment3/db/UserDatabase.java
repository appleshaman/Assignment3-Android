package com.example.assignment3.db;


import static com.example.assignment3.Utils.Encryption.EncryptPass;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.assignment3.Utils.GetHexUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao UserDao();
    //due to time limit, we did not make a register function, so we can only store the account and it's password here.
    //but in a ideal app, we only store them in database
    private static final String username1 = "admin";// first account
    private static final String password1 = "159336";
    private static final String username2 = "default";// second account
    private static final String password2 = "123456";

    private static volatile UserDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "user_database")
                            .addCallback(sUserDatabaseCallback)
                            .build();
                }
            }
        }

        Log.i("db", String.valueOf(INSTANCE));
        return INSTANCE;
    }

    private static RoomDatabase.Callback sUserDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                UserDao dao = INSTANCE.UserDao();
                dao.deleteAllUser();
                User martin = new User(username1, EncryptPass(password1));
                Log.i("db","username:" + username1);
                dao.insert(martin);
                User notMartin = new User(username2, EncryptPass(password2));
                dao.insert(notMartin);
                Log.i("db","Database Populated");


            });

        }
    };



}
