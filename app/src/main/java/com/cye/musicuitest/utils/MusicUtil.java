package com.cye.musicuitest.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.cye.musicuitest.bean.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setSong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                int artID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                song.setArtId(artID);
                song.setAlbumArt(MusicUtil.getAlbumArt(context,artID));
                if (song.getSize() > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getSong().contains("-")) {
                        String[] str = song.getSong().split("-");
                        song.setSinger(str[0]);
                        song.setSong(str[1]);
                    }
                    list.add(song);
                }
            }
            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    public static String getAlbumArt(Context context,int album_id){
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0)
        {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }
}
