package cn.itcast.rss.rsslib4j;

public class RecommChannel {
	private String urlStr;
	private String name;
	
	public RecommChannel() {
		super();
	}
	public RecommChannel(String urlStr, String name) {
		this.urlStr = urlStr;
		this.name = name;
	}
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
