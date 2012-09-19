package cn.itcast.rss.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class MainUtil {
	/**
	 * 1.将配置文件的信息，如背景桌面信息等内容读到G类中
	 * 2.读取屏幕的分辨率信息，方到G类中
	 * 一般在第一个启动的Activity的onCreate方法的第一行代码中执行。
	 * @param context
	 */
	public static void initSoftConfig(Activity context){
		
	}
	
	/**
	 * initConfig在onCreate方法中调用，对Activity进行配置。
	 * @param context
	 * @param layoutId
	 */
	public static void initConfig(Activity context,int layoutId){
		if(G.BG_ID!=0){
			View layout=(View) context.findViewById(layoutId);
			layout.setBackgroundResource(G.BG_ID);
		}
	}
	
	/**
	 * 将dip值转换成px值
	 * @param context
	 * @param dipValue
	 */
	public static int dip2px(Context context,float dipValue){
		float density=context.getResources().getDisplayMetrics().density;
		return (int)(dipValue*density+0.5f);
	}
	
}
