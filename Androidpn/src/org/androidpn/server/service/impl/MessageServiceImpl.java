package org.androidpn.server.service.impl;

import java.util.Collection;

import org.androidpn.server.dao.MessageDao;
import org.androidpn.server.model.Message;
import org.androidpn.server.service.MessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageServiceImpl implements MessageService {

	private final Log log = LogFactory.getLog(getClass());
	private MessageDao messageDao;
	
	public void saveMessage(Message msg) {
		log.debug("saveMessage()"+msg.toString());
		messageDao.saveMessage(msg);
		log.debug("saveMessage() end."+msg.toString());
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	public void saveBatch(Collection<Message> msgList) {
		for(Message msg:msgList){
			messageDao.saveMessage(msg);
		}
	}
}
