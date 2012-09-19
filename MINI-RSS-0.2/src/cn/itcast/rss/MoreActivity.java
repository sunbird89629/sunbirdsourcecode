package cn.itcast.rss;

import cn.itcast.rss.util.MainUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class MoreActivity extends Activity {
	
	private WebView wvInfo;
	
	private ImageButton ibBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.more);
		MainUtil.initConfig(this,R.id.more_ll);
		wvInfo=(WebView) this.findViewById(R.id.more_wv_info);
		wvInfo.setWebViewClient(new WebViewClient() {});
		ibBack=(ImageButton) this.findViewById(R.id.more_btn_back);
		ibBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MoreActivity.this.finish();
			}
		});
		
		Intent oldIntent=this.getIntent();
		String baiduLink=oldIntent.getStringExtra("baiduLink");
		wvInfo.loadUrl(baiduLink);
	}
	
}
