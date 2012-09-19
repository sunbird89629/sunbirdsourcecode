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
package org.androidpn.server.xmpp.session;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.dao.hibernate.UserDaoHibernate;
import org.androidpn.server.model.Message;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.XmppServer;
import org.androidpn.server.xmpp.net.Connection;
import org.androidpn.server.xmpp.net.ConnectionCloseListener;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

/** 
 * This class manages the sessions connected to the server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class SessionManager {

    private static final Log log = LogFactory.getLog(SessionManager.class);

    private static final String RESOURCE_NAME = "AndroidpnClient";

    private static SessionManager instance;

    private String serverName;

    private Map<String, ClientSession> preAuthSessions = new ConcurrentHashMap<String, ClientSession>();

    private Map<String, ClientSession> clientSessions = new ConcurrentHashMap<String, ClientSession>();

    private final AtomicInteger connectionsCounter = new AtomicInteger(0);

    private ClientSessionListener clientSessionListener = new ClientSessionListener();
    
    private UserDao userDao=new UserDaoHibernate();

    private SessionManager() {
        serverName = XmppServer.getInstance().getServerName();
    }

    /**
     * Returns the singleton instance of SessionManager.
     * 
     * @return the instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                instance = new SessionManager();
            }
        }
        return instance;
    }

    /**
     * Creates a new ClientSession and returns it.
     *  
     * @param conn the connection
     * @return a newly created session
     */
    public ClientSession createClientSession(Connection conn) {
        if (serverName == null) {
            throw new IllegalStateException("Server not initialized");
        }

        Random random = new Random();
        String streamId = Integer.toHexString(random.nextInt());

        ClientSession session = new ClientSession(serverName, conn, streamId);
        
        //-------------------------------------------------------------------
        //向ioSession中保存其对应的ClientSession，在push是，如果抛出了异常，系统调用XmppIoHandler的exceptionCaught方法，
        //这个方法会传入抛出异常的ioSession，此时可以通过IoSession获得ClientSession，然后调用其的close方法，关闭Timeout的线程
        //并发库，然后关闭session。
        session.setIoSessionAttribute("CLIENT_SESSION", session);
        conn.init(session);
        
        conn.registerCloseListener(clientSessionListener);

        // Add to pre-authenticated sessions
        preAuthSessions.put(session.getAddress().getResource(), session);

        // Increment the counter of user sessions
        connectionsCounter.incrementAndGet();

        log.debug("ClientSession created.");
        return session;
    }

    /**
     * Adds a new session that has been authenticated. 
     * 
     * "session.getAddr...ss().toString()"= "286eb7b24e3b43188a2d3a14fa36046c@127.0.0.1/AndroidpnClient"
     * "session.getUsername()"="286eb7b24e3b43188a2d3a14fa36046c"
     *  
     * @param session the session
     */
    public void addSession(ClientSession session) {//客户端认证成功后，将ClientSession从preAuthSessions移到clentSessions中。
        preAuthSessions.remove(session.getStreamID().toString());
        clientSessions.put(session.getAddress().toString(), session);
//        sendStoreMessage(session);
    }
    
    public void sendStoreMessage(ClientSession session){
    	try{
    		User user=userDao.getUserByUsername(session.getUsername());
        	String apiKey=Config.getString("apiKey", "1234567890");
        	List<Message> messages=user.getMessages();
        	for(Message msg:messages){
        		IQ iq=createNotificationIQ(apiKey, msg.getTitle(), msg.getMessage(), msg.getUri());
        		session.deliver(iq);
        	}
    	}catch(UserNotFoundException e){
    		e.printStackTrace();
    	}
    }
    

    /**
     * Returns the session associated with the username.
     * 
     * @param username the username of the client address
     * @return the session associated with the username
     */
    public ClientSession getSession(String username) {
        // return getSession(new JID(username, serverName, null, true));
        return getSession(new JID(username, serverName, RESOURCE_NAME, true));
    }

    /**
     * Returns the session associated with the JID.
     * 
     * @param from the client address
     * @return the session associated with the JID
     */
    public ClientSession getSession(JID from) {
        if (from == null || serverName == null
                || !serverName.equals(from.getDomain())) {
            return null;
        }
        // Check pre-authenticated sessions
        if (from.getResource() != null) {
            ClientSession session = preAuthSessions.get(from.getResource());
            if (session != null) {
                return session;
            }
        }
        if (from.getResource() == null || from.getNode() == null) {
            return null;
        }
        return clientSessions.get(from.toString());
    }

    /**
     * Returns a list that contains all authenticated client sessions.
     * 
     * @return a list that contains all client sessions
     */
    public Collection<ClientSession> getSessions() {
        return clientSessions.values();
    }

    /**
     * Removes a client session.
     * 
     * @param session the session to be removed
     * @return true if the session was successfully removed 
     */
    public boolean removeSession(ClientSession session) {
        if (session == null || serverName == null) {
            return false;
        }
        JID fullJID = session.getAddress();

        // Remove the session from list
        boolean clientRemoved = clientSessions.remove(fullJID.toString()) != null;
        boolean preAuthRemoved = (preAuthSessions.remove(fullJID.getResource()) != null);

        // Decrement the counter of user sessions
        if (clientRemoved || preAuthRemoved) {
            connectionsCounter.decrementAndGet();
            return true;
        }
        return false;
    }

    /**
     * Closes the all sessions. 
     */
    public void closeAllSessions() {
        try {
            // Send the close stream header to all connections
            Set<ClientSession> sessions = new HashSet<ClientSession>();
            sessions.addAll(preAuthSessions.values());
            sessions.addAll(clientSessions.values());

            for (ClientSession session : sessions) {
                try {
                    session.getConnection().systemShutdown();
                } catch (Throwable t) {
                }
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * Creates a new notification IQ and returns it.
     */
    private IQ createNotificationIQ(String apiKey, String title,
            String message, String uri) {
        Random random = new Random();
        String id = Integer.toHexString(random.nextInt());
        // String id = String.valueOf(System.currentTimeMillis());
        Element notification = DocumentHelper.createElement(QName.get("notification", NotificationManager.NOTIFICATION_NAMESPACE));
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

    /**
     * A listner to handle a session that has been closed.
     */
    private class ClientSessionListener implements ConnectionCloseListener {

        public void onConnectionClose(Object handback) {
            try {
                ClientSession session = (ClientSession) handback;
                removeSession(session);
            } catch (Exception e) {
                log.error("Could not close socket", e);
            }
        }
    }

}
