package org.androidpn.server.dao;

import java.util.List;

import org.androidpn.server.model.Message;

public interface MessageDao {
	public Message getMessage(Long id);

    public Message saveMessage(Message message);

    public void removeMessage(Long id);

    public boolean exists(Long id);

    public List<Message> getMessage();
}
