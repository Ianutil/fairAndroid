package com.ian.android.templateproject.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.ian.android.templateproject.service.HttpHeader;
import com.ian.android.templateproject.service.HttpUtils;
import com.ian.android.templateproject.service.ServiceMediator;
import com.ian.android.templateproject.service.TaskHttpRequest;
import com.ian.android.templateproject.utils.LogUtil;
import com.ian.android.templateproject.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;

/***********
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 外服云 Application
 */
public class TPApplication extends Application {

    private static TPApplication instance;

    public List<Activity> actitiyManager; // 管理Activity栈

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // 开发模式：debug模式
        // 当release模板，都
        LogUtil.isDebug = true;

        // 管理Activity栈
        actitiyManager = new ArrayList<Activity>();

        // 初始网络请求指示器
        ServiceMediator mediator = new ServiceMediator();
        TaskHttpRequest.shareInstance().setServiceMediator(mediator);

        // 配置请求头信息
        initHttpHeader(this);
    }

    // 配置请求头信息
    private void initHttpHeader(Context context){
        // 配置请求头信息
        HttpUtils.header = new HttpHeader();
        HttpUtils.header.deviceId = ToolUtils.getDeviceId(context); // 获取设备ID
        HttpUtils.header.deviceName = ToolUtils.getDeviceName(); // 获取设备名

    }

    public static TPApplication shareInstance() {
        return instance;
    }
}