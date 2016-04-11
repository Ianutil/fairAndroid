package com.ian.android.templateproject.common.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 圆形图片
 *
 */
public class RoundImageView extends ImageView {

	private Paint paint;
	private Bitmap bitmap;
	private int radius; // 半径
	private int circleColor; // 圆环边色
	private float circleSize; // 圆环粗细

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RoundImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		circleColor = 0xff999999;
		
		circleSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, this.getResources().getDisplayMetrics());

		paint = new Paint();
		/* 去锯齿 */
        paint.setAntiAlias(true);  
        paint.setFilterBitmap(true);  
        paint.setDither(true);  
        /* 设置paint的　style　为STROKE：空心 */  
        paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageResource(int resId) {
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}
		super.setImageResource(resId);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();

		if (width <= 0 || height <= 0) {
			return;
		}
		
		// 贴图
		if(bitmap == null){
			bitmap = ((BitmapDrawable)this.getDrawable()).getBitmap();
		}

		// 默认半径
		if (radius == 0) {
			radius = width < height ? width/2 : height/2;
			radius -= 4;
		}
				
		// 画图片
		canvas.save();
		bitmap = getCroppedRoundBitmap(bitmap, radius);
		int x = width/2 - bitmap.getWidth()/2;
		int y = height/2 - bitmap.getHeight()/2;
		canvas.drawBitmap(bitmap, x, y, null);
		canvas.restore();

		// 画圆
		paint.setColor(circleColor);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(circleSize);
		canvas.save();
		canvas.drawCircle(width / 2, height / 2, radius, paint);
		canvas.restore();
	}
	
	/** 
     * 获取裁剪后的圆形图片 
     * @param radius半径
	 * @param bmp 原图
     */ 
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) { 
        Bitmap scaledSrcBmp; // 缩放图
        Bitmap squareBitmap;  // 正方形图

        int diameter = radius * 2; 
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片 
        int bmpWidth = bmp.getWidth(); 
        int bmpHeight = bmp.getHeight(); 
        
        int squareWidth = 0, squareHeight = 0; 
        int x = 0, y = 0; 
        if (bmpHeight > bmpWidth) {// 高大于宽 
            squareWidth = squareHeight = bmpWidth; 
            x = 0; 
            y = (bmpHeight - bmpWidth) / 2; 
            // 截取正方形图片 
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight); 
        } else if (bmpHeight < bmpWidth) {// 宽大于高 
            squareWidth = squareHeight = bmpHeight; 
            x = (bmpWidth - bmpHeight) / 2; 
            y = 0; 
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,squareHeight); 
        } else { 
            squareBitmap = bmp; 
        } 
        
        
        // 是否需要进行缩放
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) { 
            // 创建一张指定大小的贴图
        		scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,diameter, true); 
        } else { 
            scaledSrcBmp = squareBitmap; 
        } 
        
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), 
                scaledSrcBmp.getHeight(),  
                Config.ARGB_8888); 
        Canvas canvas = new Canvas(output); 
   
        Paint paint = new Paint(); 
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight()); 
   
        paint.setAntiAlias(true); 
        paint.setFilterBitmap(true); 
        paint.setDither(true); 
        canvas.drawARGB(0, 0, 0, 0); 
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, 
                scaledSrcBmp.getHeight() / 2,  
                scaledSrcBmp.getWidth() / 2, 
                paint); 
        
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint); 
       
//        if (bmp != null) {
//			bmp.recycle();
//            bmp = null;
//		}

//		if (squareBitmap != null) {
//			squareBitmap.recycle();
//			squareBitmap = null;
//		}
//
//		if (scaledSrcBmp != null) {
//			scaledSrcBmp.recycle();
//			scaledSrcBmp = null;
//		}

        return output; 
    }

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
		invalidate();
	}

	public float getCircleSize() {
		return circleSize;
	}

	public void setCircleSize(float circleSize) {
		this.circleSize = circleSize;
		invalidate();
	} 
    
    
}
