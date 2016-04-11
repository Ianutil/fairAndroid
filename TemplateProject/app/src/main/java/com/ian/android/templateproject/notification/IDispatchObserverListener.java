package com.ian.android.templateproject.notification;

/**
 * ********
 *
 * @autor Ian
 * @date 2016-01-21 16:03
 * @describe PB 消息通知中心监听者
 * <p/>
 */
public interface IDispatchObserverListener {
    void onNotification(String key, Observer observer);
}
