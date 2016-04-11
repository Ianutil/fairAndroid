package com.ian.android.templateproject.common.imageload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;

import com.ian.android.templateproject.base.TPApplication;
import com.ian.android.templateproject.utils.FileUtils;
import com.ian.android.templateproject.service.HttpUtils;
import com.ian.android.templateproject.service.TaskHttpRequest;
import com.ian.android.templateproject.utils.LogUtil;
import com.ian.android.templateproject.utils.ToastUtil;
import com.ian.android.templateproject.utils.ToolUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 重缓冲的异步加载工具类
 *
 */
public class DownloadImageUtil {

    private static DownloadImageUtil instance;

    // 一级缓存
    private static Map<String, Bitmap> firstLevel;
    // 二级缓存
    private static Map<String, SoftReference<Bitmap>> secondLevel;


    private Handler handler; // 发送handler

    // 把下载的图片文件保存在当前目录下
    private static String filePath;

    /** 构造器 **/
    private DownloadImageUtil(){
        init();
    }

    public static DownloadImageUtil shareInstance(){

        if (instance == null){
            instance = new DownloadImageUtil();
        }
        return instance;
    }


    /** 参数初始化 ****/
    private void init() {
        // 创建存放的目录
        filePath = FileUtils.getPicClipDir();

        logger("FileUtils->"+ filePath);

        handler = new Handler();

        // 进行初始化操作
        if (firstLevel == null) {
            secondLevel = new HashMap<String, SoftReference<Bitmap>>();
            // 第一个参数：初始容量
            // 第二个参数：加载因子
            // 第三个参数：排序模式（对于访问顺序，为true；对于插入顺序，则为false）
            firstLevel = new LinkedHashMap<String, Bitmap>(15, 0.7f, true) {
                private static final long serialVersionUID = 1L;

                /**
                 * 如果此映射移除其最旧的条目，则返回true 在将新条目插入到映射后，put 和 putAll将调用此方法
                 * 此方法可以提供在每次添加新条目时移除最旧条目的实现程序
                 * 如果映射表示缓存，则此方法非常有用：它允许映射通过删除旧条止减少内存损耗
                 *
                 * el 表示在映射中最早插入的条目，如果是访问顺序映射，则为最早访问的条目。 如果返回 为true，则此条目将会被移 除
                 * 如果在put 和putAll调用之为空，那么这个就为最新的条目 返回 false,则是保留该条目
                 * ***/
                @Override
                protected boolean removeEldestEntry(Entry<String, Bitmap> el) {
                    // 当增加的条目大于当前容器时，把所要增加的条目增加到二级缓存中
                    if (size() > 15) {
                        // 把当前条目放置到二级缓存中
                        secondLevel.put(el.getKey(), new SoftReference<Bitmap>(
                                el.getValue()));
                        // 删除该条目
                        return true;
                    }
                    return super.removeEldestEntry(el);
                }
            };
        }
    }

    /************
     * 下载图片
     * @param url 下载地址
     * @param listener    加载完成图片要进行的一些操作
     */
    public void download(String url, UrlLoaderListener listener){
        if (TextUtils.isEmpty(url)){
            return;
        }
        // 文件名<URL的后半部分>
        String name = getFileName(url);

        // 从缓存中读出当前图片
        Bitmap bitmap = getImageFromFirstLevel(name);

        //如果存在就返回，此图片
        if (bitmap != null){
            sendMessage(url, listener, bitmap);
            return;
        }

        //从第二缓存中查找
        bitmap = getImageFromSecondLevel(name);
        //如果存在就返回
        if(bitmap!=null){
            sendMessage(url, listener, bitmap);
            return;
        }

        //从SD卡下查找
        bitmap = getImageFromSDCard(name);
        if(bitmap!=null){
            sendMessage(url, listener, bitmap);
            return;
        }

        //从网络中下载
        if (ToolUtils.isNetworkAvailable(TPApplication.shareInstance())){
            doTask(url, listener);
        }else {
            ToastUtil.showShortToast(TPApplication.shareInstance(), "网络连接异常");
        }
    }

    /********
     *
     * @param url
     * @param listener
     */
    private void doTask(final String url, final UrlLoaderListener listener){
        TaskHttpRequest.shareInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                // 保存在SD卡下的路径
                File file = new File(filePath, getFileName(url));
                logger(file.getPath());

                // 下载完成后，发送到监听端
                Bitmap bitmap = HttpUtils.downloadFromInternet(url, file);

                if (bitmap != null){
                    sendMessage(url, listener, bitmap);
                }
            }
        });
    }

    /************
     * 发送到接收端
     * @param url
     * @param listener
     * @param bitmap
     */
    public void sendMessage(final String url,final UrlLoaderListener listener, final Bitmap bitmap){
        if (listener == null){
            bitmap.recycle();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLoader(url, bitmap);
            }
        });
    }

    /** 从一级缓存中取出当前文件名的图片，不存在时返回 为null ***/
    public Bitmap getImageFromFirstLevel(String path) {
        // 查看当前一级缓存中，是否有这个条目
        if (firstLevel.containsKey(path)) {
            logger("一级缓存：" + path);
            return firstLevel.get(path);
        }
        return null;
    }

    /** 从二级缓存中取出当前文件名的图片，不存在时返回 为null ***/
    public Bitmap getImageFromSecondLevel(String path) {
        // 查看当前二级缓存中，是否有这个条目
        if (secondLevel.containsKey(path)) {
            // 获取一个软引用对象
            SoftReference<Bitmap> soft = secondLevel.get(path);
            if (soft != null) {
                // 从软引用对象获取当前图片
                Bitmap bit = soft.get();
                if (bit != null) {
                    logger("二级缓存：" + path);
                    // 返回把查找到的图片
                    return bit;
                }
            } else {
                // 如果映射中是否存在此条目的映射，就移除
                secondLevel.remove(path);
            }
        }
        return null;
    }

    /*** path是SD卡下的图片文件名，把当前图片增加到一级缓存中 ****/
    public Bitmap getImageFromSDCard(String path) {
        File file = new File(filePath, path);
        if (file.exists()) {
            logger("从SD卡获取数据：" + file.getAbsolutePath());
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            // 放入一级缓存中
            firstLevel.put(path, bitmap);
            // 返回 图片
            return bitmap;
        }
        return null;
    }



    /***
     * 截取字符串，从一个路径中截取些文件名，并且返回一个文件名字符串
     * @param path
     * **/
    public String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    /*** 输出LOG信息 **/
    public static void logger(String content) {
        LogUtil.d("LogadImages", content);
    }

}

