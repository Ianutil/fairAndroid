package com.ian.android.templateproject.notification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ********
 *
 * @autor Ian
 * @date 2016-01-21 16:03
 * @describe PB 消息通知中心
 * <p/>
 */
public class NotificationCenter {
    private static NotificationCenter instance;

    private HashMap<String, List<IDispatchObserverListener>> centers; // 数据池
    private List notifyCenters; // 数据池

    private Lock lock; // 锁

    private NotificationCenter() {
        centers = new HashMap<>();
        notifyCenters = new ArrayList();
        lock = new ReentrantLock();
    }

    public static NotificationCenter shareInstance() {
        if (instance == null) {
            instance = new NotificationCenter();
        }
        return instance;
    }

    /**
     * ********
     * 通知监听者
     *
     * @param key
     * @param observer
     */
    public void postObserver(final String key, final Observer observer) {
        lock.lock();

        // 获取监听者队列中心
        List<IDispatchObserverListener> observers = centers.get(key);

        // 没有监听队列时，就直接跳转循环
        if (observers != null) {
            // 当有增加到过监听者队列的监听者时，开始分发监听结果
            for (IDispatchObserverListener listener : observers) {
                listener.onNotification(key, observer);
            }
        }

        lock.unlock();
    }

    /**
     * ****
     * 注册监听者
     *
     * @param key
     * @param observer
     */
    public void registerObserver(String key, IDispatchObserverListener observer) {
        lock.lock();

        // 获取监听者队列中心
        List<IDispatchObserverListener> observers = centers.get(key);

        // 没有创建过监听都队列时，就创建一个
        if (observers == null) {
            observers = new ArrayList();

            // 增加到通知中心去
            centers.put(key, observers);
        }

        // 监听者队列中，没有排过队，就都增加到队列中
        if (!observers.contains(observer)) {
            observers.add(observer);
        }

        lock.unlock();
    }

    /**
     * *******
     * 移除监听者
     *
     * @param key
     * @param observer
     */
    public void removeObserver(String key, IDispatchObserverListener observer) {
        lock.lock();

        // 获取监听者队列中心
        List<IDispatchObserverListener> observers = centers.get(key);

        // 没有监听队列时，就直接跳转循环
        if (observers == null) {
        }

        // 监听者队列中，正在排过队，就直接移除到队列中
        if (observers.contains(observer)) {
            observers.remove(observer);
        }

        lock.unlock();
    }



    /**
     * ****
     * 注册监听者
     * TODO 请注意使用时容易内存泄漏
     * @param observer
     */
    public void register(Object observer) {
        lock.lock();

        // 获取监听者队列中心
        // 没有创建过监听都队列时，就创建一个

        // 增加到通知中心去

        // 监听者队列中，没有排过队，就都增加到队列中
        if (!notifyCenters.contains(observer)) {
            notifyCenters.add(observer);
        }

        lock.unlock();
    }
    /**
     * *******
     * 移除监听者
     *
     * @param observer
     */
    public void remove(Object observer) {
        lock.lock();

        // 获取监听者队列中心
        // 没有监听队列时，就直接跳转循环
        // 监听者队列中，正在排过队，就直接移除到队列中
        if (notifyCenters.contains(observer)) {
            notifyCenters.remove(observer);
        }

        lock.unlock();
    }

    /**
     * ********
     * 通知监听者
     *
     * @param key
     * @param observer
     */
    public void post(Object observer) {
        lock.lock();

        // 获取监听者队列中心
        // 没有监听队列时，就直接跳转循环
        if (!notifyCenters.isEmpty()) {
            // 当有增加到过监听者队列的监听者时，开始分发监听结果
            for (Object listener : notifyCenters) {
                try {
                    // TODO onEvent 只需要监听者实现就行了
                    Method method = listener.getClass().getMethod("onEvent", observer.getClass());
                    method.invoke(listener, observer);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e){
                    e.printStackTrace();
                } catch (IllegalAccessException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        lock.unlock();
    }


    public void destroy() {
        centers.clear();
        centers = null;
        notifyCenters.clear();
        notifyCenters = null;
    }
}
