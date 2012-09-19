package cn.itcast.rss.rsslib4j;

import java.net.URL;

import org.xml.sax.helpers.DefaultHandler;

public abstract class UrlRSSHandler extends DefaultHandler {
	abstract public URL getUrl();
}
