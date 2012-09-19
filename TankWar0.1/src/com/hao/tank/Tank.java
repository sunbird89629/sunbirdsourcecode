package com.hao.tank;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Tank {
	
	public static Random RANDOM=new Random();
	
	TankClient tc=null;
	
	int x, y;
	int oldX,oldY;

	int speed = 5;
	
	private static Toolkit TK=Toolkit.getDefaultToolkit();
	private static Image[] imgs={
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/U.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/RU.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/R.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/RD.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/D.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/LD.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/L.gif")),
		TK.getImage(Tank.class.getClassLoader().getResource("images/tank/LU.gif"))
	};

	public static final int WIDTH =30;
	public static final int HEIGHT = 30;
	
	public static final int X_SPEED = 5;
	public static final int Y_SPEED = 5;
	
	private boolean good;
	
	private int step=RANDOM.nextInt(12)+3;
	
	private boolean live=true;
	
	private int life=100;
	
	private BloodBar bb=new BloodBar();
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}


	private Direction dir = Direction.STOP;
	private Direction ptDir=Direction.U;

	private boolean bU = false;
	private boolean bR = false;
	private boolean bD = false;
	private boolean bL = false;

//	public Tank(int x, int y) {
//		this.x = x;
//		this.y = y;
//	}
	
	

	public Tank(TankClient tc, int x, int y,Direction dir,boolean good) {
		this.tc = tc;
		this.x = x;
		this.y = y;
		this.oldX=x;
		this.oldY=y;
		this.dir=dir;
		this.good=good;
	}



	public void move() {
		
		this.oldX=x;
		this.oldY=y;
		
		
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
		
		if(dir!=Direction.STOP){
			ptDir=dir;
		}
		
		if(!good){
			if(step==0){
				Direction[] dirs=Direction.values();
				int rn=RANDOM.nextInt(dirs.length);
				dir=dirs[rn];
				step=RANDOM.nextInt(12)+3;
			}else{
				step--;
			}
			if(RANDOM.nextInt(40)>35) this.fire();
		}
		
		if(x<0) x=0;
		if(y<0) y=0;
		if(x>TankClient.GAME_WIDTH) x=TankClient.GAME_WIDTH;
		if(y>TankClient.GAME_HEIGHT) y=TankClient.GAME_HEIGHT;
		
	}

	public void draw(Graphics g) {
		
		if(!live) return;
		
		move();
		Color c = g.getColor();
//		if(good){
//			g.setColor(Color.RED);
//		}else{
//			g.setColor(Color.BLUE);
//		}
//		g.fillOval(x, y, WIDTH, HEIGHT);
		
		g.setColor(c);
		
		switch (ptDir) {
		case U:
			g.drawImage(imgs[0], x, y, null);
			break;
		case RU:
			g.drawImage(imgs[1], x, y, null);
			break;
		case R:
			g.drawImage(imgs[2], x, y, null);
			break;
		case RD:
			g.drawImage(imgs[3], x, y, null);
			break;
		case D:
			g.drawImage(imgs[4], x, y, null);
			break;
		case LD:
			g.drawImage(imgs[5], x, y, null);
			break;
		case L:
			g.drawImage(imgs[6], x, y, null);
			break;
		case LU:
			g.drawImage(imgs[7], x, y, null);
			break;
		case STOP:
			g.drawImage(imgs[0], x, y, null);
			break;
		}
		
		if(this.good){
			bb.draw(g);
		}
	}

	public void locateDirection() {
		if (bU && !bR && !bD && !bL) dir = Direction.U;
		else if (bU && bR && !bD && !bL) dir = Direction.RU;
		else if (!bU && bR && !bD && !bL) dir = Direction.R;
		else if (!bU && bR && bD && !bL) dir = Direction.RD;
		else if (!bU && !bR && bD && !bL) dir = Direction.D;
		else if (!bU && !bR && bD && bL) dir = Direction.LD;
		else if (!bU && !bR && !bD && bL) dir = Direction.L;
		else if (bU && !bR && !bD && bL) dir = Direction.LU;
		else if (!bU && !bR && !bD && !bL) dir = Direction.STOP;
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		
		case KeyEvent.VK_UP:
			bU=true;
			break;
		case KeyEvent.VK_RIGHT:
			bR=true;
			break;
		case KeyEvent.VK_DOWN:
			bD=true;
			break;
		case KeyEvent.VK_LEFT:
			bL=true;
			break;
		}
		locateDirection();
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_A:
			superFire();
			break;
		case KeyEvent.VK_UP:
			bU=false;
			break;
		case KeyEvent.VK_RIGHT:
			bR=false;
			break;
		case KeyEvent.VK_DOWN:
			bD=false;
			break;
		case KeyEvent.VK_LEFT:
			bL=false;
			break;
		}
		locateDirection();
	}
	
	
	private void stay(){
		x=oldX;
		y=oldY;
	}
	
	public Missile fire(){
		
		if(!live){
			return null;
		}
		
		int x=this.x+Tank.WIDTH/2-Missile.WIDTH/2;
		int y=this.y+Tank.HEIGHT/2-Missile.HEIGHT/2;
		
		Missile m=new Missile(x,y,good,ptDir,tc);
		tc.missiles.add(m);
		return m;
	}
	
	public Missile fire(Direction dir){
		
		if(!live){
			return null;
		}
		
		int x=this.x+Tank.WIDTH/2-Missile.WIDTH/2;
		int y=this.y+Tank.HEIGHT/2-Missile.HEIGHT/2;
		
		Missile m=new Missile(x,y,good,dir,tc);
		tc.missiles.add(m);
		return m;
	}
	
	public void superFire(){
		Direction[] dirs=Direction.values();
		for(Direction dir:dirs){
			if(dir!=Direction.STOP) fire(dir);
		}
	}
	
	public boolean collidesWithWall(Wall w){
		if(this.getRect().intersects(w.getRect())){
			this.stay();
			return true;
		}else{
			return false;
		}
	}
	
	public boolean collidesWithTank(Tank tank){
		if(this!=tank){
			if(this.isLive()&&tank.isLive()&& this.getRect().intersects(tank.getRect()) ){
				stay();
				this.stay();
				return true;
			}
		}
		return false;
	}
	
	public void collidesWithTanks(List<Tank> tanks){
		for(int i=0;i<tanks.size();i++){
			collidesWithTank(tanks.get(i));
		}
	}
	
	public boolean collidesWithWalls(List<Wall> walls){
		boolean result=false;
		for(Wall w:walls){
			result = collidesWithWall(w);
			if(result){
				return result;
			}
		}
		return result;
	}
	
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}

	public boolean eatBlood(Blood b){
		if(this.isGood()&&this.isLive()&&b.isLive()&&this.getRect().intersects(b.getRect())){
			this.life=100;
			b.setLive(false);
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}
	
	
	private class BloodBar{
		public void draw(Graphics g){
			Color c=g.getColor();
			g.setColor(Color.CYAN);
			g.drawRect(x, y-5, WIDTH, 5);
			double dLife=life;
			int width=(int) (dLife/100*WIDTH);
			g.fillRect(x, y-5, width, 5);
			g.setColor(c);
			
		}
	}
	
	
}
