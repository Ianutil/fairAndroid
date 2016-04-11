package com.ian.android.templateproject.controller.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.base.BaseActivity;
import com.ian.android.templateproject.common.clipview.utils.ImageUtils;
import com.ian.android.templateproject.common.clipview.utils.UIUtils;
import com.ian.android.templateproject.common.clipview.view.ClipView;
import com.ian.android.templateproject.utils.FileUtils;
import com.ian.android.templateproject.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/*********
 * @author Ian
 * @date 2016-01-14 09:28
 * @describ 修改头像
 * 实现思路：
 * 截取屏幕的截图，然后在该截图里截取矩形框里的区域
 */
public class ClipPhotoActivity extends BaseActivity {

    private String file;

    private ImageView srcPicture;
    private ClipView clipview;

    private int statusBarHeight = 0;
    private int titleBarHeight = 0;

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_clip_photo);

        file = this.getIntent().getStringExtra(TAG_FILE_URL);

        log("FILE->" + file);
        srcPicture = (ImageView) findViewById(R.id.img_view);

        Bitmap srcBitmap = ImageUtils.getScaledBitmap(file, UIUtils.getScreenWidth(), UIUtils.getScreenHeight());

        srcPicture.setImageBitmap(srcBitmap);
    }

    // 选择
    public void selectOnClick(View view) {
        // 获取截屏幕
        Bitmap clipedBitmap = getClipedBitmap();

        // 保存成一张图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        clipedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);

        // 保存图片
        saveBitmap(bitmap, new File(file).getName());
    }

    // 取消
    public void cancelOnClick(View view) {
        finish();
    }


    /**
     * *****
     * 保存图片
     *
     * @param bitmap
     * @param fileName
     */
    public void saveBitmap(Bitmap bitmap, String fileName) {
        File file = new File(FileUtils.getPicClipDir(), System.currentTimeMillis() + "_" + fileName);
        LogUtil.d("截图保存路径->"+file.getAbsolutePath());
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent data = new Intent();
        data.putExtra(TAG_CLIPED_URL, file.getAbsolutePath());
        setResult(RESULT_OK, data);
        finish();
    }


    /**
     * ******
     * 获取截取的图片
     *
     * @return
     */
    private Bitmap getClipedBitmap() {
        getBarHeight();
        Bitmap screenShoot = takeScreenShot();
        clipview = (ClipView) this.findViewById(R.id.clipview);
        // 切成一个指定大小的矩形
        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot, clipview.getTopX(), clipview.getTopY() + titleBarHeight + statusBarHeight, clipview.VIEW_WIDTH, clipview.VIEW_HEIGHT);

        return finalBitmap;
    }

    /**
     * ******
     * 获取状态栏的高度s
     */
    private void getBarHeight() {
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;

        int contenttop = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        titleBarHeight = contenttop - statusBarHeight;

    }

    // 截取屏幕，绘制成一张图片
    private Bitmap takeScreenShot() {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
}
