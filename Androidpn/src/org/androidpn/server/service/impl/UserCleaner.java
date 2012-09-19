package org.androidpn.server.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.model.User;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserCleaner {
	
	private final Log log = LogFactory.getLog(UserCleaner.class);
	
	private UserDao userDao;
	private SessionManager sessionManager;
	
	private NotificationManager notificationManager;
	
	private  final long tcpDeadInterval;
	
	public UserCleaner(){
		sessionManager=SessionManager.getInstance();
		tcpDeadInterval=Config.getLong("tcp.dead.interval");
	}
	
	public void clean(){
		log.debug("clean()");
		long userInvalidateTime=Config.getLong("user.invalidate.time");
		long expirationMilliseconds=System.currentTimeMillis()-userInvalidateTime;
		Timestamp expiration=new Timestamp(expirationMilliseconds);
		List<User> userList=userDao.getOutdatedUser(expiration);
		for(User u:userList){
			userDao.removeUser(u.getId());
			log.debug("用户"+u.getUsername()+"过期，已删除。");
		}
	}


	/**
	 * 通过定时向客户端发送广播来达到关闭不断连的tcp连接。
	 */
	public void closeInvalidSession(){
		log.debug("closeInvalidSession()");
		notificationManager.sendBroadcast("1234567890", "test connection", "test connection", "");
	}
	
	
	public void checkDeadTcps(){
		for(ClientSession session:sessionManager.getSessions()){
			if(System.currentTimeMillis()-session.getIoSession().getLastIoTime()>tcpDeadInterval){
				session.close();
				log.debug("because of too long time has no heartbeat,session "+session+" has been closed.");
			}
		}
	}
	
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
	}
	
	
}
