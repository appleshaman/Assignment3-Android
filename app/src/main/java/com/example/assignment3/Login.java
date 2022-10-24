package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.assignment3.Utils.GetHexUtils;
import com.example.assignment3.db.User;
import com.example.assignment3.db.UserDao;
import com.example.assignment3.db.UserDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private EditText editText_Pass;
    private EditText editText_Account;
    UserDao mDao;
    int ifUser;
    String user;
    int correctness;
    String password;
    int count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        UserDatabase db = UserDatabase.getDatabase(this);
        mDao = db.UserDao();
        new Thread(() -> {
            count = mDao.initiate();
            Log.i("login", "init: " + count);
        }).start();



        getWindow().setStatusBarColor(Color.parseColor("#ec4141"));//set Status bar color

        Button btn_login = findViewById(R.id.buttonLogin);
        Button btn_forget = findViewById(R.id.buttonForget);
        editText_Pass = findViewById(R.id.editTextPassword);
        editText_Account = findViewById(R.id.editTextAccount);

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// if user forget the password
                Context context = getApplicationContext();
                CharSequence text = "It's this course's number or 123456";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_Account.getText().toString().equals("")){
                    Context context = getApplicationContext();
                    CharSequence text = "Input your user name";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }
                if(editText_Pass.getText().toString().equals("")){
                    Context context = getApplicationContext();
                    CharSequence text = "Input your password";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }



                user = editText_Account.getText().toString();
                Thread t1 = new Thread(() -> {
                ifUser = mDao.ifExists(user);
                });

                t1.start();

                //wait for query thread to end
                try {// search the user name in database
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if(ifUser == 0){// if the user is not correct
                    Log.i("login", "ifuser-2: " + ifUser);
                    Context context = getApplicationContext();
                    CharSequence text = "User does not exist! ";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    editText_Account.setText("");
                    editText_Pass.setText("");
                }else{
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        //use SHA-256 to hide the password, only match the hash value
                        //which avoided store plain text of password
                        password = GetHexUtils.encode(md.digest(editText_Pass.getText().toString().getBytes()));//password is stored in SHA-256

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    Log.i("login", "password: " + password);


                    //I did not implemented database, so verify the password here
//                if(Objects.equals(password, "b1f8f78e5a676b8ae6d4c12f4785887ca9e583d533e8b973534a5cc44286a36a")){

                    Thread t2 = new Thread(() -> {//search the password in database see if it match the user
                        correctness = mDao.ifMatch(user, password);// manipulate database requires different thread
                    });
                    t2.start();
                    try {
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(correctness == 1){// the password is correct, and login page is closed
                        Context context = getApplicationContext();
                        CharSequence text = "Welcome back!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        //editText_Pass.setText("");//clear the input area

                        setResult(RESULT_OK, getIntent().putExtra("user", editText_Account.getText().toString()));
                        Login.this.finish();
                    }else{// the password is wrong
                        Context context = getApplicationContext();
                        CharSequence text = "Password is wrong";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        editText_Pass.setText("");
                    }
                }





            }
        });
    }
}