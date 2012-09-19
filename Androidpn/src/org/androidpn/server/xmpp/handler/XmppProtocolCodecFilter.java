package org.androidpn.server.xmpp.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

public class XmppProtocolCodecFilter extends ProtocolCodecFilter {
	public static Map<IoSession,Long> lastCommTime=new HashMap<IoSession,Long>();
	
	public XmppProtocolCodecFilter(ProtocolCodecFactory factory) {
		super(factory);
	}
	
	

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		lastCommTime.put(session, System.currentTimeMillis());
		super.messageReceived(nextFilter, session, message);
	}
	
	

}
