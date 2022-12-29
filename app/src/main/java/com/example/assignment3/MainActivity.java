package com.example.assignment3;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.graphics.Color;
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
import android.widget.Toast;

import com.example.assignment3.Utils.FormatTheTimeUtils;
import com.example.assignment3.Utils.GetSongCoverUtils;
import com.example.assignment3.Utils.ScanLocalMusicUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
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
    private TextView songAddress;

    private boolean loopOrNot = false;// if loop to play this song
    private boolean logged = true;
    private boolean skipLogin = true;// for debug

    MusicService.controlMusic controlMusic;//playing music
    MusicService musicService;
    private MyServiceConn myServiceConn;
    private boolean isUnbind = false;
    private Intent intent;

    LocalBroadcastManager localBroadcastManagerForFinish;
    LocalBroadcastManager localBroadcastManagerForLoop;
    IntentFilter intentFilterForFinish;
    IntentFilter intentFilterForLoop;
    ReceiverForFinish receiverForFinish;
    ReceiverForLoop receiverForLoop;

    private void init() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        requestPermissions(permissions, 200);
        getWindow().setStatusBarColor(Color.parseColor("#ec4141"));//set Status bar color

        userNameTextView = findViewById(R.id.textViewUser);
        imageViewBottom = findViewById(R.id.imageViewBottom);
        last = findViewById(R.id.lastForSingle);
        pause = findViewById(R.id.pauseForSingle);
        next = findViewById(R.id.nextForSingle);
        songName = findViewById(R.id.nameForSingle);
        artistName = findViewById(R.id.artistForSingle);
        songAddress = findViewById(R.id.SongAddrForButtom);


        intent = new Intent(this, MusicService.class);
        myServiceConn = new MyServiceConn();
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        localBroadcastManagerForFinish = LocalBroadcastManager.getInstance(this);
        intentFilterForFinish = new IntentFilter("finish");
        receiverForFinish = new ReceiverForFinish();

        localBroadcastManagerForFinish.registerReceiver(receiverForFinish, intentFilterForFinish);

        localBroadcastManagerForLoop = LocalBroadcastManager.getInstance(this);
        intentFilterForLoop = new IntentFilter("loop");
        receiverForLoop = new ReceiverForLoop();

        localBroadcastManagerForLoop.registerReceiver(receiverForLoop, intentFilterForLoop);

        ActivityResultLauncher activityResultLauncherForLogin = registerForActivityResult(new ResultContractForLogin(),
                new ActivityResultCallback<String>() {
                    @Override
                    public void onActivityResult(String result) {
                        userNameTextView.setText(result);
                    }
                }
        );
        ActivityResultLauncher activityResultLauncherForSingle = registerForActivityResult(new ResultContractForSinglePage(),
                new ActivityResultCallback<String>() {
                    @Override
                    public void onActivityResult(String result) {
                        if (!Objects.equals(musicInformation.get(selectedSong).path, musicInformation.get(Integer.parseInt(result)).path)) {
                            selectedSong = Integer.parseInt(result);
                            songAddress.setText(musicInformation.get(selectedSong).path);
                            songName.setText(musicInformation.get(selectedSong).name);
                            artistName.setText(musicInformation.get(selectedSong).artist);
                            imageViewBottom.setImageBitmap(GetSongCoverUtils.getCoverPicture(musicInformation.get(selectedSong).path, false));//set cover


                        }
                    }
                }
        );

        if (savedInstanceState != null) {
            userName = savedInstanceState.getString("userName", null);
        }
        if (userName == null) {// means if have not logged in, jump to the login page
            logged = false;// comment out this to skip the login process for debug
        }
        if(!skipLogin) {
            if (!logged) {
                activityResultLauncherForLogin.launch(true);
                logged = true;
            }
        }
        musicInformation = ScanLocalMusicUtils.getMusicData(this);

        TileAdapter tileAdapter = new TileAdapter();
        songList = (ListView) findViewById(R.id.musicList);
        songList.setAdapter(tileAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSong = i;
                if (musicInformation.get(i).path != songAddress.getText()) {// not the same song
                    songAddress.setText(musicInformation.get(i).path);
                    songName.setText(musicInformation.get(i).name);
                    artistName.setText(musicInformation.get(i).artist);
                    imageViewBottom.setImageBitmap(GetSongCoverUtils.getCoverPicture(musicInformation.get(selectedSong).path, false));//set cover
                    controlMusic.play(songAddress.getText().toString());

                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                } else {//if clicked the same song again
                    if (controlMusic.isPlaying()) {
                        ImageButton imageButton = findViewById(R.id.paused);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        controlMusic.pauseMusic();
                    } else {
                        ImageButton imageButton = findViewById(R.id.started);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        controlMusic.continueMusic();
                    }
                }
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSong == -1) {// means no song selected
                    Context context = getApplicationContext();
                    CharSequence text = "No song is selected";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    selectedSong--;
                    if (selectedSong == -1) {// if over range, such as if last song is in index 0
                        selectedSong = musicInformation.size() - 1;
                    }
                    songAddress.setText(musicInformation.get(selectedSong).path);
                    songName.setText(musicInformation.get(selectedSong).name);
                    artistName.setText(musicInformation.get(selectedSong).artist);

                    imageViewBottom.setImageBitmap(GetSongCoverUtils.getCoverPicture(musicInformation.get(selectedSong).path, false));//set cover

                    controlMusic.play(songAddress.getText().toString());

                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSong == -1) {// means no song selected
                    Context context = getApplicationContext();
                    CharSequence text = "No song is selected";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    if (controlMusic.isPlaying()) {
                        ImageButton imageButton = findViewById(R.id.paused);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        controlMusic.pauseMusic();
                    } else {
                        ImageButton imageButton = findViewById(R.id.started);
                        pause.setImageDrawable(imageButton.getDrawable());//change button icon
                        controlMusic.continueMusic();
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSong == -1) {// means no song selected
                    Context context = getApplicationContext();
                    CharSequence text = "No song is selected";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    selectedSong++;
                    if (selectedSong == musicInformation.size()) {// if over range, if the song is the last one, then jump to the first one
                        selectedSong = 0;
                    }
                    songAddress.setText(musicInformation.get(selectedSong).path);
                    songName.setText(musicInformation.get(selectedSong).name);
                    artistName.setText(musicInformation.get(selectedSong).artist);

                    imageViewBottom.setImageBitmap(GetSongCoverUtils.getCoverPicture(musicInformation.get(selectedSong).path, false));//set cover

                    controlMusic.play(songAddress.getText().toString());

                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                }
            }
        });

        imageViewBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSong == -1) {// means no song selected
                    Context context = getApplicationContext();
                    CharSequence text = "No song is selected";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    activityResultLauncherForSingle.launch(true);//enter the single page for each song
                }
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
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.music_information, viewGroup, false);
                vh = new ViewHolder();
                vh.name = userNameTextView;
                vh.name = view.findViewById(R.id.nameForSingle);
                vh.artist = view.findViewById(R.id.artistForSingle);
                vh.duration = view.findViewById(R.id.duration);
                vh.cover = view.findViewById(R.id.imageViewCover);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            vh.position = i;

            e1.submit(() -> {
                if (vh.position != i) {
                    return;
                }
                if (vh.position == i) {
                    vh.name.post(() -> vh.name.setText(musicInformation.get(i).name));
                    vh.artist.post(() -> vh.artist.setText(musicInformation.get(i).artist));
                    vh.duration.post(() -> vh.duration.setText(FormatTheTimeUtils.getFormattedTime(musicInformation.get(i).duration)));
                    vh.cover.post(() -> vh.cover.setImageBitmap(GetSongCoverUtils.getCoverPicture(musicInformation.get(i).path, false)));
                }
            });
            return view;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("musicInformationForMain", (Serializable) musicInformation);
        savedInstanceState.putInt("selectedSongForMain", selectedSong);
        savedInstanceState.putString("userNameForMain", userName);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        musicInformation = (ArrayList<JavaBeanSong>) savedInstanceState.getSerializable("musicInformationForMain");
        selectedSong = savedInstanceState.getInt("selectedSongForMain", selectedSong);
        userName = savedInstanceState.getString("userNameForMain");
    }

    private class ResultContractForLogin extends ActivityResultContract<Boolean, String> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            return new Intent(MainActivity.this, Login.class);
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            return intent.getStringExtra("user");// when the login page closed, send the user's name back
        }
    }

    private class ResultContractForSinglePage extends ActivityResultContract<Boolean, String> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            Intent intent = new Intent(MainActivity.this, SinglePage.class);
            intent.putExtra("loop", loopOrNot);
            intent.putExtra("musicInformation", (Serializable) musicInformation);
            intent.putExtra("selectedSong", selectedSong);
            intent.putExtra("isPlay", controlMusic.isPlaying());// send information needs for single page
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            return intent.getStringExtra("selectedSong");
        }
    }


    private class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            controlMusic = (MusicService.controlMusic) iBinder;
            musicService = controlMusic.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void unbind(boolean isUnbind) {
        if (!isUnbind) {
            controlMusic.pauseMusic();
            unbindService(myServiceConn);
        }
    }

    public class ReceiverForLoop extends BroadcastReceiver {// receive the duration time

        @Override
        public void onReceive(Context context, Intent intent) {
            loopOrNot = intent.getBooleanExtra("loopOrNot", false);

        }
    }

    public class ReceiverForFinish extends BroadcastReceiver {// receive the finish or not

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("musicFinished", true)) {
                if (!loopOrNot) {
                    ImageButton imageButton = findViewById(R.id.paused);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    controlMusic.pauseMusic();
                }

            } else {
                if (controlMusic.isPlaying()) {// check again because the loop may already started
                    ImageButton imageButton = findViewById(R.id.started);
                    pause.setImageDrawable(imageButton.getDrawable());//change button icon
                    controlMusic.continueMusic();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind(isUnbind);
        localBroadcastManagerForFinish.unregisterReceiver(receiverForFinish);
        localBroadcastManagerForLoop.unregisterReceiver(receiverForLoop);
    }

}