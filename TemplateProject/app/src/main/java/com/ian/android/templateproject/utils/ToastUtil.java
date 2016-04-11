package com.ian.android.templateproject.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 提示框管理类
 *
 */
public class ToastUtil {


    public static void showShortToast(Context context,String msg){
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 19);
        toast.show();
    }

    public static void showLongToast(Context context,String msg){
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 19);
        toast.show();
    }
}
