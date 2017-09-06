package com.robot.tuling.videomodule.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.robot.tuling.videomodule.BR;

import java.util.HashMap;

/**
 * Created by sunnysteam on 2017/8/9.
 */

public class MusicModel extends BaseObservable {

    //歌曲编号
    @Bindable
    private int musicId;

    //歌曲标题
    @Bindable
    private String musicTitle;

    //歌曲的专辑名
    @Bindable
    private String music_album;

    //歌曲的歌手名
    @Bindable
    private String music_artist;

    //歌曲文件的路径
    @Bindable
    private String musicFileUrl;

    //歌曲的总播放时长
    @Bindable
    private int music_duration;

    //歌曲文件的大小
    @Bindable
    private Long music_size;

    //歌曲封面路径
    @Bindable
    private String music_album_cover_path;

    //歌曲歌词路径
    @Bindable
    private String music_lrc_path;

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
        notifyPropertyChanged(BR.musicId);
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    private void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
        notifyPropertyChanged(BR.musicTitle);
    }

    public String getMusic_album() {
        return music_album;
    }

    private void setMusic_album(String music_album) {
        this.music_album = music_album;
        notifyPropertyChanged(BR.music_album);
    }

    public String getMusic_artist() {
        return music_artist;
    }

    private void setMusic_artist(String music_artist) {
        this.music_artist = music_artist;
        notifyPropertyChanged(BR.music_artist);
    }

    public String getMusicFileUrl() {
        return musicFileUrl;
    }

    private void setMusicFileUrl(String musicFileUrl) {
        this.musicFileUrl = musicFileUrl;
        notifyPropertyChanged(BR.musicFileUrl);
    }

    public int getMusic_duration() {
        return music_duration;
    }

    private void setMusic_duration(int music_duration) {
        this.music_duration = music_duration;
        notifyPropertyChanged(BR.music_duration);
    }

    public Long getMusic_size() {
        return music_size;
    }

    private void setMusic_size(Long music_size) {
        this.music_size = music_size;
        notifyPropertyChanged(BR.music_size);
    }

    public String getMusic_album_cover_path() {
        return music_album_cover_path;
    }

    public void setMusic_album_cover_path(String music_album_cover_path) {
        this.music_album_cover_path = music_album_cover_path;
        notifyPropertyChanged(BR.music_album_cover_path);
    }

    public String getMusic_lrc_path() {
        return music_lrc_path;
    }

    public void setMusic_lrc_path(String music_lrc_path) {
        this.music_lrc_path = music_lrc_path;
        notifyPropertyChanged(BR.music_lrc_path);
    }

    public void setItem(HashMap<String, Object> item) {
        setMusicId(((int) item.get("musicId")));
        setMusicTitle(((String) item.get("musicTitle")));
        setMusic_album(((String) item.get("music_album")));
        setMusic_artist(((String) item.get("music_artist")));
        setMusicFileUrl(((String) item.get("musicFileUrl")));
        setMusic_duration(((int) item.get("music_duration")));
        setMusic_size(((Long) item.get("music_size")));
        setMusic_album_cover_path(((String) item.get("music_album_cover_path")));
        String lrcpath = getMusicFileUrl().substring(0, getMusicFileUrl().lastIndexOf(".")+1);
        setMusic_lrc_path(lrcpath + "lrc");
    }
}
