package cn.itcast.rss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetUtils {
	public static final String CHALLEL_LOGO_FILENAME_SEPARATOR = "___";

	public static String parseImgUrl2Name(String imgUrl) {// 文件的名称为网站地址加上文件名称。
		String filename = null;
		String[] strs = imgUrl.split("/");
		filename = strs[2] + CHALLEL_LOGO_FILENAME_SEPARATOR
				+ strs[strs.length - 1];
		return filename;
	}
	/**
	 * 根据制定的地址，建立连接。
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static InputStream getInStreamByUrl(String urlStr)
			throws Exception {
		InputStream inStream = null;
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			inStream = conn.getInputStream();
		}
		return inStream;
	}
	
	public static Bitmap getHttpBitmap(String urlStr) {
		Bitmap bitmap=null;
		try {
			InputStream inStream=getInStreamByUrl(urlStr);
			bitmap=BitmapFactory.decodeStream(inStream);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(Exception e){
			//TODO 在这里进行异常处理
		}
		return bitmap;
	}
	

	public static void writeInStream2File(InputStream inStream, File file)
			throws Exception {
		if (inStream == null)
			return;
		FileOutputStream outStream = new FileOutputStream(file);
		writeInStream2OutStream(inStream,outStream);
	}
	
	public static void writeInStream2OutStream(InputStream inStream, OutputStream outStream)
		throws Exception {
		if (inStream == null)
			return;
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
	}
	
	public static void saveNetImg2File(String urlStr,File tarFile){
		//TODO 这里的异常处理，暂时先打印堆栈。
		try{
			InputStream inStream = getInStreamByUrl(urlStr);
			writeInStream2File(inStream,tarFile);
			inStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void parseBitmap2RBitmap(){
		
	}
	
	public static String checkNull(String tarStr,String defa){
		return tarStr==null?defa:tarStr;
	}
	
	public static String checkNull(int tar,String defa){
		String tarStr=String.valueOf(tar);
		return tarStr.trim()!=null?tarStr:defa;
	}
	
	
	public static String toBaiduPath(String title, String path) {
		String baidu1 = "http://wap.baidu.com/ssid=0/from=0/bd_page_type=1/uid=wiaui_1292091570_5219/pu=sz%401330_240%2Cusm%401/w=0_10_";
		String baidu2 = "/t=wap/tc?ref=www_touch&tj=www_normal_2_0_10&p=863ec35486cc43be0be2943750&order=2&vit=osres&src=";
		return baidu1 + title + baidu2 + path;
	}
	
	public static String toGooglePath(String title, String path) {
		String google1 = "http://www.google.com.hk/gwt/x?gl=CN&q=";
		String google2 = "&hl=zh_CN&ei=KsLzTOjgDMmOkAXP6pG0AQ&ved=0CAsQFjAB&source=m&rd=1&u=";
		return google1 + title + google2 + path;
	}
	
}
