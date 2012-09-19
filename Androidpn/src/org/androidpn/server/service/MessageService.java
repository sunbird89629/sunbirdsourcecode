package org.androidpn.server.service;

import java.util.Collection;

import org.androidpn.server.model.Message;

public interface MessageService {
	void saveMessage(Message msg);
	void saveBatch(Collection<Message> msgList);
}
