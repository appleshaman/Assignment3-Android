package com.example.assignment3;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.example.assignment3.JavaBeanSong;
import java.util.ArrayList;
import java.util.List;

public class ScanLocalMusic {

    public static ArrayList<JavaBeanSong> getMusicData(Context context) {
        ArrayList<JavaBeanSong> list = new ArrayList<JavaBeanSong>();
        //use cursor like in assignment2
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                JavaBeanSong song = new JavaBeanSong();
                song.name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                song.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.name.contains("-")) {
                        String[] str = song.name.split("-");
                        song.artist = str[0];
                        song.name = str[1];
                    }
                    list.add(song);
                }
            }
            cursor.close();
        }
        return list;
    }

}
