package com.robot.common.lib.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.robot.common.lib.media.mediaentity.PhotoFolderInfo;
import com.robot.common.lib.media.mediaentity.PhotoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/06/20
 *      version:
 *      desc   : 获取所有视频文件
 * </pre>
 */
public class MediaTools {

    public static List<PhotoFolderInfo> getAllVideoFolder(Context context) {
        String[] projectionVideo = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Thumbnails.DATA
        };
        List<PhotoFolderInfo> allFolders = new ArrayList<>();
        // 临时集合存储数据，并初始化，最后把数据倒入新的集合
        List<PhotoFolderInfo> tempAllFolders = new ArrayList<>();
        // 集合第一个类用于装载所有的视频文件信息
        PhotoFolderInfo allPhotoFolderInfo = new PhotoFolderInfo();
        allPhotoFolderInfo.setFolderId(0);
        allPhotoFolderInfo.setFolderName("所有视频");
        allPhotoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
        tempAllFolders.add(0, allPhotoFolderInfo);

        SparseArray<PhotoFolderInfo> bucketMap = new SparseArray<>();

        // 查询Video数据
        Cursor cursor = null;
        try {
            cursor = MediaStore.Video.query(context.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projectionVideo);
            if (cursor != null) {
                int bucketIdColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                int bucketDisplayNameColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                while (cursor.moveToNext()) {
                    int bucketId = cursor.getInt(bucketIdColumnIndex);
                    String bucketName = cursor.getString(bucketDisplayNameColumnIndex);

                    // 找到该行数据下标
                    int idColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int dateTakenColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

                    // 通过下标把数据取出来
                    int id = cursor.getInt(idColumnIndex);
                    String path = cursor.getString(dataColumnIndex);
                    long dateTaken = cursor.getLong(dateTakenColumnIndex);

                    File file = new File(path);
                    if (file.exists() && file.length() > 0) {
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(id);
                        photoInfo.setPhotoPath(path);
                        photoInfo.setDate_taken(dateTaken);

                        if (allPhotoFolderInfo.getCoverPhoto() != null) {
                            allPhotoFolderInfo.setCoverPhoto(photoInfo);
                        }
                        //通过bucketId获取文件夹
                        PhotoFolderInfo photoFolderInfo = bucketMap.get(bucketId);

                        if (photoFolderInfo == null) {
                            photoFolderInfo = new PhotoFolderInfo();
                            photoFolderInfo.setPhotoList(new ArrayList<PhotoInfo>());
                            photoFolderInfo.setFolderId(bucketId);
                            photoFolderInfo.setFolderName(bucketName);
                            photoFolderInfo.setCoverPhoto(photoInfo);
                            bucketMap.put(bucketId, photoFolderInfo);
                            tempAllFolders.add(photoFolderInfo);
                        }
                        photoFolderInfo.getPhotoList().add(photoInfo);

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        allFolders.addAll(tempAllFolders);
        return allFolders;
    }
}
