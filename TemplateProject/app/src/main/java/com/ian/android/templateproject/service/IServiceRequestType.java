package com.ian.android.templateproject.service;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 网络请求URL配置
 *
 */
public interface IServiceRequestType {

    // 测试服务地址
//    public String HOST_NAME = "http://172.16.192.59:8090/ehrapp";
    public String HOST_NAME = "http://www.j1health.com/api.php/";

    //******************* 登陆 模块 *******************
    public String URL_LOGIN = HOST_NAME + "/login/login.api"; // 登陆

    public String URL_PRODUCT = "http://18616263331cg.6655.la:11277/service/product/getProductInfo/0";
//    public String URL_PRODUCT = HOST_NAME+"home/HealthAPPUpdateRequest";

}
