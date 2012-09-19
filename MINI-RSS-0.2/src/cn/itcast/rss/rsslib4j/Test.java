package cn.itcast.rss.rsslib4j;

import org.xml.sax.helpers.DefaultHandler;

public class Test {
	public static void main(String[] args) {
		try {
			RSSParser.parseXmlFile("http://news.baidu.com/n?cmd=1&class=civilnews&tn=rss", new RSSHandler(), false);
		} catch (RSSException e) {
			e.printStackTrace();
		}
	}
}
