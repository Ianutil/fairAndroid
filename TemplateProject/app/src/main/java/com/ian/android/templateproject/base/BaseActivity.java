package com.ian.android.templateproject.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.common.IConstant;
import com.ian.android.templateproject.common.dialog.LoadingDialog;
import com.ian.android.templateproject.service.IDispatchResponseListener;
import com.ian.android.templateproject.service.Response;
import com.ian.android.templateproject.service.TaskHttpRequest;
import com.ian.android.templateproject.utils.LogUtil;
import com.ian.android.templateproject.utils.ToastUtil;
import com.ian.android.templateproject.utils.ToolUtils;

/***********
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 所有Activity基类
 */
public class BaseActivity extends FragmentActivity implements IConstant, IDispatchResponseListener {

    protected LoadingDialog progressDialog;
    protected static boolean isResultPresent; // 判断是否为接入结果的Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 没有标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        // 增加进栈
        TPApplication.shareInstance().actitiyManager.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO 测试代码
        TaskHttpRequest.shareInstance().getHandler().listener = this;
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 移除进栈
        TPApplication.shareInstance().actitiyManager.remove(this);
        super.onDestroy();

        // 立即停止所有的线程
        TaskHttpRequest.shareInstance().getWorkQueue().clear();
    }

    @Override
    public void onBackPressed() {
        // 点击"返回"按钮时触发的逻辑
        super.onBackPressed();

        if (isResultPresent){
            isResultPresent = !isResultPresent;
            // 第一个参数：进入的
            // 第二个参数：退出的
            this.overridePendingTransition(R.anim.activity_alpha_01, R.anim.down_translate_exit);
        }else {
            // 第一个参数：进入的
            // 第二个参数：退出的
            this.overridePendingTransition(R.anim.inside_translate_back, R.anim.outside_translate_back);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                return true;
            }

            // 释放单例类
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setTitle(int titleId) {
//        super.setTitle(titleId);

        String title = getResources().getString(titleId);
        setTitle(title);
    }

    @Override
    public void setTitle(CharSequence title) {
//        super.setTitle(title);

        // 左边返回按钮
        View view = findViewById(R.id.btn_left);
        view.setVisibility(View.INVISIBLE);
        view.setEnabled(false);

        // 右边返回按钮
        view = findViewById(R.id.btn_right);
        view.setVisibility(View.INVISIBLE);
        view.setEnabled(false);

        view = findViewById(R.id.tv_title);
        ((TextView) view).setText(title);
    }

    /*******
     * 带返回按钮的title bar
     *
     * @param title
     */
    public void setBackTitle(CharSequence title) {
        setTitle(title);

        // 左边返回按钮
        View view = findViewById(R.id.btn_left);
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
    }

    /*******
     * 带返回按钮的title bar
     *
     * @param titleId
     */
    public void setBackTitle(int titleId) {
        setTitle(titleId);

        // 左边返回按钮
        View view = findViewById(R.id.btn_left);
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
    }

    /******
     * 返回按钮事件监听
     *
     * @param view
     */
    public void backOnClick(View view) {
        finish();

        // 第一个参数：进入的
        // 第二个参数：退出的
        this.overridePendingTransition(R.anim.inside_translate_back, R.anim.outside_translate_back);
    }

    // 下一个按钮事件监听
    public void nextOnClick(View view) {

    }


    //*************请求网络回调*********************
    @Override
    public void onSuccess(String method, Response result) {
        showProgress();
    }

    @Override
    public void onError(String method, Response result) {
        // 不要这些处理时，可以重写，并不回调父类方法
        dismissProgress();
        if (result != null) {
            if (!TextUtils.isEmpty(result.errorMsg)) {
                showToast(result.errorMsg);
            }
        }
    }

    /***********
     * 网络任务请求
     *
     * @param method 请求的方法名
     * @param params 参数
     */
    public void doTask(String method, Object params) {

        if (isNetworkAvailable()) {
            TaskHttpRequest.shareInstance().doTask(method, params, this);
        } else {
            showToast("网络连接异常");
        }
    }


    /***
     * 显示Loading框
     ***/
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new LoadingDialog(this);
            progressDialog.setCancelable(true);
        }

        progressDialog.show();
    }

    /***
     * 关闭Loading框
     ***/
    public void dismissProgress() {

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /******
     * 显示提示框
     *
     * @param msg
     */
    public void showToast(String msg) {
        ToastUtil.showShortToast(this, msg);
    }

    /***********
     * 日志打印
     * 需要 设置自定义的 TAG 时，重写该方法就可以了
     *
     * @param msg
     */
    public void log(String msg) {
        LogUtil.d("TAG", msg);
    }

    /*******
     * 把像素转换成当前屏幕的dip值
     *
     * @param size 像素值
     * @return
     */
    public int pxToDip(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }
    /*******
     * 把dip转换成当前屏幕的像素值
     *
     * @param dp
     * @return
     */
    private int dipToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /************
     * 跳转
     *
     * @param cls
     */
    public void presentController(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);

        // 第一个参数：进入的
        // 第二个参数：退出的
        this.overridePendingTransition(R.anim.inside_translate, R.anim.outside_translate);
    }

    /************
     * 跳转
     *
     * @param cls
     * @param data
     */
    public void presentController(Class cls, Intent data) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(data);
        startActivity(intent);

        // 第一个参数：进入的
        // 第二个参数：退出的
        this.overridePendingTransition(R.anim.inside_translate, R.anim.outside_translate);
    }

    /************
     * 带返回结果的跳转
     *
     * @param cls
     * @param data
     * @param requestCode -1 表示不接收数据跳转
     */
    public void presentResultController(Class cls, Intent data, int requestCode) {
        Intent intent = new Intent(this, cls);
        if (data != null) {
            intent.putExtras(data);
        }
        if (requestCode == -1) {
            startActivity(intent);
        } else {
            startActivityForResult(intent, requestCode);
        }

        // 跳转方式类型标记
        isResultPresent = !isResultPresent;

        // 第一个参数：进入的
        // 第二个参数：退出的
        this.overridePendingTransition(R.anim.up_translate_enter, R.anim.activity_alpha_01);
    }


    // 判断网络是否可用
    public boolean isNetworkAvailable() {
        return ToolUtils.isNetworkAvailable(this.getApplicationContext());
    }

    /****
     * 拔打电话
     *
     * @param phone 电话
     */
    public void callPhone(String phone) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    /********
     * 发送短信
     *
     * @param phone 电话
     * @param msg   信息
     */
    public void sendSMS(String phone, String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto://" + phone));
        intent.putExtra("sms_body", msg);
        startActivity(intent);
    }

    /********
     * 发送邮件
     *
     * @param email
     * @param title
     * @param msg
     */
    public void sendEMail(String email, String title, String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_CC, "cc");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, "邮箱"));
    }

    // 退出应用
    public void exit() {

        // 关闭栈内所有的Activity
        for (Activity activity : TPApplication.shareInstance().actitiyManager) {
            activity.finish();
        }

        // 释放相关的引用资源，比如单例类
    }
}
