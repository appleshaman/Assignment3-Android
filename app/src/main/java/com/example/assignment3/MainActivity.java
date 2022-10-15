package com.example.assignment3;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.assignment3.JavaBeanSong;
import com.example.assignment3.ScanLocalMusic;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private boolean logged = true;// for debug
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        requestPermissions(permissions, 200);


        userNameTextView = findViewById(R.id.textViewUser);

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
    }

    public class TileAdapter extends BaseAdapter {
        class ViewHolder {
            int position;
            TextView name;
            TextView artist;
            TextView duration;
        }
        @Override
        public int getCount() {
            return musicInformation.size();
        }// needs implement the

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
                    vh.name.post(()->vh.artist.setText(musicInformation.get(i).artist));
                    vh.name.post(()->vh.duration.setText(musicInformation.get(i).duration));
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
}