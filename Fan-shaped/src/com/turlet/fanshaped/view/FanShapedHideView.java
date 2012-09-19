package com.turlet.fanshaped.view;

import com.turlet.fanshaped.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 
 * @ClassName: FanShapedHideView
 * @Description: FanShaped隐藏时显示该控件
 * @author <a href="mailto:yfldyxl@163.com">yfldyxl@163.com</a>
 * @date 2011-12-24 下午5:40:42
 * @version V1.0
 */
public class FanShapedHideView extends View {
	private Paint paint;
	
	private Context context;
	private Bitmap circleBitmap;
	private Bitmap currentBitmap;
//	private Bitmap hideBitmap;
	private Rect bgRect;
	private Rect currentRect;
	private static int screenWidth = -1;
	private static int screenHeight = -1;
	private boolean initPass;
	private ShowOrHideAction showOrHideAction = ShowOrHideAction.no;

	public FanShapedHideView(Context context) {
		super(context);
		init(context);
	}

	public FanShapedHideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		initPass = true;
		showOrHideAction = ShowOrHideAction.hide;

		Resources resources = context.getResources();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleBitmap = BitmapFactory.decodeResource(resources, com.turlet.fanshaped.R.drawable.circle);
//		showBitmap = BitmapFactory.decodeResource(resources, R.drawable.show_goto);
//		hideBitmap = BitmapFactory.decodeResource(resources, R.drawable.hide_goto);

	}

	private void initOnDraw(boolean initPass) {
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
		screenWidth = screenWidth < screenHeight ? screenWidth : screenHeight;

		bgRect = new Rect((int)(0.05f *screenWidth), (int)(0.0f *screenWidth), (int)(0.9f *screenWidth), (int)(0.85f *screenWidth));
		currentRect = new Rect((int)(0.18f *screenWidth), (int)(0.05f *screenWidth), (int)(0.78f *screenWidth), (int)(0.70f *screenWidth));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initOnDraw(initPass);
		canvas.drawBitmap(circleBitmap, null, bgRect, null);
		if (currentBitmap != null) {
			canvas.drawBitmap(currentBitmap, null, currentRect, null);
			
		}
//		if (showOrHideAction == ShowOrHideAction.show) {
//			canvas.drawBitmap(showBitmap, null, bgRect, paint);
//		} else if (showOrHideAction == ShowOrHideAction.hide) {
//			canvas.drawBitmap(hideBitmap, null, bgRect, paint);
//		}
		
		initPass = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
//		int x = (int) event.getX();
//		int y = (int) event.getY();
		if (MotionEvent.ACTION_DOWN == action) {
			// refreshView();
			return this.performClick();
		}
		return true;
	}

	private Handler handler = new Handler();

	protected void refreshView() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		});
	}

	public void setCurrentModule(Bitmap bitmap){
		currentBitmap = bitmap;
	}
	
	public void show(final View view) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Animation scaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in);
				view.startAnimation(scaleIn);
				view.setVisibility(VISIBLE);
				showOrHideAction = ShowOrHideAction.hide;
				invalidate();
			}
		});
	}

	public void setShowAction(ShowOrHideAction action) {
		showOrHideAction = ShowOrHideAction.show;
	}

	public void recycle() {
		circleBitmap.recycle();
//		hideBitmap.recycle();
		circleBitmap = null;
//		hideBitmap = null;
	}

	public enum ShowOrHideAction {
		no, show, hide;
	}
}
