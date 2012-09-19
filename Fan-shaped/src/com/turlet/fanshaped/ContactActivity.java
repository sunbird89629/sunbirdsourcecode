package com.turlet.fanshaped;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.turlet.fanshaped.view.Rotate3d;

/**
 * 
 * @ClassName: ContactActivity
 * @Description: TODO
 * @author <a href="mailto:yfldyxl@163.com">yfldyxl@163.com</a>
 * @date 2011-12-17 下午4:45:02
 * @version V1.0
 */
public class ContactActivity extends Activity {

	private int mCenterX = 0;
	private int mCenterY = 0;
	private ViewGroup leftLayout;
	private ViewGroup rightLayout;
	public static final String ACTION_3D = "action_3d";
	private RoateRecever roateRecever;
	private ViewGroup currentLayout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCenterX = getWindowManager().getDefaultDisplay().getWidth()/2;
		setContentView(R.layout.contact);
		leftLayout = (ViewGroup) findViewById(R.id.contact_layout);
		currentLayout = leftLayout;
		registerReceiver();
	}

	


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (roateRecever!=null) {
			unregisterReceiver(roateRecever);
			roateRecever =null;
		}
	}


	public void jumpToRight(Rotate3d rightAnimation,String module) {
		if("contact".equals(module)){
			setContentView(R.layout.contact);
			rightLayout = (ViewGroup) findViewById(R.id.contact_layout);
		}
		else if ("im".equals(module)) {
			setContentView(R.layout.im);
			rightLayout = (ViewGroup) findViewById(R.id.im_layout);
		}
		else if ("call".equals(module)) {
			setContentView(R.layout.call);
			rightLayout = (ViewGroup) findViewById(R.id.call_layout);
		}
		else if ("message".equals(module)) {
			setContentView(R.layout.message);
			rightLayout = (ViewGroup) findViewById(R.id.message_layout);
		}
		else if ("usage".equals(module)) {
			setContentView(R.layout.usage);
			rightLayout = (ViewGroup) findViewById(R.id.usage_layout);
		}
		else if ("money".equals(module)) {
			setContentView(R.layout.money);
			rightLayout = (ViewGroup) findViewById(R.id.money_layout);
		}
		else if ("sns".equals(module)) {
			setContentView(R.layout.sns);
			rightLayout = (ViewGroup) findViewById(R.id.sns_layout);
		}
		else if ("more".equals(module)) {
			setContentView(R.layout.more);
			rightLayout = (ViewGroup) findViewById(R.id.more_layout);
		}
		currentLayout = rightLayout;
//		rightLayout.startAnimation(rightAnimation);
	}

	public void leftMoveHandle(String module) {
		int mID = currentLayout.getId();
		String currentModule = matchModule(mID);
		if(module.equals(currentModule))return;
		Rotate3d leftAnimation = new Rotate3d(0, -90, 0, 0, mCenterX, mCenterY);
		Rotate3d rightAnimation = new Rotate3d(90, 0, 0.0f, 0.0f, mCenterX, mCenterY);

		leftAnimation.setFillAfter(true);
		leftAnimation.setDuration(800);
		rightAnimation.setFillAfter(true);
		rightAnimation.setDuration(800);

//		currentLayout.startAnimation(leftAnimation);
		jumpToRight(rightAnimation,module);
	}





	public void rightMoveHandle(String module) {
		int mID = currentLayout.getId();
		String currentModule = matchModule(mID);
		if(module.equals(currentModule))return;
		
		Rotate3d leftAnimation = new Rotate3d(-90, 0, 0, 0, mCenterX, mCenterY);
		Rotate3d rightAnimation = new Rotate3d(0, 90, 0.0f, 0.0f, mCenterX, mCenterY);

		leftAnimation.setFillAfter(true);
		leftAnimation.setDuration(800);
		rightAnimation.setFillAfter(true);
		rightAnimation.setDuration(800);

//		currentLayout.startAnimation(rightAnimation);
		jumpToLeft(leftAnimation,module);
	}
	
	public void jumpToLeft(Rotate3d leftAnimation, String module) {
		if("contact".equals(module)){
			setContentView(R.layout.contact);
			leftLayout = (ViewGroup) findViewById(R.id.contact_layout);
		}
		else if ("im".equals(module)) {
			setContentView(R.layout.im);
			leftLayout = (ViewGroup) findViewById(R.id.im_layout);
		}
		else if ("call".equals(module)) {
			setContentView(R.layout.call);
			leftLayout = (ViewGroup) findViewById(R.id.call_layout);
		}
		else if ("message".equals(module)) {
			setContentView(R.layout.message);
			leftLayout = (ViewGroup) findViewById(R.id.message_layout);
		}
		else if ("usage".equals(module)) {
			setContentView(R.layout.usage);
			leftLayout = (ViewGroup) findViewById(R.id.usage_layout);
		}
		else if ("money".equals(module)) {
			setContentView(R.layout.money);
			leftLayout = (ViewGroup) findViewById(R.id.money_layout);
		}
		else if ("sns".equals(module)) {
			setContentView(R.layout.sns);
			leftLayout = (ViewGroup) findViewById(R.id.sns_layout);
		}
		else if ("more".equals(module)) {
			setContentView(R.layout.more);
			leftLayout = (ViewGroup) findViewById(R.id.more_layout);
		}
		
		currentLayout = leftLayout;
//		leftLayout.startAnimation(leftAnimation);

	}
	
	private String matchModule(int mID) {
		String currentModule = null;
		switch (mID) {
		case R.id.contact_layout:
			currentModule = "contact";
			break;
		case R.id.im_layout:
			currentModule = "im";
			break;
		case R.id.call_layout:
			currentModule = "call";
			break;
		case R.id.message_layout:
			currentModule = "message";
			break;
		case R.id.usage_layout:
			currentModule = "usage";
			break;
		case R.id.money_layout:
			currentModule = "money";
			break;
		case R.id.sns_layout:
			currentModule = "sns";
			break;
		case R.id.more_layout:
			currentModule = "more";
			break;
		default:
			break;
		}
		return currentModule;
	}
	
	public class RoateRecever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("RoateRecever");
			int orientation = intent.getIntExtra("orientation", -1);
			String module = intent.getStringExtra("module");
			if (1 == orientation) {
				rightMoveHandle(module);
			}else if (2 == orientation) {
				leftMoveHandle(module);
			}
		}
		
	}
	
	/**
	 * 注册广播
	 */
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(ACTION_3D);
		roateRecever = new RoateRecever();
		registerReceiver(roateRecever, filter);
	}
}
