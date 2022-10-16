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
                song.coverId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                song.songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                if (song.name.contains("-")) {// split the song name and singer's name
                    String[] strings = song.name.split("-");
                    song.name = strings[1];
                    song.artist = strings[0];
                }
                if ((song.name.charAt(song.name.length()-4) == '.')&&// remove .mp3 suffix
                        (song.name.charAt(song.name.length()-3) == 'm')&&
                        (song.name.charAt(song.name.length()-2) == 'p')&&
                        (song.name.charAt(song.name.length()-1) == '3')){
                    song.name = song.name.substring(0, song.name.length()-4);
                }

                if ((song.name.charAt(song.name.length()-5) == '.')&&
                        (song.name.charAt(song.name.length()-4) == 'f')&&// remove .flac suffix
                        (song.name.charAt(song.name.length()-3) == 'l')&&
                        (song.name.charAt(song.name.length()-2) == 'a')&&
                        (song.name.charAt(song.name.length()-1) == 'c')){
                    song.name = song.name.substring(0, song.name.length()-4);
                }

                list.add(song);
            }
            cursor.close();
        }
        return list;
    }
}
