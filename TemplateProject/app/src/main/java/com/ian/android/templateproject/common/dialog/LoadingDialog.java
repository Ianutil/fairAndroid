package com.ian.android.templateproject.common.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.ian.android.templateproject.R;


/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ Loading框
 *
 */
public class LoadingDialog extends ProgressDialog {

    public ImageView loading;

    public LoadingDialog(Context context){
        super(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_loading);

        loading = (ImageView)findViewById(R.id.img_loading);

        loading.startAnimation(initLoadingAnimation());
    }


    @Override
    protected void onStop() {
        super.onStop();

        loading.clearAnimation();
    }

    public  Animation initLoadingAnimation(){
//        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.loading_rotate);

    	Animation animation = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
//        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(RotateAnimation.INFINITE);

        return animation;
    }

}
