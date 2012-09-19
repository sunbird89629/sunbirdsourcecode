package org.androidpn.server.xmpp.message.handler;

import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.xmpp.packet.Message;

public class AcknowlageMessageHandler implements MessageHandler{

	private SessionManager sessionManager;
	public static final String SUBJECT="ACK";
	//收到用户返回的确认后要删除ioSession中存储的message并停止TimeoutTask。
	public void handle(Message message) {
		//message body  coupon id
		sessionManager=SessionManager.getInstance();
		String msgMessage=message.getBody();
		ClientSession session=sessionManager.getSession(message.getFrom());
		session.removeMessageAndTimeoutTask(msgMessage);
	}
}
