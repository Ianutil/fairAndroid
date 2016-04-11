package com.ian.android.templateproject.utils;

import android.os.Environment;

import com.ian.android.templateproject.base.TPApplication;
import com.ian.android.templateproject.common.IConstant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ******
 *
 * @author Ian
 * @date 2016-01-14 09:28
 * @describe 本地文件操作工具类
 */
public class FileUtils {

    /**
     *
     * TODO 相机拍照后，存放路径
     * 根据时间戳，返回一个空图片文件
     */
    public static File getImageFile() {
        String imagePath = getPicDir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        File imageFile = new File(imagePath + File.separator + "WMI_IMG_" + timeStamp + ".jpg");
        return imageFile;
    }

    /**
     * 获取图片目录
     *
     * @return String
     */
    private static String getPicDir() {
        return createSDCardPath(IConstant.FILE_IMAGE_PATH + File.separator + IConstant.TAG_FILE_URL);
    }

    /**
     * 获取截图存放目录
     */
    public static String getPicClipDir() {
        return createSDCardPath(IConstant.FILE_IMAGE_PATH + File.separator + IConstant.TAG_CLIPED_URL);
    }

    /**
     * **********
     * 获取指定存储根目录
     * 1、不存在时，创建文件夹
     * 2、存在时，直接返回路径
     *
     * @param file 文件夹名
     * @return 返回创建文件件的路径
     */
    private static String createSDCardPath(String file) {
        if (isSDAvailable()) {
            return getSDDir(file);
        } else {
            return getDataDir(file);
        }
    }

    /**
     * 判断sd卡是否可以用
     *
     * @return boolean
     */
    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? true : false;
    }


    /**
     * 获取到手机内存的目录
     *
     * @param string
     * @return
     */
    private static String getDataDir(String string) {
        String path = TPApplication.shareInstance().getCacheDir().getAbsolutePath() + File.separator + string;
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file.getAbsolutePath();
            } else {
                return "";
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取到sd卡的目录,指定名称的文件路径
     *
     * @param name
     * @return
     */
    private static String getSDDir(String name) {
        StringBuilder sb = new StringBuilder();
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 设置根路径
        sb.append(absolutePath);

        // 根路径
        sb.append(File.separator).append(IConstant.FILE_ROOT).append(File.separator).append(name);

        String filePath = sb.toString();
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file.getAbsolutePath();
            } else {
                return "";
            }
        }
        return file.getAbsolutePath();
    }
}
