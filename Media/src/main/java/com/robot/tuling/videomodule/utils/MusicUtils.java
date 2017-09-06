package com.robot.tuling.videomodule.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.robot.tuling.videomodule.Model.Music;
import com.robot.tuling.videomodule.Model.MusicModel;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.Service.EventCallback;

import java.util.HashMap;
import java.util.List;

/**
 * 歌曲工具类
 */
public class MusicUtils {

    /**
     * 扫描歌曲
     */
    public static void scanMusic(Context context, List<Music> musicList) {
        musicList.clear();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }

        long filterSize = ParseUtils.parseLong(Preferences.getFilterSize()) * 1024;
        long filterTime = ParseUtils.parseLong(Preferences.getFilterTime()) * 1000;

        int i = 0;
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue;
            }

            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            if (fileSize < filterSize || duration < filterTime) {
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String unknown = context.getString(R.string.unknown);
            artist = (TextUtils.isEmpty(artist) || artist.toLowerCase().contains("unknown")) ? unknown : artist;
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String coverPath = getCoverPath(context, albumId);
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));

            Music music = new Music();
            music.setId(id);
            music.setType(Music.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setDuration(duration);
            music.setPath(path);
            music.setCoverPath(coverPath);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            if (++i <= 20) {
                // 只加载前20首的缩略图
                //                CoverLoader.getInstance().loadThumbnail(music);
            }
            musicList.add(music);
        }
        cursor.close();
    }

    public static List<MusicModel> scanAllAudioFiles(Context context, List<MusicModel> musicList) {
        //生成动态数组，并且转载数据
        musicList.clear();
        //查询媒体数据库
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int musicId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String musicTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲封面路径
                int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String music_album_cover_path = getAlbumArt(context, anInt);
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String music_album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String music_artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String musicFileUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int music_duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long music_size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (music_size > 1024 * 1024 && musicFileUrl.contains(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath())) {//大于1M
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("musicId", musicId);
                    map.put("musicTitle", musicTitle);
                    map.put("musicFileUrl", musicFileUrl);
                    map.put("music_file_name", musicTitle);
                    map.put("music_album", music_album);
                    map.put("music_artist", music_artist);
                    map.put("music_duration", music_duration);
                    map.put("music_size", music_size);
                    map.put("music_album_cover_path", music_album_cover_path);
                    MusicModel model = new MusicModel();
                    model.setItem(map);
                    musicList.add(model);
                }
                cursor.moveToNext();
            }
        }
        return musicList;
    }

    public static List<MusicModel> scanAllAudioFiles(Context context, List<MusicModel> musicList, final EventCallback<MusicModel> callback) {
        //生成动态数组，并且转载数据
        musicList.clear();
        //查询媒体数据库
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int musicId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String musicTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲封面路径
                int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String music_album_cover_path = getAlbumArt(context, anInt);
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String music_album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String music_artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String musicFileUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int music_duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long music_size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (music_size > 1024 * 1024 && musicFileUrl.contains(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath())) {//大于1M
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("musicId", musicId);
                    map.put("musicTitle", musicTitle);
                    map.put("musicFileUrl", musicFileUrl);
                    map.put("music_file_name", musicTitle);
                    map.put("music_album", music_album);
                    map.put("music_artist", music_artist);
                    map.put("music_duration", music_duration);
                    map.put("music_size", music_size);
                    map.put("music_album_cover_path", music_album_cover_path);
                    MusicModel model = new MusicModel();
                    model.setItem(map);
                    musicList.add(model);
                    callback.onEvent(model);
                }
                cursor.moveToNext();
            }
        }
        return musicList;
    }

    private static String getAlbumArt(Context context, int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur != null && cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }

    public static boolean isAudioControlPanelAvailable(Context context) {
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }

    private static String getCoverPath(Context context, long albumId) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/audio/albums/" + albumId),
                new String[]{"album_art"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext() && cursor.getColumnCount() > 0) {
                path = cursor.getString(0);
            }
            cursor.close();
        }
        return path;
    }
}
