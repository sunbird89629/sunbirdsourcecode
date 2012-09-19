package com.turlet.fanshaped;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.turlet.fanshaped.view.FanShapedHideView;
import com.turlet.fanshaped.view.FanShapedHideView.ShowOrHideAction;
import com.turlet.fanshaped.view.FanShapedView;

public class MainActivity extends ActivityGroup {
	private FanShapedView fanShapedView;
	private FanShapedHideView fanShapedHideView;
	private LinearLayout eachLayout;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        
        eachLayout = (LinearLayout) findViewById(R.id.each_layout);
        fanShapedView = (FanShapedView) findViewById(R.id.fanshaped);
        fanShapedHideView = (FanShapedHideView) findViewById(R.id.fanshapedhide);
        
        eachLayout.addView(getLocalActivityManager().startActivity("contact",
				new Intent(MainActivity.this, ContactActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
        
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        fanShapedView.startAnimation(scaleIn);
        setListener();
    }

	private void setListener() {
		fanShapedView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fanShapedView.isStartActivity()) {
					String module = fanShapedView.getFirstItem();
					Intent intent = new Intent();
					intent.setAction(ContactActivity.ACTION_3D);
					intent.putExtra("orientation", FanShapedView.orient);
					intent.putExtra("module", module);
					sendBroadcast(intent);
				}else{
					Animation scaleOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_out);
					fanShapedView.startAnimation(scaleOut);
					fanShapedView.setVisibility(View.INVISIBLE);
					fanShapedHideView.setShowAction(ShowOrHideAction.show);
					fanShapedHideView.setCurrentModule(fanShapedView.getFirstBitmap());
					fanShapedHideView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		fanShapedHideView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fanShapedHideView.show(fanShapedView);
				fanShapedHideView.setCurrentModule(null);
				fanShapedHideView.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		fanShapedView.recycle();
		fanShapedHideView.recycle();
		System.exit(0);
	}
}