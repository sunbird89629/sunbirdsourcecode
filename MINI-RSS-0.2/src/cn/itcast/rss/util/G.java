package cn.itcast.rss.util;

public class G {
	public static final int BG_ALPHA=150;
	public static final int SCAN_TYPE_CHANNEL=0;
	public static final int SCAN_TYPE_SEARCH=1;
	public static final int SEARCH_WAY_TEXT=0;
	public static final int SEARCH_WAY_TITLE=1;
	
	public static final int ADD_TYPE_FEED=1;
	public static final int ADD_TYPE_QQ_ZONE=2;

	/**
	 * 记录手机的联网情况，在主程序启动的时候，检测网络状况，然后对其赋值
	 * 
	 * null 程序检测网络状况时出错
	 * 0           当前手机没有接入任何网络
	 * 1           当前手机用GPRS接入网络
	 * 2           当前手机通过wifi接入网络
	 */
	public static Integer NET_STAT;
	
	
	/**
	 * 记录当前手机的平幕分辨率的宽和高，在主程序启动的时候，对其 进行赋值。
	 * 0          程序检测屏幕分辨率的时候出错
	 * 非0     屏幕的宽和高
	 * 
	 */
	public static int SCREEN_PIX_WIDTH;
	public static int SCREEN_PIX_HEGHT;
	
	
	/**
	 * RSS浏览界面中WebWiew对图片进行缩放是的宽度标准。在屏幕分辨率获得后，对其进行赋值。
	 * 0          程序检测屏幕分辨率的时候出错
	 * 非0     屏幕的宽和高
	 */
	public static int SCAN_WEBVIEW_IMG_WITH;
	
	
	public static final String RECOMM_FEEDS_LINK="http://www.gokuai.com/w/z276vF11H746juZ4/RssFeeds.mp3";
	
	public static final String MAIN_CONFIG_SP_NAME="mainConfig";
	public static final String BG_SP_MANE="mainBg";
	
	
	public static  int BG_ID;
	public static final int SET_BG_REAUEST_CODE=1;
	public static final int SET_BG_OK_RESULT_CODE=10;
}
