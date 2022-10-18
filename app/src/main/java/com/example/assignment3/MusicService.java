package com.example.assignment3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player;
    private Timer timer;

    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new controlMusic();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
    }

    public void setTimer(){
        if(timer == null){
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (player == null) return;
                    int duration = player.getDuration();
                    int currentPosition = player.getCurrentPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("totalDuration",duration);
                    bundle.putInt("currentDuration",currentPosition);
                    Intent intent = new Intent();
                    intent.putExtra("musicDuration", bundle);
                    intent.setAction("localBroadcast");
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManager.sendBroadcast(intent);

                }
            };
            timer.schedule(timerTask,10, 500);
        }

    }
    class controlMusic extends Binder {
        public void play(String path){
            Uri uri = Uri.parse(path);
            try{
                player.reset();
                player = MediaPlayer.create(getApplicationContext(), uri);
                player.start();
                setTimer();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public MusicService getService() {
            return MusicService.this;
        }
        public void pauseMusic(){
            player.pause();
        }
        public void continueMusic(){
            player.start();
        }
        public void seekTo(int progress){
            player.seekTo(progress);
        }
        public boolean isPlaying(){
            return player.isPlaying();
        }



    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player != null) {
            if (player.isPlaying()) player.stop();
            player.release();
            player = null;
        }
    }
}