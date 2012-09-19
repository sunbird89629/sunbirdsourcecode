package cn.itcast.rss;

import java.net.MalformedURLException;

import cn.itcast.rss.rsslib4j.RSSChannel;
import cn.itcast.rss.rsslib4j.RSSException;

public interface ContentUpdateServiceBinder {
	void updateChannel(RSSChannel c) throws MalformedURLException, RSSException;
	void addChannel(String urlStr) throws MalformedURLException, RSSException;
}
