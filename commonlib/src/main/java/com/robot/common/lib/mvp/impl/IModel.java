package com.robot.common.lib.mvp.impl;

import com.robot.common.lib.mvp.base.MVPModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.functions.Consumer;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/28
 *      version:
 *      desc   : 约束接口
 * </pre>
 */
public interface IModel<T> extends MVPModel {

    ExecutorService executor = Executors.newFixedThreadPool(5);

    public void getData(Consumer<T> consumer);
}
