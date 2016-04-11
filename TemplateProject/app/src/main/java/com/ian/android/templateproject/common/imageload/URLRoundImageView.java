package com.ian.android.templateproject.common.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ian.android.templateproject.utils.LogUtil;


/***********
 *
 * @author Ian
 * @date 2015-12-16 11:06
 * @describe 加载一个URL图片
 *
 */
public class URLRoundImageView extends RoundImageView implements UrlLoaderListener{

    public URLRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public URLRoundImageView(Context context) {
        super(context);
    }

    /***********
     * 加载一个URL图片
     * @param url
     * @param resId
     */
    public void loadURL(String url, int resId){

        this.setImageResource(resId);

        // 开始下载图片
        DownloadImageUtil.shareInstance().download(url, this);
    }

    /*******
     * 更新请求的url
     * @param url
     * @param bitmap
     */
    @Override
    public void onLoader(String url, Bitmap bitmap) {
        LogUtil.d("LogadImages", "显示下载的图片->" + url);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        this.setImageDrawable(drawable);
    }
}
