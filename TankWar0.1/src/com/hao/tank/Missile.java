package com.hao.tank;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;



public class Missile {
	int x,y;

	Direction dir;
	
	TankClient tc=null;
	
	
	
	public static final int WIDTH=10;
	public static final int HEIGHT=10;
	public static final int X_SPEED=10;
	public static final int Y_SPEED=10;
	
	private boolean live=true;
	
	private boolean good=true;
	

	public boolean isLive() {
		return live;
	}


	public Missile(int x, int y,boolean good, Direction dir,TankClient tc) {
		this.x = x;
		this.y = y;
		this.good=good;
		this.dir = dir;
		this.tc=tc;
	}
	
	public void draw(Graphics g){
		if(!live) return;
		move();
		Color c=g.getColor();
		if(this.good){
			g.setColor(Color.RED);
		}else{
			g.setColor(Color.BLACK);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
	}

	public void move() {
		switch (dir) {
		case U:
			y -= X_SPEED;
			break;
		case RU:
			x += X_SPEED;
			y -= Y_SPEED;
			break;
		case R:
			x += X_SPEED;
			break;
		case RD:
			x += X_SPEED;
			y += Y_SPEED;
			break;
		case D:
			y += Y_SPEED;
			break;
		case LD:
			x -= X_SPEED;
			y += Y_SPEED;
			break;
		case L:
			x -= X_SPEED;
			break;
		case LU:
			x -= X_SPEED;
			y -= Y_SPEED;
			break;
		case STOP:
			break;
		}
		
		if(x<0||y<0||x>TankClient.GAME_WIDTH||y>TankClient.GAME_HEIGHT){
			live=false;
//			tc.missiles.remove(this);
		}
	}
	
	
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
	public boolean hitTank(Tank tank){
		if(!this.live){
			return false;
		}
		if(this.getRect().intersects(tank.getRect())&&tank.isLive()&&this.good!=tank.isGood()){
			if(tank.isGood()){
				tank.setLife(tank.getLife()-20);
				this.live=false;
				
				if(tank.getLife()<=0){
					tank.setLive(false);
					
					tc.explodes.add(new Explode(x, y, tc));
				}
			}else{
				tank.setLive(false);
				this.live=false;
				
				tc.explodes.add(new Explode(x, y, tc));
			}
			
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean hitTanks(List<Tank> tanks){
		boolean result=false;
		for(int i=0;i<tanks.size();i++){
			result=hitTank(tanks.get(i));
		}
		return result;
		
	}
	
	public boolean hitWall(Wall w){
		
		if(this.getRect().intersects(w.getRect())){
			this.live=false;
			return true;
		}else{
			return false;
		}
		
	}


	public void hitWalls(List<Wall> walls) {
		for(Wall w:walls){
			hitWall(w);
		}
	}
	
	
}
