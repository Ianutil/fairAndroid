package com.ian.android.templateproject.service;

/***********
 * 
 * @author Ian
 * @date 2015-12-14 11:31 
 * @describe 请求结果分发
 *
 */
public interface IDispatchResponseListener {

	void onSuccess(String method, Response result);
	
	void onError(String method, Response result);
}
