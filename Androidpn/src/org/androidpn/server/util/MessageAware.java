package org.androidpn.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;

public class MessageAware {
	private SessionManager sessionManager;
	//Key username+messageId;
	private Map<String,Timer> timerMap=new HashMap<String, Timer>();
	
	
	private class MessageAwareTask implements Runnable{
		private ClientSession session;
		public MessageAwareTask(ClientSession session){
			this.session=session;
			sessionManager=SessionManager.getInstance();
		}
		public void run() {
			sessionManager.removeSession(session);
			session.close();
		}
	}
	
	
}
