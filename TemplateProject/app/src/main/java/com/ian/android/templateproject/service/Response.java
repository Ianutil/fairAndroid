package com.ian.android.templateproject.service;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 网络响应
 *
 */
public class Response<T> {

	public int statusCode;  // 状态码
	public String errorMsg; // 异常信息
	public int errorCode; // 结果码 返回处理结果（比如：处理成功为0，失败为1）
	public T result; // 服务器返回的结果

	public String response; // 请求服务端响应的数据结果

	public String method;

	public Response(){
		errorCode = 1; // 默认为异常错误码
		errorMsg = "请求失败"; // 默认为
	}

	@Override
	public String toString() {
		return "Response{" +
				"result=" + result +
				", errorCode=" + errorCode +
				", errorMsg='" + errorMsg + '\'' +
				", response='" + response + '\'' +
				", method='" + method + '\'' +
				", statusCode=" + statusCode +
				'}';
	}
}
