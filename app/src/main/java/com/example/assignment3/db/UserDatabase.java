package com.example.assignment3.db;


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

@Database(entities = User.class, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao UserDao();

    private static final String pass = "159336";
    private static String encryptedPass = null;

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
                Log.i("db", "Executor()");
                UserDao dao = INSTANCE.UserDao();
                EncryptPass();
                User martin = new User("martin", encryptedPass);
                dao.insert(martin);
                dao.insert(new User("Not Martin", "123456"));
                Log.i("db","Database Populated");


            });
        }
    };

    private static void EncryptPass(){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            encryptedPass = GetHexUtils.encode(md.digest(pass.getBytes()));
            Log.i("db","encrypted:" + encryptedPass);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }





}
