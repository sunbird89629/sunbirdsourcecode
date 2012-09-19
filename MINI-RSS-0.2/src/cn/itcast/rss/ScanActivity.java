package cn.itcast.rss;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.itcast.rss.db.RSSChannelService;
import cn.itcast.rss.db.RSSItemService;
import cn.itcast.rss.rsslib4j.RSSChannel;
import cn.itcast.rss.rsslib4j.RSSException;
import cn.itcast.rss.rsslib4j.RSSHandler;
import cn.itcast.rss.rsslib4j.RSSItem;
import cn.itcast.rss.rsslib4j.RSSParser;
import cn.itcast.rss.util.G;
import cn.itcast.rss.util.MainUtil;
import cn.itcast.rss.util.NetUtils;

public class ScanActivity extends Activity {

	private RSSChannelService channelService;
	private RSSItemService itemService;
	private RSSChannel channel;
	private RSSItem curItem;
	private int curItemPosi;

	private ListView lvItems;
	private SlidingDrawer sdItems;
	private TextView tvITitle;
	private TextView tvIAP;
	private WebView wvIDesc;

	private ImageButton btnHome;
	private ImageButton btnPrev;
	private ImageButton btnSave;
	private ImageButton btnNext;
	private ImageButton btnCollect;

	private ImageView ivSDUnread;
	private ImageView ivSDHandel;
	private ImageView ivSDReaded;

	private ProgressDialog progressDialog;
	
	private PopupWindow nPopupWidow;
	private ViewFlipper nViewFlipper;

	private static String HTML_TEMPLATE = "<html><head><script language=\"JavaScript\">var toWidth=$WIDTH;window.onload=function(){var imgs=document.getElementsByTagName('img');for(i=0;i<imgs.length;i++){var img=imgs[i];img.width=toWidth;img.height=toWidth/img.width*img.height;}}</script></head><body>$CONTENT</body></html>";

	private LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.scan);
		MainUtil.initConfig(this, R.id.scan_fl);

//		Drawable drawable1=this.findViewById(R.id.scan_ll_top_test).getBackground();
//		Drawable drawable2=this.findViewById(R.id.scan_ll_middle_test).getBackground();
//		Drawable drawable3=this.findViewById(R.id.scan_ll_bottom).getBackground();
//		Drawable drawable4=this.getResources().getDrawable(R.drawable.scan_item_title);
//		Drawable drawable5=this.getResources().getDrawable(R.drawable.scan_item_bg);
		this.findViewById(R.id.scan_ll_top_test).getBackground().setAlpha(G.BG_ALPHA);
		this.findViewById(R.id.scan_ll_middle_test).getBackground().setAlpha(G.BG_ALPHA);
		this.findViewById(R.id.scan_ll_bottom).getBackground().setAlpha(G.BG_ALPHA);

		inflater=LayoutInflater.from(this);
		
		// 寻找界面中的控件。
		lvItems = (ListView) this.findViewById(R.id.scan_lv_items);
		sdItems = (SlidingDrawer) this.findViewById(R.id.scan_sd_items);
		tvITitle = (TextView) this.findViewById(R.id.scan_tv_ITitle);
		tvIAP = (TextView) this.findViewById(R.id.scan_tv_IAP);
		wvIDesc = (WebView) this.findViewById(R.id.scan_wv_IDesc);
		wvIDesc.setBackgroundColor(0);
		wvIDesc.setWebViewClient(new WebViewClient() {
		});

		ivSDUnread = (ImageView) this.findViewById(R.id.scan_iv_unread);
		ivSDHandel = (ImageView) this.findViewById(R.id.scan_iv_handle);
		ivSDReaded = (ImageView) this.findViewById(R.id.scan_iv_readed);

		View.OnClickListener vocl = new ScanIVOnClickListener();
		ivSDUnread.setOnClickListener(vocl);
		ivSDHandel.setOnClickListener(vocl);
		ivSDReaded.setOnClickListener(vocl);

		btnHome = (ImageButton) this.findViewById(R.id.scan_btn_home);
		btnPrev = (ImageButton) this.findViewById(R.id.scan_btn_prev);
		btnSave = (ImageButton) this.findViewById(R.id.scan_btn_more);
		btnNext = (ImageButton) this.findViewById(R.id.scan_btn_next);
		btnCollect = (ImageButton) this.findViewById(R.id.scan_btn_collect);

		sdItems.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				ivSDHandel.setImageResource(R.drawable.btn_close);
			}
		});
		sdItems.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				ivSDHandel.setImageResource(R.drawable.btn_open);
			}
		});
		sdItems.open();

		lvItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				curItem = channel.getItems().get(position);
				curItemPosi = position;
				showCurItemInfo();
				sdItems.close();
			}
		});

		// 对生命的成员变量进行初始化。
		channelService = new RSSChannelService(this);
		itemService = new RSSItemService(this);

		Intent intent = getIntent();
		int scanType = intent.getIntExtra("scanType", 0);
		if (scanType == 0) {
			int channelId = intent.getIntExtra("channelId", 0);
			loadChannelInfo(channelId);
		} else {
			String keyWord = intent.getStringExtra("keyWord");
			int searchWay = intent.getIntExtra("searchWay", 0);
			initSearchInfo(keyWord, searchWay);
		}
		curItem = channel.getItems().get(0);
		curItemPosi = 0;
		showCurItemInfo();

		lvItems.setAdapter(new ItemItemAdapter(this, channel.getItems()));

		ScanBtnOnClickListener sbocl = new ScanBtnOnClickListener();
		btnHome.setOnClickListener(sbocl);
		btnPrev.setOnClickListener(sbocl);
		btnSave.setOnClickListener(sbocl);
		btnNext.setOnClickListener(sbocl);
		btnCollect.setOnClickListener(sbocl);
	}

	/**
	 * 从网上更新当前channel的所有信息，将channel的信息更新，将item保存到数据库中。
	 */
	private void loadChannelInfo(int channelId) {
		channel = channelService.getById(channelId);
		LinkedList<RSSItem> iList = itemService.getListByCId(channelId);
		channel.setItems(iList);
	}

	private void initSearchInfo(String keyWord, int searchWay) {
		progressDialog = new ProgressDialog(ScanActivity.this);

		if (searchWay == G.SEARCH_WAY_TITLE) {
			keyWord = "title:" + keyWord;
		}
		String urlStr = "http://news.baidu.com/ns?word=---keyWord---&tn=newsrss&sr=0&cl=2&rn=20&ct=0";
		progressDialog.show();
		RSSHandler handler;
		try {
			String encodedKeyWord = URLEncoder.encode(keyWord, "gb2312");
			urlStr = urlStr.replace("---keyWord---", encodedKeyWord);
			handler = new RSSHandler();
			RSSParser.parseXmlFile(new URL(urlStr), handler, false);
			handler.getRSSChannel().setChannelUrl(urlStr);
			channel = handler.getRSSChannel();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RSSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			progressDialog.cancel();
		}
	}

	private void showCurItemInfo() {
		tvITitle.setText(curItem.getTitle());
		if (curItem.getPubDate() != null) {
			Date date = new Date();
			date.parse(curItem.getPubDate());
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm");
			tvIAP.setText(curItem.getAuthor() + "  " + format.format(date));
		} else {
			tvIAP.setText(curItem.getAuthor() + "  " + "匿名人士");
		}
		String data=HTML_TEMPLATE.replace("$WIDTH",String.valueOf(G.SCAN_WEBVIEW_IMG_WITH) ).replace("$CONTENT", curItem.getDescription());
		wvIDesc.loadDataWithBaseURL("http://www.google.com",
				curItem.getDescription(), "text/html", "utf-8", "");
		if (curItem.getReaded() == 0) {
			itemService.setItemReaded(curItem.getId());
		}
	}
	
	private void initChildMenu(){
		nViewFlipper = new ViewFlipper(this);
		nViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.child_menu_in));
		nViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.child_menu_out));
		LinearLayout nLayout=(LinearLayout) inflater.inflate(R.layout.child_menu, null);
		nViewFlipper.addView(nLayout);
		nViewFlipper.setFlipInterval(1000000);
		
		nPopupWidow = new PopupWindow(nViewFlipper, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		nPopupWidow.setFocusable(false);
	}

	private class ScanBtnOnClickListener implements ImageView.OnClickListener {

		@Override
		public void onClick(View v) {
			int vId = v.getId();
			switch (vId) {
			case R.id.scan_btn_home:
				homeClick(v);
				break;
			case R.id.scan_btn_prev:
				prevClick(v);
				break;
			case R.id.scan_btn_more:
				moreClick(v);
				break;
			case R.id.scan_btn_next:
				nextClick(v);
				break;
			case R.id.scan_btn_collect:
				collectClick(v);
				break;
			}
		}

		private void homeClick(View v) {
			Intent intent = new Intent(ScanActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		private void prevClick(View v) {
			if (curItemPosi <= 0) {
				Toast.makeText(ScanActivity.this, "当前已是第一项！",
						Toast.LENGTH_SHORT).show();
			} else {
				curItemPosi--;
				curItem = channel.getItems().get(curItemPosi);
				showCurItemInfo();
			}
		}

		private void moreClick(View v) {
			String link = curItem.getLink();
			String baiduLink = NetUtils.toBaiduPath(null, link);
			Intent intent = new Intent(ScanActivity.this, MoreActivity.class);
			intent.putExtra("baiduLink", baiduLink);
			startActivity(intent);
		}

		private void nextClick(View v) {
			if (curItemPosi >= channel.getItems().size() - 1) {
				Toast.makeText(ScanActivity.this, "当前已是最后一项！",
						Toast.LENGTH_SHORT).show();
			} else {
				curItemPosi++;
				curItem = channel.getItems().get(curItemPosi);
				showCurItemInfo();
			}
		}

		private void collectClick(View v) {
			if(nPopupWidow==null){
				initChildMenu();
			}
			if (nPopupWidow.isShowing()){
				nViewFlipper.stopFlipping();
				nPopupWidow.dismiss();
			}else {
				nPopupWidow.showAtLocation(findViewById(R.id.scan_ll),Gravity.BOTTOM, 0,  MainUtil.dip2px(ScanActivity.this, 50));
				nViewFlipper.startFlipping();// 播放动画
			}
		}
	}

	private class ScanIVOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int vId = v.getId();
			switch (vId) {
			case R.id.scan_iv_unread:
				onUnreadClick(v);
				break;
			case R.id.scan_iv_handle:
				onHandleClick(v);
				break;
			case R.id.scan_iv_readed:
				onReadedClick(v);
				break;
			}
		}

		private void onReadedClick(View v) {
			Toast.makeText(ScanActivity.this, String.valueOf(v.getId()),Toast.LENGTH_LONG).show();
		}

		private void onHandleClick(View v) {
			Toast.makeText(ScanActivity.this, String.valueOf(v.getId()),Toast.LENGTH_LONG).show();
		}

		private void onUnreadClick(View v) {
			Toast.makeText(ScanActivity.this, String.valueOf(v.getId()),Toast.LENGTH_LONG).show();
		}
	}

}
