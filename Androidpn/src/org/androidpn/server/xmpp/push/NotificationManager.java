/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.androidpn.server.dao.MessageDao;
import org.androidpn.server.dao.UserDao;
import org.androidpn.server.model.Message;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/**
 * This class is to manage sending the notifcations to the users.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationManager {

	public static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";
	
	public static final String MSG_KEY="LAST_MSG";

	private final Log log = LogFactory.getLog(getClass());

	private SessionManager sessionManager;

	private MessageDao messageDao;

	private UserDao userDao;

	/**
	 * Constructor.
	 */
	public NotificationManager() {
		sessionManager = SessionManager.getInstance();
	}

	/**
	 * Broadcasts a newly created notification message to all connected users.
	 * 
	 * @param apiKey the API key
	 * @param title the title
	 * @param message the message details
	 * @param uri the uri
	 */
	public void sendBroadcast(String apiKey, String title, String message, String uri) {
		log.debug("----------------------sendBroadcast() begin...");
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
		for (ClientSession session : sessionManager.getSessions()) {
			if (session.getPresence().isAvailable()) {
				notificationIQ.setTo(session.getAddress());
				session.deliver(notificationIQ);
			}
		}
		log.debug("----------------------sendBroadcast() end...");
	}

	/**
	 * Sends a newly created notification message to the specific user.
	 * 
	 * session是已经建立连接的会话。
	 * 
	 * @param apiKey the API key
	 * @param title the title
	 * @param message the message details
	 * @param uri the uri
	 * 
	 * @return true is send successfully,else is false.
	 */
	private void sendNotificationToUser(String apiKey, String username,String title, String message, String uri,boolean isPersistentIfError) {
		log.debug("sendNotifcationToUser(),username is:" + username+ ",title is:" + title + ",message is:" + message + ",uri is:"+ uri);
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
		ClientSession session = sessionManager.getSession(username);
		User user=null;
		try {
			user=userDao.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Message msg = new Message(new Timestamp(System.currentTimeMillis()), title, message, uri, user);
		if (session != null) {// 如果会话存在，则发送消息。
			if (session.getPresence().isAvailable()) {
				if (isPersistentIfError) {
					session.addMessageAndTimeoutTask(msg);
				}
//				if(isPersistentIfError){
//					session.setIoSessionAttribute(NotificationManager.MSG_KEY, msg);
//				}else{
//					session.removeIoSessionAttribute(NotificationManager.MSG_KEY);
//				}
				notificationIQ.setTo(session.getAddress());
				session.deliver(notificationIQ);
			}else{
				messageDao.saveMessage(msg);
			}
		}else{
			if(isPersistentIfError){
				messageDao.saveMessage(msg);
			}
		}
	}
	
	/**
	 * 保证将消息传达到用户的手中，如果用户当前的push不可用，则将此信息保存到数据库中
	 * @param apiKey
	 * @param username
	 * @param title
	 * @param message
	 * @param uri
	 */
	public void sendNotificationToUserGuarantee(String apiKey, String username,
			String title, String message, String uri) {
		sendNotificationToUser(apiKey, username, title, message, uri,true);
	}

	/**
	 * 发送因各种原因push失败而持久化到数据库中的优惠劵。
	 * @param apiKey
	 * @param username
	 */
	public void sendRetentionNotificationToUser(String apiKey, String username) {
		try {
			User user = userDao.getUserByUsername(username);
			List<Message> messages = user.getMessages();
			List<Message> removeMsgs = new ArrayList<Message>();
			for (int i = 0; i < messages.size(); i++) {
				Message msg = messages.get(i);
				if (!msg.isOutOfDate()) {// 该消息没有过期
					sendNotificationToUserGuarantee(apiKey, username, msg.getTitle(), msg.getMessage(), msg.getUri());
				} else {// 该消息已经过期
					// TODO 该消息已经过期
				}
				removeMsgs.add(msg);
			}
			user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			for (Message msg : removeMsgs) {
				user.remove(msg);
			}
			userDao.saveUser(user);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new notification IQ and returns it.
	 */
	private IQ createNotificationIQ(String apiKey, String title,String message, String uri) {
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());
		// String id = String.valueOf(System.currentTimeMillis());

		Element notification = DocumentHelper.createElement(QName.get(
				"notification", NOTIFICATION_NAMESPACE));
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText(apiKey);
		notification.addElement("title").setText(title);
		notification.addElement("message").setText(message);
		notification.addElement("uri").setText(uri);

		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);
		return iq;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
