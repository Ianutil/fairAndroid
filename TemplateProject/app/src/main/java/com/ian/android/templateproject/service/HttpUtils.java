package com.ian.android.templateproject.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ian.android.templateproject.service.https.AllowAllHostnameVerifier;
import com.ian.android.templateproject.service.https.SimpleX509TrustManager;
import com.ian.android.templateproject.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/***********
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 网络请求
 */
public class HttpUtils {
    public static int CONNECTION_TIMEOUT = 5000; // 连接超时
    public static int READ_TIMEOUT = 5000; // 读取超时
    public static String KEY_MOBILE_PHONE = "Mobile-Phone"; // 自定义头信息的key值
    public static HttpHeader header; // 需要自定义传输的头信息

    public static final String TAG = "HTTP_CONNECT";

    /****************
     * 网络请求
     *
     * @param url       请求url地址
     * @param parameter 请求参数
     * @return 返回响应结果
     */
    public static Response requestPost(String url, String parameter) {

        Response response = new Response();
        HttpURLConnection con = null;
        InputStream in = null;
        OutputStream out = null;
        try {

            con = getHttpURLConnection(url);

            // 把参数传输到服务器
            if (!TextUtils.isEmpty(parameter)) {
                LogUtil.d(TAG, "PARAMETER->" + parameter);

                out = con.getOutputStream();
                byte[] buf = parameter.getBytes("UTF-8");
                out.write(buf, 0, buf.length);
                out.flush();
                out.close();
            }


            // 是否请求成功
            if (con.getResponseCode() == 200) {

                in = con.getInputStream();

                StringBuffer data = new StringBuffer();
                byte[] buf = new byte[1024];

                int length = -1;
                while ((length = in.read(buf)) != -1) {
                    // 读取到的结果
                    data.append(new String(buf, 0, length));
                }

                // 响应结果数据
                response.statusCode = 200;
                response.response = data.toString(); // 响应的结果
                LogUtil.d(TAG, "RESULT->" + response.response);

                in.close();
                in = null;
            }else {
                // 响应结果数据
                response.statusCode = con.getResponseCode();
                LogUtil.d(TAG, "RESULT->" + response.response);
                sendErrorMessage(response, "请求失败");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            sendErrorMessage(response, "请求失败");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sendErrorMessage(response, "请求失败");
        } catch (KeyManagementException e) {
            e.printStackTrace();
            sendErrorMessage(response, "请求失败");
        } catch (KeyStoreException e) {
            e.printStackTrace();
            sendErrorMessage(response, "请求失败");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(response, "连接超时");
        } finally {
            if (con != null) {
                con.disconnect();
                con = null;
            }
        }

        return response;
    }

    /**************
     * 获取一个HttpURLConnection 对象
     *
     * @param url url服务指向地址
     * @return HttpURLConnection 对象
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("URL 等于空");
        }

        LogUtil.d(TAG, "URL->" + url);

        URL address = new URL(url);

        HttpURLConnection con = (HttpURLConnection) address.openConnection();

        // HTTPS 请求
        if (con instanceof HttpsURLConnection) {
            SSLContext context = SSLContext.getInstance("TLS");

            // 传入 KeyStore 证书
            // 默认允许所有HTTPS请求
            context.init(new KeyManager[0], new TrustManager[]{new SimpleX509TrustManager((KeyStore) null)}, new SecureRandom());

            SSLSocketFactory socketFactory = context.getSocketFactory();

            ((HttpsURLConnection) con).setSSLSocketFactory(socketFactory);

            ((HttpsURLConnection) con).setHostnameVerifier(new AllowAllHostnameVerifier());
        }

        // 申明自己接收的编码方法
        con.setRequestProperty("Accept-Encoding", "*"); // 支持所有传输数据的类型
        con.setRequestProperty("Accept-Language", "zh-cn,zh"); // 语言 中文
        con.setRequestProperty("Accept-Charset", "utf-8"); // 编码方式
        con.setRequestProperty("Content-type", "application/json"); // 传输格式
        con.setRequestProperty("Connection", "close"); // 在完成本次请求的响应后，断开连接，不要等待本次连接的后续请求了

        // 设置自定义头信息
        if (header != null) {
            // 把头信息转换成 JSON
            String value = new Gson().toJson(header);
            con.setRequestProperty(KEY_MOBILE_PHONE, value);

            LogUtil.d(TAG, "HEADER->" + value);
        }

        con.setRequestMethod("POST"); // 请求方式
        con.setUseCaches(false); // 是否缓存
        con.setConnectTimeout(CONNECTION_TIMEOUT); // 连接超时
        con.setReadTimeout(READ_TIMEOUT); // 读取超时
        con.setDoOutput(true); // 支持输出
        con.setDoInput(true); // 支持输入

        return con;
    }

    /******
     * 发送请求信息
     *
     * @param response 响应结果
     * @param errorMsg
     */
    public static void sendErrorMessage(Response response, String errorMsg) {

        TaskHttpRequest.shareInstance().sendMessage(response, errorMsg);
    }


    /****
     * 从网络获取图片
     *
     * @param str_url  下载的url
     * @param file 存放的文件名
     * @return Bitmap
     */
    public static Bitmap downloadFromInternet(String str_url, File file) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(str_url);

            connection = (HttpURLConnection) url.openConnection();
            // 从网络上获取一个输入流
            InputStream is = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // 创建一个输出流
            FileOutputStream fos = new FileOutputStream(file);
            // 第一个参数：压缩格式
            // 第二个参数：压缩质量最小值为0，最大值100
            // 第三个参数：一个输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            // fos.close();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return null;
    }
}


