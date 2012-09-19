package cn.itcast.rss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import cn.itcast.rss.rsslib4j.RSSException;
import cn.itcast.rss.rsslib4j.RSSParser;
import cn.itcast.rss.rsslib4j.RecommChannel;
import cn.itcast.rss.rsslib4j.RecommChannelHandler;
import cn.itcast.rss.rsslib4j.RecommGroup;
import cn.itcast.rss.util.MainUtil;
import cn.itcast.rss.util.NetUtils;

public class RecommActivity extends TabActivity {
	private TabHost mTabHost;
	
	private ListView channelListView=null;
	private ArrayAdapter<String> channelListAdapter = null;
	
	private List<RecommGroup> recommGroupList;
	
	private LayoutInflater inflater;
	
	private ProgressDialog progressDialog;
	
	private ContentUpdateServiceBinder binder;
	
	private RecommChannel curChannel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomm);
		
		
		MainUtil.initConfig(this,android.R.id.tabhost);
		progressDialog = new ProgressDialog(RecommActivity.this);
		inflater = LayoutInflater.from(this);
		
		initData();
		
		mTabHost=this.getTabHost();
		
		createTabs();
		
		Intent intent = new Intent(this,ContentUpdateService.class);
		
		bindService(intent, new ServiceConnection(){

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder=(ContentUpdateServiceBinder) service;
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
			
		}, BIND_AUTO_CREATE);
	}
	
	private void showAddChanelConfirmDialog(){
		new AlertDialog.Builder(RecommActivity.this).setIcon(
				android.R.drawable.btn_star).setTitle("添加Feed").setMessage(curChannel.getName())
				.setPositiveButton("确定",new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AddRecommChannelTask().execute(curChannel);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
		}).create().show();
	}
	
	//初始化数据。
	private void initData(){
		RecommChannelHandler handler=new RecommChannelHandler();
		try {
			initDateFromNetFile(handler);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				initDataFromAsset(handler);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RSSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		recommGroupList=handler.getRecommGroupList();
	}
	
	private void initDateFromNetFile(RecommChannelHandler handler) throws Exception{
			File file=this.getFileStreamPath("RssFeeds.xml");
			RSSParser.parseXmlInStream(file, handler, false);
	}
	
	private void initDataFromAsset(RecommChannelHandler handler) throws IOException, RSSException{
			InputStream inStream=this.getAssets().open("RssFeeds.xml");
			RSSParser.parseXmlInStream(inStream, handler, false);
	}
	
	//根据初试化得数据，创建Tab选项卡。
	private void createTabs() {
		for(int i=0;i<recommGroupList.size();i++){
			Drawable drawable = new BitmapDrawable(NetUtils.getHttpBitmap(recommGroupList.get(i).getUrlStr()+"/favicon.ico"));
			mTabHost.addTab(mTabHost.newTabSpec(String.valueOf(i)).
	                setIndicator(recommGroupList.get(i).getName(),drawable).
	                setContent(createChannelDetails()));
		}
        getTabHost().setCurrentTab(0);
    }
	
	private TabContentFactory createChannelDetails() {

		return new TabHost.TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				int position=Integer.valueOf(tag);
				View view=inflater.inflate(R.layout.recomm_group, null);
				ListView lv=(ListView) view.findViewById(R.id.ListView01);
				RecommChannelAdapter adapter=new RecommChannelAdapter(RecommActivity.this,recommGroupList.get(position));
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						int currentTab=mTabHost.getCurrentTab();
						curChannel=recommGroupList.get(currentTab).getChanByIndex(position);
						showAddChanelConfirmDialog();
					}
					
				});
				return view;
			}};
	}
	
	private class RecommChannelAdapter extends BaseAdapter{
		private Context context;
		private RecommGroup group;
		private LayoutInflater inflater;
		public RecommChannelAdapter(Context context,RecommGroup group){
			this.context=context;
			this.group=group;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return group.getChanList().size();
		}

		@Override
		public Object getItem(int position) {
			return group.getChanList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView=inflater.inflate(R.layout.recomm_channel, null);
			}
			TextView tv=(TextView) convertView.findViewById(R.id.recomm_channel_tv_name);
			tv.setText(group.getNameByPos(position));
			return convertView;
		}
		
	}
	
	class AddRecommChannelTask extends AsyncTask<RecommChannel, Void, Void>{
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(RecommChannel... params) {
			try {
				progressDialog.show();
				binder.addChannel(curChannel.getUrlStr());
				progressDialog.cancel();
			} catch (MalformedURLException e) {
				progressDialog.cancel();
				//TODO 异常处理，告诉用户出了什么样的问题
				e.printStackTrace();
			} catch (RSSException e) {
				progressDialog.cancel();
				//TODO 异常处理，告诉用户出了什么样的问题
				e.printStackTrace();
			}
			cancel(false);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.cancel();
		}
	}
}
