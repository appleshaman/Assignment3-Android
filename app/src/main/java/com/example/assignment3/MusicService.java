package com.example.assignment3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Timer;

public class MusicService extends Service {
    private MediaPlayer player;

    public MusicService() {}

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

    class controlMusic extends Binder {
        public void play(String path){
            Uri uri = Uri.parse(path);
            try{
                player.reset();
                player = MediaPlayer.create(getApplicationContext(), uri);
                player.start();

                //addTimer();
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