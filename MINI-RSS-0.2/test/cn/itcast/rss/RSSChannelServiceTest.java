package cn.itcast.rss;

import java.net.URL;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;
import cn.itcast.rss.db.RSSChannelService;
import cn.itcast.rss.rsslib4j.RSSChannel;
import cn.itcast.rss.rsslib4j.RSSHandler;
import cn.itcast.rss.rsslib4j.RSSParser;

public class RSSChannelServiceTest extends AndroidTestCase {
	public static final String TAG = "RSSChannelServiceTest";

	public void testAdd() {

		try {
			String path="http://feed.feedsky.com/lengxiaohua";
			RSSHandler handler = new RSSHandler();
			RSSParser.parseXmlFile(new URL(path),handler, false);
			
			handler.getRSSChannel().setChannelUrl(path);
			RSSChannelService service = new RSSChannelService(this.getContext());
			System.out.println(handler.getRSSChannel());
			service.save(handler.getRSSChannel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetAll() {
			RSSChannelService cService = new RSSChannelService(
					this.getContext());
			List<RSSChannel> cList = cService.getAll();
			for (RSSChannel c : cList) {
				Log.i(TAG, c.toString());
			}
	}
	
}
