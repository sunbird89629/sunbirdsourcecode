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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.androidpn.server.model.Message;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.net.Connection;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

/** 
 * This class represents a session between the server and a client.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ClientSession extends Session {

    private static final Log log = LogFactory.getLog(ClientSession.class);

    private static final String ETHERX_NAMESPACE = "http://etherx.jabber.org/streams";

    private AuthToken authToken;

    private boolean initialized;

    private boolean wasAvailable = false;

    private Presence presence = null;
    
    private ScheduledExecutorService scheduledExecutorService;
    private Map<String,ScheduledFuture> activeMessages;
    
    
    
    {
    	scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
    	activeMessages=new HashMap<String, ScheduledFuture>();
    }

    /**
     * Constructor.
     * 
     * @param serverName the server name
     * @param connection the connection
     * @param streamID the stream ID
     */
    public ClientSession(String serverName, Connection connection,
            String streamID) {
        super(serverName, connection, streamID);
        presence = new Presence();
        presence.setType(Presence.Type.unavailable);
        
    }

    /**
     * Creates a new session between the server and a client, and returns it.
     * 
     * @param serverName the server name
     * @param connection the connection
     * @param xpp the XML parser to handle incoming data 
     * @return a newly created session
     * @throws XmlPullParserException if an error occurs while parsing incoming data
     */
    public static ClientSession createSession(String serverName,
            Connection connection, XmlPullParser xpp)
            throws XmlPullParserException {
        log.debug("createSession()...");

        if (!xpp.getName().equals("stream")) {
            throw new XmlPullParserException("Bad opening tag (not stream)");
        }

        if (!xpp.getNamespace(xpp.getPrefix()).equals(ETHERX_NAMESPACE)) {
            throw new XmlPullParserException("Stream not in correct namespace");
        }

        String language = "en";
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            if ("lang".equals(xpp.getAttributeName(i))) {
                language = xpp.getAttributeValue(i);
            }
        }

        // Store language and version information
        connection.setLanaguage(language);
        connection.setXMPPVersion(MAJOR_VERSION, MINOR_VERSION);

        // Create a ClientSession
        ClientSession session = SessionManager.getInstance().createClientSession(connection);

        // Build the start packet response
        StringBuilder sb = new StringBuilder(200);
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<stream:stream ");
        sb
                .append("xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\" from=\"");
        sb.append(serverName);
        sb.append("\" id=\"");
        sb.append(session.getStreamID().toString());
        sb.append("\" xml:lang=\"");
        sb.append(language);
        sb.append("\" version=\"");
        sb.append(MAJOR_VERSION).append(".").append(MINOR_VERSION);
        sb.append("\">");
        connection.deliverRawText(sb.toString());

        // XMPP 1.0 needs stream features
        sb = new StringBuilder();
        sb.append("<stream:features>");
        if (connection.getTlsPolicy() != Connection.TLSPolicy.disabled) {
            sb.append("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\">");
            if (connection.getTlsPolicy() == Connection.TLSPolicy.required) {
                sb.append("<required/>");
            }
            sb.append("</starttls>");
        }

        String specificFeatures = session.getAvailableStreamFeatures();
        if (specificFeatures != null) {
            sb.append(specificFeatures);
        }
        sb.append("</stream:features>");

        connection.deliverRawText(sb.toString());
        return session;
    }

    /**
     * Returns the username associated with this session.
     * 
     * @return the username
     * @throws UserNotFoundException if a user has not authenticated yet
     */
    public String getUsername() throws UserNotFoundException {
        if (authToken == null) {
            throw new UserNotFoundException();
        }
        return getAddress().getNode();
    }

    /**
     * Returns the authentication token associated with this session.
     * 
     * @return the authentication token
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Initialize the session with an authentication token
     * @param authToken the authentication token
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Initialize the session with an authentication token and resource name.
     * 
     * @param authToken the authentication token
     * @param resource the resource
     */
    public void setAuthToken(AuthToken authToken, String resource) {
        setAddress(new JID(authToken.getUsername(), getServerName(), resource));
        this.authToken = authToken;
        setStatus(Session.STATUS_AUTHENTICATED);
        // Add session to the session manager
        sessionManager.addSession(this);
    }

    /**
     * Indicates if the session has been initialized.
     * 
     * @return true if the session has been initialized, false otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialization state of the session.
     * 
     * @param initialized true if the session has been initialized
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Indicates if the session was available ever.
     *  
     * @return true if the session was available ever, false otherwise.
     */
    public boolean wasAvailable() {
        return wasAvailable;
    }

    /**
     * Returns the presence of this session.
     *  
     * @return the presence
     */
    public Presence getPresence() {
        return presence;
    }

    /**
     * Sets the presence of this session.
     * 
     * @param presence the presence
     */
    public void setPresence(Presence presence) {
        Presence oldPresence = this.presence;
        this.presence = presence;
        if (oldPresence.isAvailable() && !this.presence.isAvailable()) {
            setInitialized(false);
        } else if (!oldPresence.isAvailable() && this.presence.isAvailable()) {
            wasAvailable = true;
        }
    }

    /**
     * Returns a text with the available stream features. 
     */
    public String getAvailableStreamFeatures() {
        StringBuilder sb = new StringBuilder();
        if (getAuthToken() == null) {
            // Supports Non-SASL Authentication            
            sb.append("<auth xmlns=\"http://jabber.org/features/iq-auth\"/>");
            // Supports In-Band Registration
            sb.append("<register xmlns=\"http://jabber.org/features/iq-register\"/>");
        } else {
            // If the session has been authenticated            
            sb.append("<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>");
            sb.append("<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/>");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " presence: " + presence;
    }

    
    public boolean isCanCommunication(){
    	return this.connection.isSessionClossing();
    }
    
    public IoSession getIoSession(){
    	return this.getConnection().getIoSession();
    }
    
    
    public void setIoSessionAttribute(Object key,Object value) {
		getIoSession().setAttribute(key,value);
	}
	
	public void removeIoSessionAttribute(Object key){
		getIoSession().removeAttribute(key);
	}
	
	public Object getIoSessionAttribute(Object key){
		return getIoSession().getAttribute(key);
	}
    
    
    
    public void addMessage(Message msg){
    	if(msg==null){
    		throw new RuntimeException("You should not add a empty message!");
    	}
    	Map<String,Message> msgMap=(Map<String,Message>) getIoSessionAttribute(NotificationManager.MSG_KEY);

    	if(msgMap==null){
    		msgMap=new HashMap<String,Message>();
    		setIoSessionAttribute(NotificationManager.MSG_KEY, msgMap);
    	}
    	msgMap.put(msg.getMessage(), msg);
    }
    
    public void removeMessage(String msgMessage){
    	Map<String,Message> msgMap=(Map<String,Message>) getIoSessionAttribute(NotificationManager.MSG_KEY);
    	if(msgMap!=null){
    		msgMap.remove(msgMessage);
    	}
    }
    //提交一个TimeoutTask,监控当前发送的消息，在超时时关闭session。
	public void submitTimeoutTask(Message msg){
    	
    	Long waitTime=Config.getLong("message.push.timeout", 120000L);
    	
    	
		ScheduledFuture future= scheduledExecutorService.schedule(new TimeoutTask(),waitTime,TimeUnit.MILLISECONDS);
		
		activeMessages.put(msg.getMessage()+"", future);
		System.out.println();
	}
    
	
	//当用户收到消息后，会马上发送一个确认的消息，消息的内容就是coupon的id，服务器端在收到这个消息后，调用此方法，
	//将消息从ioSession的map中移除，然后将监听此消息超时的线程移除
    public void stopAndRemoveTimeoutTask(String msgMessage){
		ScheduledFuture future=activeMessages.get(msgMessage);
		if(future!=null && !future.isDone()){
			future.cancel(true);
		}
		activeMessages.remove(msgMessage);
	}
    
    
    
    public void addMessageAndTimeoutTask(Message msg){
    	if(msg==null){
    		throw new RuntimeException("You should not add a empty message!");
    	}
    	addMessage(msg);
    	submitTimeoutTask(msg);
    }
    
    public void removeMessageAndTimeoutTask(String msgMessage){
    	removeMessage(msgMessage);
    	stopAndRemoveTimeoutTask(msgMessage);
    }
    
    
    //此任务主要是监听消息发送的时间，如果在指定的时间内没有发送成功并且没有发生异常的话，则此任务执行，调用当前session的close方法，关闭session，
    //如果该任务关联的Message发送到了客户端并受到的客户端的确认信息，则该任务会被停止，则当前session不会被关闭。
	private class TimeoutTask implements Runnable{
		public TimeoutTask(){
		}
		public void run() {
			//log.debug("timeout task is run.....................................................");
			close();
		}
	}
	
	//停止当前的push等待监控线程，并关闭session
	//父类中的close方法只调用的Connection的close方法，没有关和当前session对应的TimeoutTask的线程池，所以在此处覆盖了父方法，增加了
	//关闭父方法的语句，外部想要关闭当前session的时候，要掉用此方法。
	@Override
	public void close() {
		scheduledExecutorService.shutdownNow();
		super.close();
	}
    
}
