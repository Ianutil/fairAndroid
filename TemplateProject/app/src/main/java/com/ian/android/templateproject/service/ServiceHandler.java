package com.ian.android.templateproject.service;

import android.os.Handler;
import android.os.Message;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 线程通信中，通知数据请求完成
 *
 */
public class ServiceHandler extends Handler {

    public IDispatchResponseListener listener;
    public Response response; // 响应结果
    public String method;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        // 响应结果
        response = (Response)msg.obj;

        switch (msg.what){
            case 0: // 成功

                // 当接收者为空时，自动取消，不再向下发放通知
                if (listener != null){
                    listener.onSuccess(method, response);
                }
                break;
            default: // 失败

                // 当接收者为空时，自动取消，不再向下发放通知
                if (listener != null){
                    listener.onError(method, response);
                }
                break;
        }

    }
}
