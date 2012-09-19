package com.hao.tank;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public class TankClient extends Frame{
	
	
	
	public static final int GAME_WIDTH=800;
	public static final int GAME_HEIGHT=600;
	
	Tank myTank=null;
	
	List<Missile> missiles=new LinkedList<Missile>();
	List<Tank> enemyTanks=new LinkedList<Tank>();
	List<Explode> explodes=new LinkedList<Explode>();
	List<Wall> walls=new LinkedList<Wall>();
	
	Blood blood=null;
	Image offScreenImage=null;
	
	
	public void restart(){
		myTank=new Tank(this,300, 500,Direction.STOP, true);
		missiles.clear();
		enemyTanks.clear();
		int initTankCount=PropertyMgr.getInt("initTankCount");
		for(int i=0;i<initTankCount;i++){
			enemyTanks.add(new Tank(this,50+40*i,50,Direction.D,false));
		}
		explodes.clear();
		walls.clear();
		walls.add(new Wall(100,200,200,50,this));
		walls.add(new Wall(400,400,300,30,this));
		blood=new Blood();
	}
	
	
	@Override
	public void update(Graphics g) {
		if(offScreenImage==null){
			offScreenImage=this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen=offScreenImage.getGraphics();
		
		Color c=gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		
		g.drawImage(offScreenImage, 0, 0, null);
	}


	@Override
	public void paint(Graphics g) {
		g.drawString("missile count is:"+missiles.size(), 10, 50);
		g.drawString("enemy tank count is:"+enemyTanks.size(), 10, 70);
		g.drawString("explode count is:"+explodes.size(), 10, 90);
		g.drawString("my tank life is:"+myTank.getLife(), 10, 110);
		
		myTank.eatBlood(blood);
		myTank.draw(g);
		
		
		Iterator<Tank> tankIterator=enemyTanks.iterator();
		while(tankIterator.hasNext()){
			Tank tank=tankIterator.next();
			if(tank.isLive()){
				tank.collidesWithWalls(walls);
				tank.collidesWithTanks(enemyTanks);
				tank.collidesWithTank(myTank);
				tank.draw(g);
			}else{
				tankIterator.remove();
			}
		}
		
		
		
		Iterator<Missile> missileIterator=missiles.listIterator();
		while(missileIterator.hasNext()){
			Missile m=missileIterator.next();
			if(m.isLive()){
				m.hitTanks(enemyTanks);
				m.hitTank(myTank);
				m.hitWalls(walls);
				m.draw(g);
			}else{
				missileIterator.remove();
			}
		}
		
		
		Iterator<Explode> explodeIterator=explodes.listIterator();
		while(explodeIterator.hasNext()){
			Explode e=explodeIterator.next();
			if(e.isLive()){
				e.draw(g);
			}else{
				explodeIterator.remove();
			}
		}
		
		for(Wall w:walls){
			w.draw(g);
		}
		
		blood.draw(g);
//		for(Missile m:missiles){
//			if(m.isLive()) m.draw(g);
//		}
	}


	public void lauchFrame(){
		this.setLocation(100,100);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("Tank War");
		this.setBackground(Color.GREEN);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		this.addKeyListener(new KeyMonitor());
		this.setResizable(false);
		this.setVisible(true);
		new Thread(new PaintThread()).start();
	}
	
	
	public static void main(String[] args) {
		TankClient tc=new TankClient();
		tc.lauchFrame();
		tc.restart();
	}
	
	
	private class PaintThread extends Thread{
		@Override
		public void run() {
			while(true){
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private class KeyMonitor extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			
			if(e.getKeyCode()==KeyEvent.VK_F2){
				restart();
				return;
			}
			
			myTank.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
		
		
		
	}
	
	
}
