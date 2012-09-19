package cn.itcast.rss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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

public class MainActivity extends Activity {

	private ListView lvChannels;
	private LayoutInflater inflater;
	private ProgressDialog progressDialog;
	
	private ContentUpdateServiceBinder binder;
	private RSSItemService itemService;
	private RSSChannelService channelService;

	private ImageButton btnConfig;
	private ImageButton btnBook;
	private ImageButton btnSearch;
	private ImageButton btnCollection;
	private ImageButton btnExit;

	private List<RSSChannel> cList;
	private RSSChannel curChannel;
	private int curPostion;
	
	private ProgressBar progressBar;
	
	private static Integer netAvailableValue=null;
	
	private ServiceConnection serviceConn;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getScreenResolution();
		getConfigBgId();
		MainUtil.initConfig(this, R.id.main_ll);
		if(netAvailableValue==null){
			netAvailableValue=isNetAvailable();
			if(netAvailableValue==0){
				showConnInfoDialog();
			}
		}
		this.findViewById(R.id.main_ll_menu).getBackground().setAlpha(G.BG_ALPHA);
		
		itemService=new RSSItemService(this);
		channelService=new RSSChannelService(this);
		Intent updateService = new Intent(this, ContentUpdateService.class);
		serviceConn=new ServiceConnection(){

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				MainActivity.this.binder=(ContentUpdateServiceBinder) service;				
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
			
		};
		bindService(updateService, serviceConn, BIND_AUTO_CREATE);
		
		lvChannels = (ListView) this.findViewById(R.id.main_lv_channels);
		
		inflater = LayoutInflater.from(this);
		progressDialog = new ProgressDialog(MainActivity.this);

		btnConfig = (ImageButton) this.findViewById(R.id.main_ibn_config);
		btnBook = (ImageButton) this.findViewById(R.id.main_ibn_book);
		btnSearch = (ImageButton) this.findViewById(R.id.main_ibn_search);
		btnCollection = (ImageButton) this.findViewById(R.id.main_ibn_collection);
		btnExit = (ImageButton) this.findViewById(R.id.main_ibn_exit);
		
		progressBar=(ProgressBar) this.findViewById(R.id.channel_item_pb_flush);
		
		lvChannels.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				curChannel = cList.get(position);
				int itemNum=itemService.getItemNumByChanId(curChannel.getId());
				if(itemNum>0){
					Intent intent = new Intent(MainActivity.this,ScanActivity.class);
					intent.putExtra("channelId", curChannel.getId());
					startActivity(intent);
				}else{
					Dialog dialog = new AlertDialog.Builder(MainActivity.this).setIcon(
							android.R.drawable.btn_star).setTitle("出错了").setMessage(
							"该频道下没有任何内容！").setPositiveButton("马上更新",
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new FlushChannelTask().execute(curChannel);
								}
							}).setNegativeButton("取    消", null).create();
							dialog.show();
				}
				
			}
		});
		
		MainBtnOnClickListener mbocl = new MainBtnOnClickListener();
		btnConfig.setOnClickListener(mbocl);
		btnBook.setOnClickListener(mbocl);
		btnSearch.setOnClickListener(mbocl);
		btnCollection.setOnClickListener(mbocl);
		btnExit.setOnClickListener(mbocl);
		
		new LoadRecommChannelTask().execute(null);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		MainUtil.initConfig(this,R.id.main_ll);
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			showCancelDialog();
		}
		return false;
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==G.SET_BG_REAUEST_CODE && resultCode==G.SET_BG_OK_RESULT_CODE){
			this.findViewById(R.id.main_ll).invalidate();
			MainUtil.initConfig(MainActivity.this, R.id.main_ll);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(binder!=null){
			unbindService(serviceConn);
		}
	}

	private int isNetAvailable(){
		int result;
		ConnectivityManager connManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=connManager.getActiveNetworkInfo();
		if(info!=null && info.isAvailable()){
			result=1;
		}else{
			result=0;
		}
		return result;
	}
	
	private void getScreenResolution(){
		G.SCREEN_PIX_WIDTH=getWindowManager().getDefaultDisplay().getWidth();
		G.SCREEN_PIX_HEGHT=getWindowManager().getDefaultDisplay().getHeight();
		G.SCAN_WEBVIEW_IMG_WITH=G.SCREEN_PIX_WIDTH-15;
	}

	private void initData() {
		cList = new RSSChannelService(this).getAll();
		if(cList.size()!=0){
			curChannel=cList.get(0);
		}
		ChannelItemAdapter adapter = new ChannelItemAdapter(this, cList);
		lvChannels.setAdapter(adapter);
	}
	
	private void getConfigBgId(){
		SharedPreferences sp=this.getSharedPreferences(G.MAIN_CONFIG_SP_NAME, Context.MODE_PRIVATE);
		G.BG_ID=sp.getInt(G.BG_SP_MANE, 0);
	}

	private void showCancelDialog(){
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("确认退出吗？");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				MainActivity.this.finish();
			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	private void showSearchDialog(){
		final EditText editText=new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle("请输入要搜索的关键字：")
				.setIcon(R.drawable.search_dialog_logo)
				.setView(editText)
				.setPositiveButton("全文搜索", new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String keyWord=editText.getText().toString();
						Intent intent=new Intent(MainActivity.this,ScanActivity.class);
						intent.putExtra("scanType", G.SCAN_TYPE_SEARCH);
						intent.putExtra("searchWay", G.SEARCH_WAY_TEXT);
						intent.putExtra("keyWord", keyWord);
						startActivity(intent);
					}
					
				})
				.setNegativeButton("标题搜索", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String keyWord=editText.getText().toString();
						Intent intent=new Intent(MainActivity.this,ScanActivity.class);
						intent.putExtra("scanType", G.SCAN_TYPE_SEARCH);
						intent.putExtra("searchWay", G.SEARCH_WAY_TITLE);
						intent.putExtra("keyWord", keyWord);
						startActivity(intent);
					}
				})
				.show();
	}
	private void showConnInfoDialog(){
		new AlertDialog.Builder(this)
		.setTitle("当前未链接网络！")
		.setIcon(R.drawable.netstat_logo)
		.setPositiveButton("继续浏览", new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
			
		})
		.setNegativeButton("退出应用", new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
			}
			
		})
		.show();
	}
	//删除某个订阅频道时显示的对话框。
	private void showDeleDialog(){
		new AlertDialog.Builder(this)
		.setTitle("删除频道")
		.setIcon(R.drawable.netstat_logo)
		.setMessage(curChannel.getTitle())
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				channelService.delete(curChannel.getId());
				itemService.deleteByCId(curChannel.getId());
				initData();
				lvChannels.invalidate();
			}
			
		})
		.setNegativeButton("取消",null)
		.show();
	}
	
	private void showAddChannelDialog(){
		BookItemAdapter adapter=new BookItemAdapter(MainActivity.this);
		adapter.addItem(R.drawable.add_item_rssfeed,R.string.book_news);
		adapter.addItem(R.drawable.add_item_qqzone2,R.string.book_qqzone);
		adapter.addItem(R.drawable.add_item_recom,R.string.book_reco);
		AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("添加新的项目");
		builder.setAdapter(adapter,new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					onItem0Click(dialog,which);
					break;
				case 1:
					onItem1Click(dialog,which);
					break;
				case 2:
					onItem2Click(dialog,which);
					break;
				}
			}

			private void onItem0Click(DialogInterface dialogInterface, int which) {
				showAddRssFeedDialog();
			}

			private void onItem1Click(DialogInterface dialogInterface, int which) {
				showAddQQZoneDialog();
			}

			private void onItem2Click(DialogInterface dialogInterface, int which) {
				Intent intent=new Intent(MainActivity.this,RecommActivity.class);
				startActivity(intent);
			}
		});
		builder.create().show();
	}
	
	private void showAddRssFeedDialog(){
		final EditText editText=new EditText(this);
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
		.setIcon(android.R.drawable.btn_star)
		.setTitle("添加Feed")
		.setView(editText)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			String urlStr=editText.getText().toString();
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AddChannelTask().execute(urlStr);
			}
		}).setNegativeButton("取消",null).create();
		dialog.show();
	}
	
	private void showAddQQZoneDialog(){
		final EditText editText=new EditText(this);
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
		.setIcon(android.R.drawable.btn_star)
		.setTitle("添加Feed")
		.setView(editText)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String urlStr=editText.getText().toString();
				urlStr="http://feeds.qzone.qq.com/cgi-bin/cgi_rss_out?uin="+urlStr;
				new AddChannelTask().execute(urlStr);
			}
		}).setNegativeButton("取消",null).create();
		dialog.show();
	}
	
	private void showConfigMenuDialog(){
		
	}

	private class MainBtnOnClickListener implements ImageView.OnClickListener {

		@Override
		public void onClick(View v) {
			int vId = v.getId();
			switch (vId) {
			case R.id.main_ibn_config:
				configClick(v);
				break;
			case R.id.main_ibn_book:
				bookClick(v);
				break;
			case R.id.main_ibn_search:
				searchClick(v);
				break;
			case R.id.main_ibn_collection:
				collectionClick(v);
				break;
			case R.id.main_ibn_exit:
				exitClick(v);
				break;
			}
		}

		private void configClick(View v) {
			Intent intent=new Intent(MainActivity.this,SetBgActivity.class);
			startActivityForResult(intent, 1);
		}

		private void bookClick(View v) {
			showAddChannelDialog();
		}

		private void searchClick(View v) {
			showSearchDialog();
		}

		private void collectionClick(View v) {
			Toast.makeText(MainActivity.this, v.getId(), Toast.LENGTH_SHORT).show();
		}

		private void exitClick(View v) {
			showCancelDialog();
		}
	}
	
	class LoadRecommChannelTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				InputStream inStream=NetUtils.getInStreamByUrl(G.RECOMM_FEEDS_LINK);
				if(inStream!=null){
					FileOutputStream fOutStream=MainActivity.this.openFileOutput("RssFeeds.xml", 1);
					NetUtils.writeInStream2OutStream(inStream, fOutStream);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	class AddChannelTask extends AsyncTask<String,Void,Void>{
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			RSSHandler handler = new RSSHandler();
			try {
				RSSParser.parseXmlFile(new URL(params[0]),handler,false);
				handler.getRSSChannel().setChannelUrl(params[0]);
				channelService.save(handler.getRSSChannel());
				progressDialog.cancel();
			} catch (MalformedURLException e) {
			} catch (RSSException e) {
				e.printStackTrace();
			}
			cancel(false);
			return null;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			progressDialog.cancel();
		}
		
	}
	
	class FlushChannelTask extends AsyncTask<RSSChannel, Void, Void> {
		private ProgressBar pbFlush;
		private ImageView ivFlush;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			LinearLayout linearLayout=(LinearLayout) lvChannels.getChildAt(curPostion);
			pbFlush=(ProgressBar) linearLayout.findViewById(R.id.channel_item_pb_flush);
			pbFlush.setVisibility(View.VISIBLE);
			ivFlush=(ImageView) linearLayout.findViewById(R.id.channel_item_iv_flush);
			ivFlush.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(RSSChannel... params) {
			RSSChannel c=params[0];
			RSSHandler handler = new RSSHandler();
			handler.setRSSChannel(c);
			try {
				RSSParser.parseXmlFile(new URL(c.getChannelUrl()), handler, false);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RSSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channelService.update(c);
			LinkedList<RSSItem> iList = c.getItems();
			for (RSSItem i : iList) {
				i.setChannelId(c.getId());
			}
			itemService.deleteByCId(c.getId());
			itemService.batchSave(iList);
			cancel(false);
			return null;
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(pbFlush!=null){
				pbFlush.setVisibility(View.INVISIBLE);
			}
			if(ivFlush!=null){
				ivFlush.setVisibility(View.VISIBLE);
			}
			
			initData();
			lvChannels.invalidate();
		}
	} 
	
	public class ChannelItemAdapter extends BaseAdapter {

		public static final String TAG = "ChannelItemAdapter";// TODO delete

		private Context context;
		private List<RSSChannel> cList;

		private LayoutInflater inflater;

		public ChannelItemAdapter(Context context, List<RSSChannel> cList) {
			this.context = context;
			this.cList = cList;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return cList.size();
		}

		@Override
		public Object getItem(int position) {
			return cList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RSSChannel c = cList.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.channel_item, null);
				convertView.getBackground().setAlpha(G.BG_ALPHA);
			}

			ImageView ivCLogo = (ImageView) convertView.findViewById(R.id.channel_item_iv_channel_logo);
			Bitmap bm = this.getChannelLogo(c);
			ivCLogo.setImageBitmap(bm);

			TextView tvCase = null;

			tvCase = (TextView) convertView.findViewById(R.id.channel_item_tv_title);
			tvCase.setText(c.getTitle());

			tvCase = (TextView) convertView.findViewById(R.id.channel_item_tv_updateTime);
			Date date=new Date(c.getUpdateTime());
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd hh:mm");
			String updateTimeStr=dateFormat.format(date);
			tvCase.setText("更新时间:"+updateTimeStr);

			tvCase = (TextView) convertView.findViewById(R.id.channel_item_tv_count);			
			int total=itemService.getItemNumByChanId(c.getId());
			int unread=itemService.getUnreadItemNumByChanId(c.getId());
			
			tvCase.setText(String.valueOf(unread)+"/"+String.valueOf(total));
			ImageView ivCase;
			View.OnClickListener ivOCL=new ImageViewOCL(position);
			ivCase=(ImageView) convertView.findViewById(R.id.channel_item_iv_flush);
			ivCase.setOnClickListener(ivOCL);
			ivCase=(ImageView) convertView.findViewById(R.id.channel_item_iv_delete);
			ivCase.setOnClickListener(ivOCL);

			return convertView;
		}

		private Bitmap getChannelLogo(RSSChannel c) {
			Bitmap result = null;
			String imgUrl = c.getRSSImage().getUrl();
			if (imgUrl == null || "".equals(imgUrl.trim())) {// 返回默认的图片
				result = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.channel_logo_def);
			} else {// 在图片文件夹中寻找，或到网上去下载。
					// 获得文件夹得路径
				String dir = context.getFilesDir().getAbsolutePath()+ "/channellogo";
				File dirFile = new File(dir);
				if (!dirFile.exists()) {
					dirFile.mkdir();
				}
				String filename = NetUtils.parseImgUrl2Name(imgUrl);
				File logoFile = new File(dir, filename);
				if (logoFile.exists()) {// 如果本地存在，就使用本地的文件。
					result = loadBitmapByFile(logoFile);
				} else {// 本地不存在，到网上寻找。然后保存到本地。
					NetUtils.saveNetImg2File(imgUrl, logoFile);
					result = loadBitmapByFile(logoFile);
				}
			}
			return result;
		}

		private Bitmap loadBitmapByFile(File file) {
			Bitmap bm = null;
			bm = BitmapFactory.decodeFile(file.getAbsolutePath());
			return bm;
		}

		// 获得圆角图片的方法
		public  Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}

	}
	
	private class ImageViewOCL implements View.OnClickListener{
		int position;
		public ImageViewOCL(int position){
			this.position=position;
		}

		@Override
		public void onClick(View v) {
			int vId=v.getId();
			curChannel=cList.get(position);
			curPostion=position;
			switch (vId) {
			case R.id.channel_item_iv_delete:
				showDeleDialog();
				break;
			case R.id.channel_item_iv_flush:
				new FlushChannelTask().execute(curChannel);
				break;
			}
		}
	}
	
	
	
	
}