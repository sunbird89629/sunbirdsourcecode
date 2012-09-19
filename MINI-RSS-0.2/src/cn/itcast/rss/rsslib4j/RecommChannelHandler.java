package cn.itcast.rss.rsslib4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import cn.itcast.rss.util.G;


public class RecommChannelHandler extends UrlRSSHandler {
	
	private String cur_tag;
	private List<RecommGroup> recommGroupList;
	private RecommGroup cur_group;
	
	public static final String GROUP_TAG="group";
	public static final String FEED_TAG="feed";
	
	public static final String G_ATTR_URL="url";
	public static final String G_ATTR_NAME="name";
	public static final String G_ATTR_NUM="num";

	public static final String C_ATTR_URL="url";
	public static final String C_ATTR_NAME="name";

	public String getCur_tag() {
		return cur_tag;
	}


	public void setCur_tag(String cur_tag) {
		this.cur_tag = cur_tag;
	}


	public List<RecommGroup> getRecommGroupList() {
		return recommGroupList;
	}


	public void setRecommGroupList(List<RecommGroup> recommGroupList) {
		this.recommGroupList = recommGroupList;
	}


	public RecommGroup getCur_group() {
		return cur_group;
	}


	public void setCur_group(RecommGroup cur_group) {
		this.cur_group = cur_group;
	}


	@Override
	public URL getUrl() {
		String urlStr=G.RECOMM_FEEDS_LINK;
		URL url=null;
		try {
			url=new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	
	@Override
	public void startDocument() throws SAXException {
		recommGroupList=new ArrayList<RecommGroup>();
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(tagIsEqual(qName, GROUP_TAG)){
			cur_group=new RecommGroup();
			processGroupAttr(attributes);
		}else if(tagIsEqual(qName, FEED_TAG)){
			RecommChannel channel=new RecommChannel(attributes.getValue(C_ATTR_URL),attributes.getValue(C_ATTR_NAME));
			cur_group.addChan(channel);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(tagIsEqual(qName, GROUP_TAG)){
			recommGroupList.add(cur_group);
		}
	}


	private boolean tagIsEqual(String a, String b){
		return a.equalsIgnoreCase(b);
	} 

	private void processGroupAttr(Attributes attributes) {
		cur_group.setUrlStr(attributes.getValue(G_ATTR_URL));
		cur_group.setName(attributes.getValue(G_ATTR_NAME));
		cur_group.setNum(parseStr2IntByDef(attributes.getValue(G_ATTR_NUM),0));
	}


	private int parseStr2IntByDef(String str,int def){
		int result=def;
		try{
			result=Integer.parseInt(str);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}	

}
