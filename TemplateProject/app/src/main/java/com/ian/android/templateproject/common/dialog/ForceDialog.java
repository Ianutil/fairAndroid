package com.ian.android.templateproject.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ian.android.templateproject.R;
import com.ian.android.templateproject.entity.ForceGroupInfo;
import com.ian.android.templateproject.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/***********
 *
 * @author Ian
 * @date 2016-12-14 11:31
 * @describ 加入圈子 弹窗
 *
 */
public class ForceDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener{

    private GridView gridView;

    private List<ForceGroupInfo> forceCommune;
    private List<ForceGroupInfo> datas;
    private ForceDialogAdapter adapter;
    private int start = 0;

    public ForceDialog(Context context, int style) {
        super(context, style);
        Window window = getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();
        window.setGravity(Gravity.CENTER);

        Rect rect = new Rect();
        View view1 = window.getDecorView();
        view1.getWindowVisibleDisplayFrame(rect);
//        windowparams.height = ToolUtils.dip2px(context, 155);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        windowparams.width = dm.widthPixels;
        windowparams.height = dm.heightPixels;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes((android.view.WindowManager.LayoutParams) windowparams);

        this.setCancelable(true);
    }

    public ForceDialog(Context context){
        this(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为true点击区域外消失
        this.setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_force);

        forceCommune = new ArrayList<>();

        initData();

        gridView = (GridView)findViewById(R.id.gridview);
        adapter = new ForceDialogAdapter(getContext(),datas);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        // 换一批
        View btn = findViewById(R.id.refreshCommune);
        btn.setOnClickListener(this);

        // 加入圈子
        btn = findViewById(R.id.foceCommune);
        btn.setOnClickListener(this);

        // 关闭窗口
        btn = findViewById(R.id.closeDialog);
        btn.setOnClickListener(this);

        btn = findViewById(R.id.dialog);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_inside_01);
        btn.startAnimation(animation);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ForceGroupInfo info = (ForceGroupInfo)adapter.getItem(position);
        info.flag = !info.flag;

        if (info.flag){
            if (!forceCommune.contains(info)){
                forceCommune.add(info);
            }
        }else {
            forceCommune.remove(info);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.refreshCommune: // 换一批
                refreshCommune(view);
                break;
            case R.id.foceCommune: // 加入圈子
                foceCommune(view);
                break;
            case R.id.closeDialog: // 关闭窗口
                closeDialog(view);
                break;
        }
    }

    // 加入圈子
    public void foceCommune(View view){
        ToastUtil.showShortToast(getContext(), "加入圈子");
        dismiss();
    }

    // 换一批
    public void refreshCommune(View view){
        start += 9;
        initData();
        adapter.setDatas(datas);
    }

    // 关闭窗口
    public void closeDialog(View view){
        dismiss();
    }

    private void initData(){
        if (datas == null){
            datas = new ArrayList<>();
        }else{
            datas.clear();
        }

        int count = start + 9;
        for (int i = start; i < count; i++){
            ForceGroupInfo info = new ForceGroupInfo();

            info.resId = R.drawable.icon_loading;
            info.name = "张磊"+i;
//            info.url = "http://c.hiphotos.baidu.com/image/pic/item/58ee3d6d55fbb2fb3943da344a4a20a44623dca8.jpg";
            datas.add(info);
        }
    }
}
