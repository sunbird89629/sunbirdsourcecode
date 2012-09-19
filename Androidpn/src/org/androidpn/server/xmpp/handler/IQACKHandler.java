package org.androidpn.server.xmpp.handler;

import org.androidpn.server.xmpp.UnauthorizedException;
import org.xmpp.packet.IQ;

public class IQACKHandler extends IQHandler {
	 private static final String NAMESPACE = "apnclient:iq:acknowlage";
	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		return null;
	}

}
