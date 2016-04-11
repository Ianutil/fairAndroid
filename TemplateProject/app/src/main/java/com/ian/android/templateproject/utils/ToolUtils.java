package com.ian.android.templateproject.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 工具方法管理类
 */
public class ToolUtils {

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {

                for (int i = 0; i < networkInfo.length; i++) {

                    LogUtil.d("NETWORK", i + "===状态===" + networkInfo[i].getState());
                    LogUtil.d("NETWORK", i + "===类型===" + networkInfo[i].getTypeName());

                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //设置手机屏幕亮度，

    /**
     * 设置手机屏幕亮度
     *
     * @param mActivity,values
     */
    public static void setWindowAlpha(Activity mActivity, float values) {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = values;
        mActivity.getWindow().setAttributes(params);
    }

    /**
     * 把 钱 的值 加上,号和小数补全
     *
     * @return 每个月的钱 6,300.00
     */
    public static String formatMoney(String money) {
        StringBuilder mBuilder = new StringBuilder();

        // 清除StringBuilder
        mBuilder.delete(0, mBuilder.length());
        // 获取小数点的角标
        int pointIndex = money.lastIndexOf('.');
        // 判断小数点位置进行补0
        if (pointIndex == -1) {
            mBuilder.append(money + ".00");
        } else if (money.substring(pointIndex).length() == 2) {
            mBuilder.append(money + "0");
        }
        int length = mBuilder.length();

        // 初始化x为小数点前一位的角标
        for (int x = length - 3; x > 3; ) {
            // 如果去掉后面的小数位还大于3位,就前移3个角标加上","号
            x -= 3;
            mBuilder.insert(x, ",");
        }

        return mBuilder.toString();
    }


    /**
     * 检查字符串是否是邮箱
     *
     * @param strEmail
     * @return 合法：true,不合法：false
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 检查字符串是否是手机号
     *
     * @param phoneNumber
     * @return 合法：true,不合法：false
     */
    public static boolean isPhone(String phoneNumber) {
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


    /**
     * **********
     * 格式化时间
     *
     * @param time        时间
     * @param format_form 时间格式
     * @return
     */
    public static String formatDate(long time, String format_form) {
        SimpleDateFormat format = new SimpleDateFormat(format_form);

        String name = format.format(time);

        return name;
    }


    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */

    public static Date parseDateString(String strDate, String pattern) {

        if (isEmpty(strDate)) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * ******
     * 判断是否字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {

        if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
            return true;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }

        return true;
    }

    /**
     * *********
     * 获取设备名
     *
     * @return 设备名
     */
    public static String getDeviceName() {
        return new Build().MODEL;
    }


    /**
     * *********
     * 把图片转换成字符串
     *
     * @param bitmap
     * @return 字符串
     */
    public static String convertBitmapToString(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        //初始化一个流对象
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //把bitmap100%高质量压缩 到 output对象里
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        byte[] buf = output.toByteArray();//转换成功了


        String result = Base64.encodeToString(buf, Base64.DEFAULT);

        return result;
    }

    /**
     * *********
     * 将日期转化为周几格式
     *
     * @param strDate "2012-12-12"
     * @return 字符串 周一
     */
    public static String getWeekDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        Date date = null;
        try {
            date = format.parse(strDate);// 将字符串转换为日期
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * *********
     * 将日期只保留月和日
     *
     * @param strDate "2012-12-12"
     * @return 字符串 "12-12"
     */
    public static String changeWeekDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
        Date date = null;
        String str = null;
        try {
            date = format.parse(strDate);// 将字符串转换为日期
            str = (date.getMonth() + 1) + "-" + date.getDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 判断网络状态
     * 1、wifi网
     * 2、3G网
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            // wifi网
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // 3G网
            NetworkInfo mobilWorkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((networkInfo != null && networkInfo.isConnected())
                    || (mobilWorkInfo != null && mobilWorkInfo.isConnected())) {

                LogUtil.d("TAG", "isNetworkConnected true");
                return true;
            }

        }
        return false;
    }

    /********
     * 把整型数值格式化成默认日期类型
     * @param time
     * @return
     */
    public static String formatDate(long time) {
        return formatDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 判断GPS是否开启
     *
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean flag = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (flag) {
            LogUtil.d("TAG", "isGPSEnabled true");
            return true;
        } else {
            LogUtil.d("TAG", "isGPSEnabled false");
            return false;
        }
    }

    /**
     * 隐藏虚拟键盘
     *
     * @param context
     */
    public static void KeyBoardCancle(Activity context) {
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     * 隐藏虚拟键盘
     *
     * @param editText
     */
    public static void KeyBoardCancle(EditText editText) {
        ((InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出软键盘
     *
     * @param editText
     */
    public static void KeyBoardPop(final EditText editText) {

        new Handler() {
            public void handleMessage(android.os.Message msg) {
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }.sendEmptyMessageDelayed(0, 200);

    }

    /**
     * 格式化价格
     *
     * @param str
     * @return
     */
    public static String formatPrice(String str) {
        return new DecimalFormat("0.00").format(Double.valueOf(str));
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * ******
     * 获取设备ID，如果没有就生成一个UUID
     * 1、本机号码
     * 2、设备唯一编号
     * 3、SIM卡的序号
     *
     * @param context
     * @return 设备ID 或者 UUID
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String device, serial, phone, androidId;

        phone = tm.getLine1Number(); //获取本机号码

        device = "" + tm.getDeviceId(); //获取智能设备唯一编号

        serial = "" + tm.getSimSerialNumber(); //获得SIM卡的序号

        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID devicedID = new UUID(androidId.hashCode(), ((long) device.hashCode() << 32) | serial.hashCode() | phone.hashCode());

        LogUtil.d("TAG", "devicedID=" + devicedID + ",phone=" + phone + ",device=" + device + ",serial=" + serial + ",androidId=" + androidId);

        return devicedID.toString();
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String version = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;

            if (TextUtils.isEmpty(version)) {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.e("VersionInfo", "Exception", e);
        }
        return version;
    }

    /**
     * 获取meta号
     *
     * @param context
     * @return
     */
    public static String getAppMeteValue(Context context, String key) {
        String msg = null;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            msg = info.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return msg;
    }
}

