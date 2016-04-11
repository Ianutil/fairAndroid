package com.ian.android.templateproject.service;

import android.os.Message;

import com.ian.android.templateproject.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 网络请求类
 *
 */
public class TaskHttpRequest{
	private static TaskHttpRequest instance;
	private ThreadPoolExecutor executor;
	private BlockingQueue<Runnable> workQueue;
    private ThreadFactory factory ;
    private ServiceMediator serviceMediator;
	private ServiceHandler handler;     // 通知更新UIhanler
	//此构造方法为私有，外部不能进行new 操作
	private TaskHttpRequest(){
		workQueue = new ArrayBlockingQueue(20);
		
		factory = new ThreadFactory() {
	        public Thread newThread(Runnable runnable) {
	            Thread t = new Thread(runnable);
	            return t;
	        }
	    };

        executor = new ThreadPoolExecutor(2, 20, 60L, TimeUnit.SECONDS, workQueue, factory);

		// 通知更新UIhanler
		handler = new ServiceHandler();
	}
	
	/*********
	 * 公开实例方法
	 * @return HttpUtils
	 */
	public static TaskHttpRequest shareInstance(){
		if (instance == null) {
			instance = new TaskHttpRequest();
		}
		return instance;
	}

	/************
	 * 网络服务请求
	 * @param method 请求服务名
	 * @param params 参数
	 * @param listener 回调监听
	 */
	public void doTask(final String method, final Object params, final IDispatchResponseListener listener){
		if (serviceMediator == null) {
			return;
		}

		if (handler == null){
			return;
		}

		// 接收者
		handler.listener = listener;

		// 请求接口名称
		handler.method = method;
		
		executor.execute(new Runnable() {
			private Response response;
			public void run() {				
				try {
					LogUtil.d("HTTP_CONNECT", "**********************************************");
					LogUtil.d("HTTP_CONNECT", "METHOD->" + method);

					// 方法
					Method methods = serviceMediator.methods.get(method);
					
					// 反射 执行指定方法
					if (params == null){
						response = (Response)methods.invoke(serviceMediator, new Object[]{});
					}else{
						response = (Response)methods.invoke(serviceMediator, new Object[]{params});
					}

					// "请求失败" 为缺省值,当response为空时为请求失败
					sendMessage(response, "请求失败");

					LogUtil.d("HTTP_CONNECT", "**********************************************");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					// 请求失败
					sendMessage(response, "请求失败");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					// 请求失败
					sendMessage(response, "请求失败");
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					// 请求失败
					sendMessage(response, "请求失败");
				} catch (Exception e) {
					e.printStackTrace();
					// 请求失败
					sendMessage(response, "请求失败");
				}
			}
		});
	}

	/******
	 * 发送请求信息
	 * @param response 响应结果
	 * @param errorMsg 请求错误异常信息
	 */
	public void sendMessage(Response response,String errorMsg){
		// 请求失败
		if (response == null){
			response = new Response();
			response.errorCode = 100;
			response.errorMsg = errorMsg;
			response.method = handler.method;
			handler.sendEmptyMessage(1);
		}else {

			response.method = handler.method;
			Message msg = handler.obtainMessage();
			msg.obj = response;

			// 返回成功
			if (response.errorCode == 0){
				// 返回成功
				msg.what = 0;

			}else {
				// 返回失败
				msg.what = 1;
			}

			handler.sendMessage(msg);
		}
	}

	public ServiceMediator getServiceMediator() {
		return serviceMediator;
	}

	public void setServiceMediator(ServiceMediator serviceMediator) {
		this.serviceMediator = serviceMediator;
	}

	public ServiceHandler getHandler() {
		return handler;
	}

	public void setHandler(ServiceHandler handler) {
		this.handler = handler;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}
}
