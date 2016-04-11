package com.ian.android.templateproject.controller;

import android.os.Bundle;
import android.view.View;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.base.BaseActivity;
import com.ian.android.templateproject.controller.home.HomeActivity;
import com.ian.android.templateproject.service.Response;
import com.ian.android.templateproject.service.ServiceMediator;

import java.util.HashMap;


/**
 * ******
 *
 * @author Ian
 * @date 2016-01-14 09:28
 * @describe 程序主入口
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle(R.string.app_name);

        showResult("常浩");
        showResult(1,"abcd");
        showResult(2,2,3,4);
        showResult(3,"ABC",3,this);
    }

    @Override
    public void onSuccess(String method, Response result) {
        super.onSuccess(method, result);

        // 请求数据成功
        if (method.contentEquals(ServiceMediator.REQUEST_GET_FUND_INFO)) {
            dismissProgress();

            showToast("请求数据成功");

            presentController(HomeActivity.class);

            HashMap<String, String> params = new HashMap<>();
            params.put("userId", "0");
            params.put("token", "0");
            doTask(ServiceMediator.REQUEST_GET_FUND_INFO, params);

            return;
        }

    }

    private void showResult(Object... obj){
        log("**********************" + obj.getClass().getSimpleName() + " " + obj.length);
        Object tmp;
        for (int i = 0; i < obj.length; i++) {
            tmp = obj[i];
            log(tmp.getClass().getName());
            log(obj[i].toString());
        }
    }

    @Override
    public void onError(String method, Response result) {
        super.onError(method, result);
    }


    public void requestData(View view) {
        showProgress();
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", "0");
        params.put("token", "0");
        doTask(ServiceMediator.REQUEST_GET_FUND_INFO, params);
    }

}
