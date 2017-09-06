package com.robot.common.lib.databases;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yuanzhaofeng
 * on 2017/8/3 16:04.
 * desc:视频数据库表
 * version:
 */
@Entity(
        nameInDb = "tb_video"
)
public class VideoTable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String fileType;
    @NotNull
    private String fileName;
    @NotNull
    private int functionType;
    @NotNull
    private String filePath;
    @NotNull
    private String recordTime;
    private String createBy;
    private String createDT;
    private String updateBy;
    private String updateDT;

    public String getUpdateDT() {
        return this.updateDT;
    }

    public void setUpdateDT(String updateDT) {
        this.updateDT = updateDT;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateDT() {
        return this.createDT;
    }

    public void setCreateDT(String createDT) {
        this.createDT = createDT;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFunctionType() {
        return this.functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 926184179)
    public VideoTable(Long id, @NotNull String fileType, @NotNull String fileName,
                      int functionType, @NotNull String filePath,
                      @NotNull String recordTime, String createBy, String createDT,
                      String updateBy, String updateDT) {
        this.id = id;
        this.fileType = fileType;
        this.fileName = fileName;
        this.functionType = functionType;
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.createBy = createBy;
        this.createDT = createDT;
        this.updateBy = updateBy;
        this.updateDT = updateDT;
    }

    @Generated(hash = 47411859)
    public VideoTable() {
    }
}
