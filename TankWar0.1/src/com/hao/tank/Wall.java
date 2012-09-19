package com.hao.tank;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Wall {
	
	
	TankClient tc=null;
	int x,y,w,h;
	
	
	public Wall(int x, int y, int w, int h,TankClient tc) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tc = tc;
	}



	public void draw(Graphics g){
		g.fillRect(x, y, w, h);
	}
	
	
	public Rectangle getRect(){
		return new Rectangle(x,y,w,h);
	}
	
	
}
