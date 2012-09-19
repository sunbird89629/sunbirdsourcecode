package com.hao.tank;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ObjectInputStream.GetField;


public class Explode {
	
	
	int x,y;
	
	private boolean live=true;



	private TankClient tc;
	
//	int[] diameter={4,7,12,18,26,32,49,30,14,6};
	
	public static Toolkit TK=Toolkit.getDefaultToolkit();
	public static Image[] imgs={
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/1.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/2.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/3.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/4.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/5.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/6.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/7.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/8.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/9.png")),
		TK.getImage(Explode.class.getClassLoader().getResource("images/bomb/10.png")),
	};
	
	int step=0;
	
	public boolean isLive() {
		return live;
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	public Explode(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}



	public void draw(Graphics g){
		if(!live) return;
		if(step==imgs.length){
			live=false;
			step=0;
			return;
		}
		
		Color c=g.getColor();
		g.setColor(Color.ORANGE);
		g.drawImage(imgs[step], x, y, null);
		g.setColor(c);
		
		step++;
	}
}
