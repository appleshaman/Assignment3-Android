package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.assignment3.Utils.GetHexUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private EditText editText_Pass;
    private EditText editText_Account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_login = findViewById(R.id.buttonLogin);
        Button btn_forget = findViewById(R.id.buttonForget);
        editText_Pass = findViewById(R.id.editTextPassword);
        editText_Account = findViewById(R.id.editTextAccount);

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                CharSequence text = "It is this course's number";
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
                String  temp = null;
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    //use SHA-256 to hide the password, only match the hash value
                    //which avoided store plain text of password
                    temp = GetHexUtils.encode(md.digest(editText_Pass.getText().toString().getBytes()));

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //I did not implemented database, so verify the password here
                if(Objects.equals(temp, "b1f8f78e5a676b8ae6d4c12f4785887ca9e583d533e8b973534a5cc44286a36a")){
                    Context context = getApplicationContext();
                    CharSequence text = "Welcome back!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    editText_Pass.setText("");//clear the input area

                    setResult(RESULT_OK, getIntent().putExtra("user", editText_Account.getText().toString()));
                    Login.this.finish();
                }else{
                    Context context = getApplicationContext();
                    CharSequence text = "Password is wrong";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    editText_Pass.setText("");
                }
            }
        });
    }
}