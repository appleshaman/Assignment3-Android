package com.example.assignment3.Utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static String EncryptPass(String password){
        String encryptedPass = null;
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            encryptedPass = GetHexUtils.encode(md.digest(password.getBytes()));
            Log.i("db","encrypted:" + encryptedPass);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedPass;
    }
}
