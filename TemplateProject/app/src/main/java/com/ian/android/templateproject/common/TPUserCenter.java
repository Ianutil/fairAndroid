package com.ian.android.templateproject.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.ian.android.templateproject.base.TPApplication;
import com.ian.android.templateproject.entity.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 用户信息保存
 *
 */
public class TPUserCenter implements IConstant{

    private static TPUserCenter instance;
    private SharedPreferences sharedPreferences;

    private boolean isShowGuide; // 是否显示

    private boolean isLogin; // 是否登录

    private User user; // 用户名和密码

    private TPUserCenter(){
        Context context = TPApplication.shareInstance().getApplicationContext();

        sharedPreferences = context.getSharedPreferences(SP_USER_INFO_FILE, Context.MODE_PRIVATE);
    }

    public static TPUserCenter shareInstance(){
        if (instance == null){
            instance = new TPUserCenter();
        }
        return instance;
    }

    /************
     *  是否需要显示 导航页
     *  true 表示第一次进入应用
     *  false 表示不是第一次进入
     *
     * @return boolean
     *
     */
    public boolean isLogin(){

        return isLogin;
    }

    /************
     *  是否需要显示 导航页
     *  true 表示第一次进入应用
     *  false 表示不是第一次进入
     *
     * @return boolean
     *
     */
    public boolean isShowGuide(){

        isShowGuide = sharedPreferences.getBoolean(KEY_IS_SHOW_GUIDE, true);


        return isShowGuide;
    }


    /************
     *  是否需要显示 导航页
     *
     */
    public void setShowGuide(boolean isShowGuide){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_SHOW_GUIDE, isShowGuide);
        editor.commit();
        editor = null;
    }


    /*********
     * 保存对象
     * @param key
     * @param object
     */
    private void saveObject(String key, Object object){
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(object);
            // 将字节流编码成base64的字符窜
            String value = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();

            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /********
     * 获取保存的对象
     * @param key
     * @return
     */
    private Object getObject(String key){
        Object object = null;
        String value = sharedPreferences.getString(key, null);
        if(value == null){
            return null;
        }
        //读取字节
        byte[] base64 = Base64.decode(value.getBytes(), Base64.DEFAULT);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);

        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            //读取对象
            object = bis.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }
}
