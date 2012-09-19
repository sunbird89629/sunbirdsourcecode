package cn.itcast.rss.rsslib4j;

import java.util.ArrayList;
import java.util.List;

public class RecommGroup {
	private String urlStr;
	private String name;
	private int num;
	private List<RecommChannel> chanList=new ArrayList<RecommChannel>();
	
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
	public List<RecommChannel> getChanList() {
		return chanList;
	}
	public void setChanList(List<RecommChannel> chanList) {
		this.chanList = chanList;
	}
	
	public void addChan(RecommChannel r){
		chanList.add(r);
	}
	public RecommChannel getChanByIndex(int index){
		return chanList.get(index);
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	public String getNameByPos(int position){
		return chanList.get(position).getName();
	}
}
