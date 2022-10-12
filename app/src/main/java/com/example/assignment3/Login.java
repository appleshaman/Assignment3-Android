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
                toHex hex = new toHex();
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    //use SHA-256 to hide the password, only match the hash value
                    //which avoided store plain text of password
                    temp = hex.encode(md.digest(editText_Pass.getText().toString().getBytes()));

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
                    editText_Pass.setText("");
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

        static class toHex {// a function used to make hex decimal
        private final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        public String encode(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            StringBuilder stringBuffer = new StringBuilder(bytes.length * 2);

            for (byte aByte : bytes) {
                int high = (aByte & 0xf0) >> 4;
                int low = aByte & 0x0f;
                stringBuffer.append(HEX_CHAR[high]).append(HEX_CHAR[low]);
            }
            return stringBuffer.toString();
        }
        public byte[] decode(String hex) {
            if (hex == null || hex.length() == 0) {
                return null;
            }
            int len = hex.length() / 2;
            byte[] result = new byte[len];
            String highString = null;
            String lowString = null;
            int high = 0;
            int low = 0;
            for (int i = 0; i < len; i++) {
                highString = hex.substring(i * 2, i * 2 + 1);
                high = Integer.parseInt(highString, 16);
                lowString = hex.substring(i * 2 + 1, i * 2 + 2);
                low = Integer.parseInt(lowString, 16);
                result[i] = (byte) ((high << 4) + low);
            }
            return result;
        }
    }
}