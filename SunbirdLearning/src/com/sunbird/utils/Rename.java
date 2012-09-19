package com.sunbird.utils;
import java.io.File;


public class Rename {
	public static void main(String[] args) {
		String path="/Users/zhangshaofang/Downloads/zhangtin/·²ÈËÐÞÏÉ´«(401Æð)/";
		File destFile=new File(path);
		File[] files=destFile.listFiles();
		for(File f:files){
			String fName=f.getName();
			String[] strs=fName.split("\\.");
			try{
				int num=Integer.valueOf(strs[0]);
				//num+=400;
				if(num<10){
					File newFile=new File(f.getParent(),"000"+num+"."+strs[1]);
					f.renameTo(newFile);
					System.out.println(f.getName()+" rename to "+newFile.getName());
				}else if(num<100){
					File newFile=new File(f.getParent(),"00"+num+"."+strs[1]);
					f.renameTo(newFile);
					System.out.println(f.getName()+" rename to "+newFile.getName());
				}else if(num<1000){
					File newFile=new File(f.getParent(),"0"+num+"."+strs[1]);
					f.renameTo(newFile);
					System.out.println(f.getName()+" rename to "+newFile.getName());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
