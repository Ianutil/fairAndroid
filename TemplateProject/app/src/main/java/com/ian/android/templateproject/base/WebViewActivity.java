package com.ian.android.templateproject.base;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ian.android.templateproject.R;
import com.ian.android.templateproject.entity.User;

import java.net.URLDecoder;
import java.net.URLEncoder;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 公共加载WEB页
 *
 */
public class WebViewActivity extends BaseActivity {

    private WebView webview;
    private ProgressBar progressBar;

    private String title;
    // 不设置默认为<动态显示标题栏>,设置false为不动态
    private boolean isShow; // 是否动态显示标题

    private String url; // 判断是否显示指定的URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        initView();
    }

    // 判断 是否显示拦截该URL地址
    protected boolean isIntercept(WebView view, String url) {
        log("isIntercept->URL:" + url);

        // 设置好定义好的拦截协议
        if (url.contains("wfy://")){

            String data = url.split("wfy://")[1];

            data = decodeURL(data);
            log("---->"+data);

            User info = new Gson().fromJson(data, User.class);

            // action 处理
//            if (info.invokeName.contentEquals("getBack")){
//                super.onBackPressed();
//            }//else if ()


            return true;
        }
        // 返回true时为拦截
        return false;
    }

    @Override
    public void backOnClick(View view) {
        if (webview.canGoBack()) {
            webview.goBack();
            return;
        }

        super.backOnClick(view);
    }

    @Override
    public void nextOnClick(View view) {
        super.nextOnClick(view);

        while (webview.canGoBack()) {
            webview.goBack();
        }
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        webview.clearCache(true); // 清理缓存
        super.onDestroy();
    }


    private void initView(){
        // 显示的标题
        title = getIntent().getStringExtra("Title");

        View layout = findViewById(R.id.layout_title);
        if (!TextUtils.isEmpty(title)){
            setBackTitle(title);
            layout.setVisibility(View.VISIBLE);

        }else {
            // 没有设置Title时，不显示title
            setBackTitle(R.string.app_name);
            layout.setVisibility(View.GONE);
        }

        // 不设置默认为<动态显示标题栏>,设置false为不动态
        isShow = getIntent().getBooleanExtra("isShow", true);

        // 加载进度条
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        webview = (WebView)findViewById(R.id.webview);
        webview.addJavascriptInterface(this, "demo");

        // 初始化WebViewSettings
        initWebSettings();

        // 判断是否显示指定的URL
        url = getIntent().getStringExtra("URL");
        if (!TextUtils.isEmpty(url)){
            webview.loadUrl(url);
        }else{
//        webview.loadUrl("http://blog.csdn.net/totogogo/article/details/7309256");
            webview.loadUrl("http://www.baidu.com");
        }

    }


    // 初始化WebViewSettings
    private void initWebSettings(){
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                // 忽略SSL验证
                handler.proceed();
//                handler.cancel();
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 进行拦截URL
                return isIntercept(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                title = view.getTitle();

                log("TITLE=" + title);

                if (isShow) {
                    // 更新新标题
                    updateTitle(title);
                }

            }
        });


        // 设置加载进度
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

//                log("Progress:" + newProgress);
                // 这里将textView换成你的progress来设置进度
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                }

                progressBar.setProgress(newProgress);
                progressBar.postInvalidate();
            }
        });
    }


    // 更新标题栏<标题名>
    private void updateTitle(String title){
        View view = findViewById(R.id.tv_title);
        ((TextView) view).setText(title);
    }


    /********
     * URL解码
     * @param data
     * @return
     */
    protected String decodeURL(String data){
        String result = null;
        try{
            result = URLDecoder.decode(data, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /*********
     * URL编码
     * @param data
     * @return
     */
    protected String encodeURL(String data){
        String result = null;
        try{
            result = URLEncoder.encode(data, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @JavascriptInterface
    public String getUserInfo(){

        return "1234";
    }
}
