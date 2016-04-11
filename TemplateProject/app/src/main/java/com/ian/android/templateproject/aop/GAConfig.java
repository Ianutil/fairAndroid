package com.ian.android.templateproject.aop;

import com.ian.android.templateproject.controller.MainActivity;
import com.ian.android.templateproject.controller.home.ClipPhotoActivity;
import com.ian.android.templateproject.controller.home.HomeActivity;

import java.util.HashMap;

/**
 * @author Ian
 * @date 2016-01-25 10:16
 * <p/>
 * AOP 用户数据采集打点信息配置
 * TODO 注意使用方法记录规则
 * TODO Key 监听 类 + TAG<分隔符标记> + 监听方法
 * TODO Value 记录打点编号记录码
 */
public class GAConfig {

    // 测试监测的方法名
    public static final String TAG = "@";
    public static final String ONCREATE = "onCreate";
    public static final String ONCREATEVIEW = "onCreateView";
    public static final String PAGE = "page"; // 页面打点标记
    public static final String OPERATION = "operation"; // Action打点标记

    // 测试
    public static final String kGA0000 = "201601261025";
    public static final String kGA0001 = "201601261025_01";
    public static final String kGA0002 = "201601261025_02";
    public static final String kGA0003 = "201601261025_03";
    public static final String kGA0004 = "201601261025_04";

    //初始化类方法和点的关联
    public static HashMap<String, String> GACMap = new HashMap<String, String>();

    static {
        // TODO Key 监听 类 + TAG<分隔符标记> + 监听方法
        // TODO Value 记录打点编号记录码
        /** 测试 */
        GACMap.put(MainActivity.class.getName() + TAG + ONCREATE, kGA0000 + TAG + PAGE);
        GACMap.put(MainActivity.class.getName() + TAG + "requestData", kGA0001 + TAG + OPERATION);
        GACMap.put(HomeActivity.class.getName() + TAG + ONCREATE, kGA0002 + TAG + PAGE);
        GACMap.put(ClipPhotoActivity.class.getName() + TAG + ONCREATE, kGA0003 + TAG + PAGE);
        GACMap.put(ClipPhotoActivity.class.getName() + TAG + "selectOnClick", kGA0004 + TAG + OPERATION);
    }


    // 初始化 描述点和监听类/方法的 关联
    public static HashMap<String, String> GAStrMap = new HashMap<String, String>();

    static {
        // TODO Key 记录打点编号记录码
        // TODO Value 记录打点页面
        /** 测试 */
        GAStrMap.put(kGA0000, "测试");
        GAStrMap.put(kGA0001, "请求网络");
        GAStrMap.put(kGA0002, "进入主页");
        GAStrMap.put(kGA0003, "进入修改头像");
        GAStrMap.put(kGA0004, "选择修改头像");
    }


}
