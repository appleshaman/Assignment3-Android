package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SinglePage extends AppCompatActivity {

    MusicService.controlMusic controlMusic;//playing music
    MusicService musicService;
    MyServiceConn myServiceConn;
    private ArrayList<JavaBeanSong> musicInformation = new ArrayList<JavaBeanSong>();

    private boolean isUnbind = false;

    private Intent intent;

    private ImageButton last;
    private ImageButton pause;
    private ImageButton next;
    private ImageButton loopOrNot;
    private ImageButton back;
    private TextView artistName;
    private TextView songName;
    private Context context;
    private TextView songAddress;
    private ImageView coverPicture;
    public GetSongCover getSongCover = new GetSongCover();
    private int selectedSong;

    private void init(){

        last = findViewById(R.id.lastForSingle);
        pause = findViewById(R.id.pauseForSingle);
        next = findViewById(R.id.nextForSingle);
        loopOrNot = findViewById(R.id.imageButtonLoop);
        back = findViewById(R.id.imageButtonBack);
        artistName  = findViewById(R.id.artistForSingle);
        songName = findViewById(R.id.nameForSingle);
        coverPicture = findViewById(R.id.coverForSingle);

        songName.setText(musicInformation.get(selectedSong).name);
        artistName.setText(musicInformation.get(selectedSong).artist);

        coverPicture.setImageBitmap(getSongCover.getCoverPicture(musicInformation.get(selectedSong).path, true));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_page);

        Intent receiveIntent = getIntent();
        musicInformation = (ArrayList<JavaBeanSong>) receiveIntent.getSerializableExtra("musicInformation");
        selectedSong = receiveIntent.getIntExtra("selectedSong", -1);

        myServiceConn = new MyServiceConn();
        intent = new Intent(this, MusicService.class);
        bindService(intent, myServiceConn,BIND_AUTO_CREATE);
        startService(intent);
        init();

        if(!receiveIntent.getBooleanExtra("isPlay", false)){
            ImageButton imageButton = findViewById(R.id.pausedForSingle);
            pause.setImageDrawable(imageButton.getDrawable());//change button icon
        }else{
            ImageButton imageButton = findViewById(R.id.startedForSingle);
            pause.setImageDrawable(imageButton.getDrawable());//change button icon
        }

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedSong --;
                if(selectedSong == -1){// if over range
                    selectedSong = musicInformation.size() - 1;
                }
                //songAddress.setText(musicInformation.get(selectedSong).path);
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);

                coverPicture.setImageBitmap(getSongCover.getCoverPicture(musicInformation.get(selectedSong).path, true));//set cover

                controlMusic.play(musicInformation.get(selectedSong).path);

                ImageButton imageButton = findViewById(R.id.startedForSingle);
                pause.setImageDrawable(imageButton.getDrawable());//change button icon
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(controlMusic.isPlaying()){
                    ImageButton imageButton = findViewById(R.id.pausedForSingle);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    controlMusic.pauseMusic();
                }else{
                    ImageButton imageButton = findViewById(R.id.startedForSingle);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    controlMusic.continueMusic();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedSong ++;
                if(selectedSong == musicInformation.size()){// if over range
                    selectedSong = 0;
                }
                //songAddress.setText(musicInformation.get(selectedSong).path);
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);

                coverPicture.setImageBitmap(getSongCover.getCoverPicture(musicInformation.get(selectedSong).path, true));//set cover

                controlMusic.play(musicInformation.get(selectedSong).path);

                ImageButton imageButton = findViewById(R.id.startedForSingle);
                pause.setImageDrawable(imageButton.getDrawable());//change button icon
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, getIntent().putExtra("selectedSong", Integer.toString(selectedSong)));
                finish();
            }
        });
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