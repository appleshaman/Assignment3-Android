package com.example.assignment3;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

public class GetSongCover {
    public static Bitmap getCoverPicture(String path, boolean bigOrSmall) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] bytes = mediaMetadataRetriever.getEmbeddedPicture();
        float size;
        if(bigOrSmall){
            size = 500;
        }else{
            size = 120;
        }
        Bitmap bitmap;
        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Matrix matrix = new Matrix();
            float sx = (size / width);
            float sy = (size / height);
            matrix.postScale(sx, sy);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        } else {

            bitmap = BitmapFactory.decodeFile("drawable/music_default_cover.jpg");
            int width = bitmap.getWidth();//if could not find cover from the song
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float sx = (size / width);
            float sy = (size / height);
            matrix.postScale(sx, sy);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        }
        return bitmap;
    }
}

