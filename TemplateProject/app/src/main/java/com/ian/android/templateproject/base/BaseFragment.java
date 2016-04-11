package com.ian.android.templateproject.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ian.android.templateproject.common.IConstant;
import com.ian.android.templateproject.service.IDispatchResponseListener;
import com.ian.android.templateproject.service.Response;
import com.ian.android.templateproject.service.TaskHttpRequest;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 所有Fragment基类
 *
 */
public class BaseFragment extends Fragment implements IConstant, IDispatchResponseListener {

    protected BaseActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (BaseActivity)this.getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO 测试代码
        TaskHttpRequest.shareInstance().getHandler().listener = this;
    }

    /***
     * 显示Loading框
     ***/
    public void showProgress() {
        activity.showProgress();
    }

    /***
     * 显示Loading框
     ***/
    public void dismissProgress() {

        activity.dismissProgress();
    }


    //*************请求网络回调*********************
    @Override
    public void onSuccess(String method, Response result){

    }

    @Override
    public void onError(String method, Response result){

    }

    /***********
     * 网络任务请求
     * @param method 请求的方法名
     * @param params 参数
     */
    public void doTask(String method, Object params){

        TaskHttpRequest.shareInstance().doTask(method, params, this);
    }

    /******
     * 显示提示框
     * @param msg
     */
    public void showToast(String msg){
        activity.showToast(msg);
    }

    /***********
     * 日志打印
     * 需要 设置自定义的 TAG 时，重写该方法就可以了
     * @param msg
     */
    public void log(String msg){
        activity.log(msg);
    }

    /*******
     * 把像素转换成当前屏幕的dip值
     * @param size 像素值
     * @return
     */
    public int pxToDip(int size){
        return activity.pxToDip(size);
    }

    /************
     * 跳转
     * @param cls
     */
    public void presentController(Class cls){
        activity.presentController(cls);
    }
}
