package cn.itcast.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import cn.itcast.rss.db.RSSChannelService;
import cn.itcast.rss.db.RSSItemService;
import cn.itcast.rss.rsslib4j.RSSChannel;
import cn.itcast.rss.rsslib4j.RSSException;
import cn.itcast.rss.rsslib4j.RSSHandler;
import cn.itcast.rss.rsslib4j.RSSItem;
import cn.itcast.rss.rsslib4j.RSSParser;

public class ContentUpdateService extends Service {

	private static final String TAG = "ContentUpdateService";
	private int flag = 0;

	private ServiceBinder binder = new ServiceBinder();
	private RSSChannelService channelService;
	private RSSItemService itemService;

	private ExecutorService threadPool = Executors.newCachedThreadPool();

	@Override
	public void onCreate() {
		super.onCreate();
		channelService = new RSSChannelService(this);
		itemService = new RSSItemService(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// if(flag==0){//表示服务时第一次创建。
		// // 启动服务的时候，将用户的订阅全部更新一遍。
		// Log.i(TAG, "服务启动了");
		// new Thread(new Runnable(){
		// @Override
		// public void run() {
		// List<RSSChannel> cList = channelService.getAll();
		// for (RSSChannel c : cList) {
		// updateChannel(c);
		// }
		// }
		// }).start();
		// flag=1;
		// }

	}

	private class ServiceBinder extends Binder implements
			ContentUpdateServiceBinder {

		@Override
		public void updateChannel(RSSChannel c) throws MalformedURLException, RSSException {
			updateChannelByChannel(c);
		}
		
		public void addChannel(String urlStr) throws MalformedURLException, RSSException {
			addChannelByUrl(urlStr);
		}
	}
	
	
	/**
	 * Service和Activity都执行在主线程中，Activity执行这个方法的时候是阻塞式的，这个方法刚好需要的事阻塞式的。
	 * 将异常全部抛到前台，前台可以根据产生的异常来判断究竟是那里出现的偏差，告诉用户怎样处理。
	 * 
	 * @param urlStr
	 * @throws RSSException 
	 * @throws MalformedURLException 
	 */
	
	private void addChannelByUrl(String urlStr) throws MalformedURLException, RSSException {
		RSSHandler handler = new RSSHandler();
		RSSParser.parseXmlFile(new URL(urlStr),handler,false);
		handler.getRSSChannel().setChannelUrl(urlStr);
		handler.getRSSChannel().setUpdateTime(System.currentTimeMillis());
		int id=channelService.saveAndReturnId(handler.getRSSChannel());
		LinkedList<RSSItem> iList = handler.getRSSChannel().getItems();
		for (RSSItem i : iList) {
			i.setChannelId(id);
		}
		itemService.batchSave(iList);
	}
	

	// 更新指定的RSSChannel
	/**
	 * @return num 更新的条数。
	 * @throws RSSException 
	 * @throws MalformedURLException 
	 */
	private int updateChannelByChannel(RSSChannel c) throws MalformedURLException, RSSException {
		RSSHandler handler = new RSSHandler();
		handler.setRSSChannel(c);
		RSSParser.parseXmlFile(new URL(c.getChannelUrl()), handler, false);
		handler.getRSSChannel().setUpdateTime(System.currentTimeMillis());
		channelService.update(c);
		LinkedList<RSSItem> iList = c.getItems();
		for (RSSItem i : iList) {
			i.setChannelId(c.getId());
		}
		itemService.deleteByCId(c.getId());
		itemService.batchSave(iList);
		return iList.size();
	}

	private void controlItemNum(RSSChannel c) {
		int storeNum = itemService.getItemNum(c.getId());
		if (storeNum + c.getItems().size() < c.getItemSotrage()) {
			return;
		}
		// 删除一些item--计算需要删除的项的总数。
		int deleNum = storeNum - (c.getItemSotrage() - c.getItems().size());
		itemService.deleteToSotre(c.getId(), deleNum);
	}
}
