package com.hao.tank;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyMgr {
	private static Properties props=new Properties();
	static{
		InputStream inStream=PropertyMgr.class.getClassLoader().getResourceAsStream("config/tank.properties");
		try {
			props.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getProperty(String key){
		return props.getProperty(key);
	}
	
	public static int getInt(String key){
		return Integer.parseInt(getProperty(key));
	}
}
