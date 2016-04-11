package com.ian.android.templateproject.controller.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.base.BaseActivity;
import com.ian.android.templateproject.common.dialog.ForceDialog;
import com.ian.android.templateproject.common.imageload.RoundImageView;
import com.ian.android.templateproject.common.imageload.URLImageView;
import com.ian.android.templateproject.service.Response;
import com.ian.android.templateproject.service.ServiceMediator;
import com.ian.android.templateproject.utils.FileUtils;

import java.io.File;
import java.util.HashMap;


/*********
 * @author Ian
 * @date 2016-01-14 09:28
 * @describe 程序主页
 */
public class HomeActivity extends BaseActivity {
    private String mCameraFilePath; // 拍照图片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        
        setBackTitle(R.string.app_name);

        URLImageView img = (URLImageView)findViewById(R.id.img_url);
        String url = "http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb3943da344a4a20a44623dca8.jpg";
        img.loadURL(url, R.drawable.icon_loading);
    }


    // 打开图片
    public void openImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    // 打开对话框
    public void openDiloag(View view){
        ForceDialog dialog = new ForceDialog(this, R.style.Dialog);
        dialog.show();
    }


    // 调用相机
    public void openCamera(View view){
        File file = FileUtils.getImageFile();

        mCameraFilePath = file.getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, 101);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 未完成请求
        if (resultCode != RESULT_OK) {
            log("**********未完成请求*********");

            return;
        }

        Uri result = (data == null ? null : data.getData());

        // 相机拍照状况处理
        switch (requestCode) {
            case 100: // 选择图片
                getFilePath(result);
                break;
            case 101: // 相机拍照回调
                File cameraFile = new File(mCameraFilePath);
                result = Uri.fromFile(cameraFile);
                // Broadcast to the media scanner that we have a new photo
                // so it will be added into the gallery for the user.
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));

                // 进行切图
                Intent intent = new Intent();
                intent.putExtra(TAG_FILE_URL, mCameraFilePath);
                presentResultController(ClipPhotoActivity.class, intent, 102);
                break;
            case 102: // 切图完成
                showPhoto(data);
                break;
        }

    }


    /************
     * 显示切图片
     * @param data
     */
    private void showPhoto(Intent data){

        String path = data.getStringExtra(TAG_CLIPED_URL);

        RoundImageView icon = (RoundImageView)findViewById(R.id.img_head);

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        icon.setImageDrawable(drawable);
        icon.postInvalidate();
    }

    /*********
     * 获取完整路径
     * @param uri
     */
    private void getFilePath(Uri uri){

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

        if(cursor!=null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCameraFilePath = cursor.getString(columnIndex);
        }else{
            mCameraFilePath = uri.toString();
        }

        cursor.close();
        cursor = null;
        System.gc();

        if(mCameraFilePath.startsWith("file://")){
            mCameraFilePath =  mCameraFilePath.substring(7);
        }

        // 进行切图
        Intent bundle = new Intent();
        bundle.putExtra(TAG_FILE_URL, mCameraFilePath);
        presentResultController(ClipPhotoActivity.class, bundle, 102);
    }

    @Override
    public void onSuccess(String method, Response result) {
        super.onSuccess(method, result);

        if (method.contentEquals(ServiceMediator.REQUEST_GET_FUND_INFO)){
            dismissProgress();
            showToast("接收数据成功");

            log("接收数据->" + result.toString());
            ForceDialog dialog = new ForceDialog(this, R.style.Dialog);
            dialog.show();

            return;
        }

    }

    @Override
    public void onError(String method, Response result) {
        super.onError(method, result);
    }


    public void requestData(View view){
        showProgress();
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", "0");
        params.put("pageNo", "0");
        params.put("pageSize", "8");
        doTask(ServiceMediator.REQUEST_GET_FUND_INFO, params);
    }
}
