package com.hao.tank;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Blood {
	
	int x,y,w,h;
	
	private boolean live=true;
	
	
	
	private int[][] positions={
			{350,300},	
			{360,300},	
			{375,275},	
			{400,200},	
			{360,270},	
			{365,290},	
			{340,280}	
	};
	
	
	int step=0;
	
	
	public Blood() {
		x=positions[0][0];
		y=positions[0][1];
		w=5;
		h=5;
		
	}



	public void draw(Graphics g){
		
		move();
		
		if(!live){
			return;
		}
		
		Color c=g.getColor();
		g.setColor(Color.MAGENTA);
		g.fillRect(x, y, w, h);
		g.setColor(c);
	}
	
	public Rectangle getRect(){
		return new Rectangle(x,y,w,h);
	}
	
	public void move(){
		if(step>=positions.length-1){
			step=0;
		}else{
			step++;
		}
		x=positions[step][0];
		y=positions[step][1];
	}



	public boolean isLive() {
		return live;
	}



	public void setLive(boolean live) {
		this.live = live;
	}
	
	
	
	
	
}
