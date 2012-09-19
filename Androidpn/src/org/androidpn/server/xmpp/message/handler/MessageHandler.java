package org.androidpn.server.xmpp.message.handler;

import org.xmpp.packet.Message;

public interface MessageHandler {
	void handle(Message message);
}
