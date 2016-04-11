package com.ian.android.templateproject.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ian.android.templateproject.entity.User;
import com.ian.android.templateproject.utils.LogUtil;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

/***********
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 所有网络请求接口调用
 */
public class ServiceMediator implements IServiceRequestType {

    /***************
     * 登录模块
     ****************/
    public static final String REQUEST_LOGIN = "login"; // 登陆请求
    public static final String REQUEST_GET_FUND_INFO = "getProductInfo"; // 登陆请求

    public HashMap<String, Method> methods; // 存放网络请求方法名

    public ServiceMediator() {

        if (methods == null) {
            methods = new HashMap<String, Method>();
        }

        Method[] mds = this.getClass().getMethods();

        for (int i = 0; i < mds.length; ++i) {

            if (!methods.containsValue(mds[i])) {
                methods.put(mds[i].getName(), mds[i]);
            }
        }


    }

    /************
     * 登陆请求
     * {"loginName":"chankouv_0202@163.com","password":"58334475"}
     *
     * @param params
     * @return Response
     */
    public Response<User> login(User params) {

        Response<User> response = null;

        try {

            String json = null;
            if (params != null) {
                json = new Gson().toJson(params);
            }

            // 请求服务返回结果
            response = HttpUtils.requestPost(URL_LOGIN, json);

            // 请求成功
            if (response != null && response.statusCode == 200) {

                // 解析到指定结果
                Type type = new TypeToken<Response<User>>() {
                }.getType();

                Response<User> result = new Gson().fromJson(response.response, type);

                LogUtil.d("HTTP_CONNECT", "解析->" + result);

                response = result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 请求失败
            HttpUtils.sendErrorMessage(response, "解析数据异常");
        }

        return response;
    }


    /************
     * 登陆请求
     * {"token":"","userId":"0"}
     *
     * @param params
     * @param listener
     * @return
     */
    public Response getProductInfo(HashMap<String, Object> params) {

        Response response = null;

        try {

            JSONObject object = new JSONObject();

            if (params != null) {
                for (HashMap.Entry<String, Object> entry : params.entrySet()) {
                    object.put(entry.getKey(), entry.getValue());
                }
            }

            String json = object.toString();

            // 请求服务返回结果
            response = HttpUtils.requestPost(URL_PRODUCT, json);

            // 请求成功
            if (response != null && response.statusCode == 200) {

                // 解析到指定结果
                Type type = new TypeToken<Response>() {
                }.getType();

                Response<User> result = new Gson().fromJson(response.response, type);

                LogUtil.d("HTTP_CONNECT", "解析->" + result);

                response = result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 请求失败
            HttpUtils.sendErrorMessage(response, "请求失败");
        }

        return response;
    }

}
