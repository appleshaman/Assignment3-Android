package com.example.assignment3;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView userNameTextView;
    private String userName = null;
    private final ExecutorService e1 = Executors.newSingleThreadScheduledExecutor();
    private ArrayList<JavaBeanSong> musicInformation = new ArrayList<JavaBeanSong>();
    private ListView songList;
    private int selectedSong = -1;

    private ImageView imageViewBottom;
    private ImageButton last;
    private ImageButton pause;
    private ImageButton next;
    private TextView artistName;
    private TextView songName;
    private Context context;
    private TextView songAddress;
    private boolean logged = true;// for debug

    GetSongCover getSongCover = new GetSongCover();

    MusicService.controlMusic musicService;//playing music
    private MyServiceConn myServiceConn;
    private boolean isUnbind = false;
    private Intent intent;


    private void init(){
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        requestPermissions(permissions, 200);

        userNameTextView = findViewById(R.id.textViewUser);
        imageViewBottom = findViewById(R.id.imageViewBottom);
        last = findViewById(R.id.last);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        songName = findViewById(R.id.name);
        artistName = findViewById(R.id.artist);
        songAddress = findViewById(R.id.SongAddrForButtom);
        intent = new Intent(this, MusicService.class);
        myServiceConn = new MyServiceConn();
        bindService(intent, myServiceConn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        init();

        ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ResultContract(),
                new ActivityResultCallback<String>() {
                    @Override
                    public void onActivityResult(String result) {
                        userNameTextView.setText(result);
                    }
                }
        );
        if(savedInstanceState != null) {
            userName = savedInstanceState.getString("userName",null);
        }
        if(userName == null){// means have not log in, jump to the login page
            //logged = false;
        }
        if(!logged){
            activityResultLauncher.launch(true);
            logged = true;
        }

        ScanLocalMusic scanLocalMusic = new ScanLocalMusic();
        musicInformation = scanLocalMusic.getMusicData(this);

        TileAdapter tileAdapter = new TileAdapter();
        songList = (ListView)findViewById(R.id.musicList);
        songList.setAdapter(tileAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSong = i;
                if(musicInformation.get(i).path != songAddress.getText()){// not the same song
                    songAddress.setText(musicInformation.get(i).path);
                    songName.setText(musicInformation.get(i).name);
                    artistName.setText(musicInformation.get(i).artist);
                    imageViewBottom.setImageBitmap(getSongCover.getCoverPicture(context,musicInformation.get(selectedSong).path));//set cover
                    musicService.play(songAddress.getText().toString());

                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                }else {//if clicked the same song again
                    if(musicService.isPlaying()){
                        ImageButton imageButton = findViewById(R.id.paused);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        musicService.pauseMusic();
                    }else{
                        ImageButton imageButton = findViewById(R.id.started);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        musicService.continueMusic();
                    }
                }
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedSong --;
                if(selectedSong == -1){// if over range
                    selectedSong = musicInformation.size() - 1;
                }
                songAddress.setText(musicInformation.get(selectedSong).path);
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);

                ImageView imageView = view.findViewById(R.id.imageViewCover);
                imageViewBottom.setImageBitmap(getSongCover.getCoverPicture(context,musicInformation.get(selectedSong).path));//set cover

                musicService.play(songAddress.getText().toString());

                ImageButton imageButton = findViewById(R.id.started);
                pause.setImageDrawable(imageButton.getDrawable());//change button icon
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicService.isPlaying()){
                    ImageButton imageButton = findViewById(R.id.paused);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    musicService.pauseMusic();
                }else{
                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    musicService.continueMusic();
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
                songAddress.setText(musicInformation.get(selectedSong).path);
                songName.setText(musicInformation.get(selectedSong).name);
                artistName.setText(musicInformation.get(selectedSong).artist);

                imageViewBottom.setImageBitmap(getSongCover.getCoverPicture(context,musicInformation.get(selectedSong).path));//set cover

                musicService.play(songAddress.getText().toString());

                ImageButton imageButton = findViewById(R.id.started);
                pause.setImageDrawable(imageButton.getDrawable());//change button icon
            }
        });


    }

    public class TileAdapter extends BaseAdapter {
        class ViewHolder {
            int position;
            TextView name;
            TextView artist;
            TextView duration;
            ImageView cover;
        }
        @Override
        public int getCount() {
            return musicInformation.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.music_information, viewGroup, false);
                vh = new ViewHolder();
                vh.name = view.findViewById(R.id.name);
                vh.artist = view.findViewById(R.id.artist);
                vh.duration = view.findViewById(R.id.duration);
                vh.cover = view.findViewById(R.id.imageViewCover);
                view.setTag(vh);
            }else{
                vh = (ViewHolder) view.getTag();
            }
            vh.position = i;

            e1.submit(()->{
                if(vh.position != i){
                    return;
                }

                if(vh.position == i){
                    vh.name.post(()->vh.name.setText(musicInformation.get(i).name));
                    vh.artist.post(()->vh.artist.setText(musicInformation.get(i).artist));
                    vh.duration.post(()->vh.duration.setText(getFormattedTime(musicInformation.get(i).duration)));
                    vh.cover.post(()->vh.cover.setImageBitmap(getSongCover.getCoverPicture(context,musicInformation.get(i).path)));
                }
            });
            return view;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("userName", userName);
        super.onSaveInstanceState(savedInstanceState);
    }

    private class ResultContract extends ActivityResultContract<Boolean, String> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            return new Intent(MainActivity.this, Login.class);
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            return intent.getStringExtra("user");
        }
    }

    public static String getFormattedTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicService = (MusicService.controlMusic) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }

    private void unbind(boolean isUnbind){
        if(!isUnbind){
            musicService.pauseMusic();
            unbindService(myServiceConn);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbind(isUnbind);
    }

}