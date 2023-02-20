package com.example.assignment3;

import static com.example.assignment3.Utils.Encryption.EncryptPass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment3.db.User;
import com.example.assignment3.db.UserDao;
import com.example.assignment3.db.UserDatabase;
import com.example.assignment3.db.UserDb;

import java.util.concurrent.atomic.AtomicInteger;

public class Register extends AppCompatActivity {


    private UserDao mDao;
    private UserDatabase db = UserDb.getDb();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(Color.parseColor("#ec4141"));//set Status bar color

        mDao = db.UserDao();




        Button button_back = findViewById(R.id.buttonBack);
        Button button_register = findViewById(R.id.buttonRegister);
        EditText editTextName = findViewById(R.id.editTextTextRegisterName);
        EditText editTextPassword = findViewById(R.id.editTextTextRegisterPassword);
        EditText editTextPasswordAgain = findViewById(R.id.editTextTextRepeatPassword);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register.this.finish();
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                AtomicInteger exist = new AtomicInteger();
                new Thread(() -> {
                exist.set(mDao.ifExists(editTextName.getText().toString()));
                }).start();
                if(editTextName.getText().toString().equals("")){
                    CharSequence text = "User name could not be empty!";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else if(editTextPassword.getText().toString().equals("")){
                    CharSequence text = "Password could not be empty!";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else if(!editTextPassword.getText().toString().equals(editTextPasswordAgain.getText().toString())){
                    CharSequence text = "Two password inputs do not match!";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else if(exist.get() != 0){
                    CharSequence text = "This user is already exist!";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    new Thread(() -> {
                        mDao.insert(new User(editTextName.getText().toString(), EncryptPass(editTextPassword.getText().toString())));
                    }).start();

                        CharSequence text = "User created!";
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
            }
        });
    }
}