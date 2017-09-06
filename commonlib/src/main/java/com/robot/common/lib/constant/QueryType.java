package com.robot.common.lib.constant;

/**
 * Created by zouweilin
 * on 2016/12/22.
 */
public enum QueryType {

    None,
    Control,
    Charge,
    Distance,
    ProgramAction,      // 开始一系列的动作，如ProgramAction+actionList
    SpeakText,          // 语音
    PAUSE,              // 其他类型，比如暂停
    ReplyControl,       // 机器人已经准备好自动开启视频
    GetLocation,
    // 导航
    NavigationMap,      // 获取导航地图
    NavigationMapSize,  // 导航地图原始像素
    NavigationMonitor,  // 需要导航提供坐标，监控
    NavigationControl,  // AB点移动
    NavigationLocation, // 机器人位置点
    NavigationStop      // 导航结束
}
