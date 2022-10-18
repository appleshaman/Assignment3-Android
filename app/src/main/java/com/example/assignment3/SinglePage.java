package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class SinglePage extends AppCompatActivity {

    MusicService.controlMusic controlMusic;//playing music
    MusicService musicService;
    MyServiceConn myServiceConn;
    private ArrayList<JavaBeanSong> musicInformation = new ArrayList<JavaBeanSong>();

    private Intent intent;

    private ImageButton last;
    private ImageButton pause;
    private ImageButton next;
    private ImageButton loopOrNotButton;
    private ImageButton back;
    private TextView artistName;
    private TextView songName;
    private TextView currentDuration;
    private TextView totalDuration;
    private SeekBar seekBar;

    private ImageView coverPicture;
    private GetSongCover getSongCover = new GetSongCover();
    private FormatTheTime formatTheTime = new FormatTheTime();

    private LocalBroadcastManager localBroadcastManager;
    private Receiver receiver;
    private IntentFilter intentFilter;
    private boolean loopOrNot = false;

    private int selectedSong;

    private void init(){
        last = findViewById(R.id.lastForSingle);
        pause = findViewById(R.id.pauseForSingle);
        next = findViewById(R.id.nextForSingle);
        loopOrNotButton = findViewById(R.id.imageButtonLoop);
        back = findViewById(R.id.imageButtonBack);
        artistName  = findViewById(R.id.artistForSingle);
        songName = findViewById(R.id.nameForSingle);
        coverPicture = findViewById(R.id.coverForSingle);
        totalDuration = findViewById(R.id.totalDuration);
        currentDuration = findViewById(R.id.currentDuration);
        seekBar = findViewById(R.id.seekBar);

        songName.setText(musicInformation.get(selectedSong).name);
        artistName.setText(musicInformation.get(selectedSong).artist);
        totalDuration.setText(FormatTheTime.getFormattedTime(musicInformation.get(selectedSong).duration));
        coverPicture.setImageBitmap(getSongCover.getCoverPicture(musicInformation.get(selectedSong).path, true));
    }



    public class Receiver extends BroadcastReceiver{// receive the duration time
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("musicDuration");
            int total = bundle.getInt("totalDuration");
            int current = bundle.getInt("currentDuration");

            seekBar.setMax(total);
            seekBar.setProgress(current);
            totalDuration.setText(FormatTheTime.getFormattedTime(total));
            currentDuration.setText(FormatTheTime.getFormattedTime(current));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_page);

        Intent receiveIntent = getIntent();
        musicInformation = (ArrayList<JavaBeanSong>) receiveIntent.getSerializableExtra("musicInformation");
        selectedSong = receiveIntent.getIntExtra("selectedSong", -1);
        loopOrNot = receiveIntent.getBooleanExtra("loop",false);

        myServiceConn = new MyServiceConn();
        intent = new Intent(this, MusicService.class);
        bindService(intent, myServiceConn,BIND_AUTO_CREATE);
        startService(intent);

        init();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter("progress");
        receiver = new Receiver();

        localBroadcastManager.registerReceiver(receiver, intentFilter);

        if(!receiveIntent.getBooleanExtra("isPlay", false)){// see if the music is playing
            ImageButton imageButton = findViewById(R.id.pausedForSingle);
            pause.setImageDrawable(imageButton.getDrawable());//change button icon
        }else{
            ImageButton imageButton = findViewById(R.id.startedForSingle);
            pause.setImageDrawable(imageButton.getDrawable());//change button icon
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == seekBar.getMax()||(Objects.equals(currentDuration.getText().toString(), totalDuration.getText().toString()))){
                    Intent intent = new Intent();
                    intent.putExtra("musicFinished", true);
                    intent.setAction("finish");
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManager.sendBroadcast(intent);
                    if (loopOrNot == false) {
                        controlMusic.pauseMusic();
                        ImageButton imageButton = findViewById(R.id.pausedForSingle);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                controlMusic.seekTo(seekBar.getProgress());
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedSong --;
                if(selectedSong == -1){// if over range
                    selectedSong = musicInformation.size() - 1;
                }
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);
                totalDuration.setText(FormatTheTime.getFormattedTime(musicInformation.get(selectedSong).duration));
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
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);
                totalDuration.setText(FormatTheTime.getFormattedTime(musicInformation.get(selectedSong).duration));
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
                Intent intent = new Intent();
                intent.putExtra("musicFinished", !controlMusic.isPlaying());
                intent.setAction("finish");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                localBroadcastManager.sendBroadcast(intent);
                finish();
            }
        });

        loopOrNotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loopOrNot = !loopOrNot;
                controlMusic.setLooping(loopOrNot);
                Context context = getApplicationContext();
                CharSequence text;
                if(loopOrNot){
                    text = "loop mode on!";
                }else {
                    text = "loop mode off!";
                }
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent intent = new Intent();
                intent.putExtra("loopOrNot", loopOrNot);
                intent.setAction("loop");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                localBroadcastManager.sendBroadcast(intent);
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