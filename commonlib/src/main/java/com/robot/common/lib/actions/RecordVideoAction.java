package com.robot.common.lib.actions;

/**
 * Created by yuanzhaofeng
 * on 2017/8/11 16:52.
 * desc: 拍视频指令
 * version:
 */
public enum RecordVideoAction {
    None,//无
    StartRecord,//录制
    StopRecord,//停止录制
    Open,//打开预览
    Close,//关闭预览
    Delete,//删除视频
    Send,//发送到家庭圈
    Next,//下一个
    Previous,//上一个
    First,//第一个
    Last,//最后一个
}
