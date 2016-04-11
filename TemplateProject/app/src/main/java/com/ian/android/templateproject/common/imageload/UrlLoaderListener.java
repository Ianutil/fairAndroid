package com.ian.android.templateproject.common.imageload;

import android.graphics.Bitmap;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 重缓冲的异步加载工具类
 *
 */
public interface UrlLoaderListener {

    public void onLoader(String url, Bitmap bitmap);
}
