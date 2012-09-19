package cn.itcast.rss.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.itcast.rss.rsslib4j.RSSChannel;
import cn.itcast.rss.rsslib4j.RSSImage;

public class RSSChannelService {

	private DBOpenHelper helper;
	private SQLiteDatabase wdb;

	public RSSChannelService(Context context) {
		helper = new DBOpenHelper(context);
	}

	/**
	 * 向表中插入一个实体。
	 * 
	 * @param RSSChannel
	 */
	public void save(RSSChannel c) {
		wdb = helper.getWritableDatabase();
		save(c, wdb);
		wdb.close();
	}

	public void save(RSSChannel c, SQLiteDatabase wdb) {
		if (c.getRSSImage() == null) {
			c.setRSSImage(new RSSImage());
		}
		String sql = "INSERT INTO book_channel(title, channel_url, img_title, img_link, img_url, desc, link, lang, ttl, copyright, pub_date, update_time) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] bindArgs = new Object[] { c.getTitle(), c.getChannelUrl(),
				c.getRSSImage().getTitle(), c.getRSSImage().getLink(),
				c.getRSSImage().getUrl(), c.getDescription(), c.getLink(),
				c.getLanguage(), c.getTTL(), c.getCopyright(), c.getPubDate(),
				c.getPubDate() };
		wdb.execSQL(sql, bindArgs);
	}

	/**
	 * 向表中插入一个实体,返回插入实体的id。
	 * 
	 * @param RSSChannel
	 */
	public int saveAndReturnId(RSSChannel c) {
		wdb = helper.getWritableDatabase();
		Integer id = null;
		save(c, wdb);
		String sql = "SELECT _id FROM book_channel where channel_url='"
				+ c.getChannelUrl() + "'";
		Cursor cursor = null;
		try {
			cursor = wdb.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				id = cursor.getInt(0);
			}
		} finally {
			cursor.close();
			wdb.close();
		}
		return id;
	}

	public void delete(int id) {
		wdb = helper.getWritableDatabase();
		String sql = "DELETE FROM book_channel WHERE _id=" + id;
		wdb.execSQL(sql);
		wdb.close();
	}

	public void update(RSSChannel c) {
		wdb = helper.getWritableDatabase();
		String sql = "UPDATE book_channel SET title=?,img_title=?,img_link=?,img_url=?,desc=?,link=?,lang=?,ttl=?,copyright=?,pub_date=?,update_time=? WHERE _id=?";
		if (c.getRSSImage() == null) {
			c.setRSSImage(new RSSImage());
		}
		Object[] bindArgs = new Object[] { c.getTitle(),
				c.getRSSImage().getTitle(), c.getRSSImage().getLink(),
				c.getRSSImage().getUrl(), c.getDescription(), c.getLink(),
				c.getTTL(), c.getCopyright(), c.getPubDate(), c.getId(),
				c.getPubDate() };
		wdb.execSQL(sql, bindArgs);
		wdb.close();
	}

	public RSSChannel getById(int id) {
		wdb = helper.getWritableDatabase();
		RSSChannel c = null;
		String sql = "SELECT * FROM book_channel";
		Cursor cursor = null;
		try {
			cursor = wdb.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				c = curCursor2RSSChannel(cursor);
			}
		} finally {
			cursor.close();
			wdb.close();
		}
		return c;
	}

	public String getChannelUrlById(int id) {
		wdb = helper.getWritableDatabase();
		String channelUrl = null;
		String sql = "SELECT channel_url FROM book_channel WHERE _id=" + id;
		Cursor cursor = null;
		try {
			cursor = wdb.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				channelUrl = cursor.getString(0);
			}
		} finally {
			cursor.close();
			wdb.close();
		}
		return channelUrl;
	}

	/**
	 * 将数据库中存储的所有的RSSChannel取出来，封装成一个List返回。如果当 钱数据库中没有 记录，就返回一个有0条记录的List对象。
	 * 
	 * @return
	 */
	public List<RSSChannel> getAll() {
		wdb = helper.getWritableDatabase();
		List<RSSChannel> cList = new ArrayList<RSSChannel>();
		String sql = "SELECT * FROM book_channel";
		Cursor cursor = null;
		try {
			cursor = wdb.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				cList.add(curCursor2RSSChannel(cursor));
			}
		} finally {
			cursor.close();
			wdb.close();
		}
		return cList;
	}

	/**
	 * 把游标当前所指的那条记录取出，封装成RSSChannel对象。
	 * 
	 * @return channel
	 */
	private RSSChannel curCursor2RSSChannel(Cursor c) {
		wdb = helper.getWritableDatabase();
		RSSChannel channel = null;
		if (c != null) {
			// _id title channel_url img_title img_link img_url desc link lang
			// ttl copyright pub_date category
			channel = new RSSChannel();
			channel.setId(c.getInt(c.getColumnIndex("_id")));
			channel.setTitle(c.getString(c.getColumnIndex("title")));
			channel.setChannelUrl(c.getString(c.getColumnIndex("channel_url")));

			RSSImage img = new RSSImage();
			img.setTitle(c.getString(c.getColumnIndex("img_title")));
			img.setLink(c.getString(c.getColumnIndex("img_link")));
			img.setUrl(c.getString(c.getColumnIndex("img_url")));
			channel.setRSSImage(img);
			
			channel.setDescription(c.getString(c.getColumnIndex("desc")));
			channel.setLink(c.getString(c.getColumnIndex("link")));
			channel.setLanguage(c.getString(c.getColumnIndex("lang")));
			channel.setTTL(String.valueOf(c.getInt(c.getColumnIndex("ttl"))));
			channel.setCopyright(c.getString(c.getColumnIndex("copyright")));
			channel.setPubDate(c.getString(c.getColumnIndex("pub_date")));
			channel.setUpdateTime(c.getLong(c.getColumnIndex("update_time")));
		}
		wdb.close();
		return channel;
	}
}
