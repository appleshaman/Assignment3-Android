package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class SinglePage extends AppCompatActivity {

    MusicService.controlMusic controlMusic;//playing music
    MusicService musicService;
    MyServiceConn myServiceConn;

    private boolean isUnbind = false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_page);

        myServiceConn = new MyServiceConn();
        intent = new Intent(this, MusicService.class);
        bindService(intent, myServiceConn,BIND_AUTO_CREATE);
        startService(intent);

    }

    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder){
            controlMusic = (MusicService.controlMusic)iBinder;
            musicService = controlMusic.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }

}