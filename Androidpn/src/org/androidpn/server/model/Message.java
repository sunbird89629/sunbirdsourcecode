package org.androidpn.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.androidpn.server.util.Config;

@Entity
@Table(name = "apn_message")
public class Message implements Serializable{
	private static final long serialVersionUID = 1845362556725768545L;
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name = "create_date")
	private Timestamp createDate;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "uri")
	private String uri;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Message() {
		super();
	}

	public Message(Timestamp createDate, String title, String message,
			String uri) {
		super();
		this.createDate = createDate;
		this.title = title;
		this.message = message;
		this.uri = uri;
	}

	public Message(Timestamp createDate, String title, String message, String uri,User user) {
		super();
		this.createDate = createDate;
		this.title = title;
		this.message = message;
		this.uri = uri;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * 返回该优惠劵是否过期
	 * @return
	 */
	public boolean isOutOfDate(){
		long retentions=Config.getLong("message.retention.time");
		long createTime=createDate.getTime();
		long realRetentions=System.currentTimeMillis()-createTime;
		return realRetentions>retentions;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Message)) {
			return false;
		} else {
			Message m = (Message)o;
			return this.id.equals(m.getId());
		}
	}
	
	@Override
    public int hashCode() {
    	return this.id.hashCode();
    }

	@Override
	public String toString() {
		return "Message [createDate=" + createDate + ", id=" + id
				+ ", message=" + message + ", title=" + title + ", uri=" + uri
				+ ", user=" + user + "]";
	}
}
