package cn.itcast.rss.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME="";
	private static final int DB_VERSION=4;
	

	// vesion 数据库的版本号。
	public DBOpenHelper(Context context) {
		super(context, "rss.db", null, 4);
	}
	

	// 数据库第一次被穿件的时候调用该方法，适合对数据库中的表结构进行初始化。
	@Override
	public void onCreate(SQLiteDatabase db) {		
		String sql="CREATE TABLE book_channel(_id Integer primary key autoincrement,channel_url varchar(100),title varchar(20),"+
					"img_title varchar(30),img_link varchar(100),img_url varchar(100),desc varchar(50),link varchar(200),"+
					"lang varchar(30),ttl Integer,copyright varchar(100),pub_date varchar(20),"+
					"category varchar(50), item_storage Integer, update_time long)";
		db.execSQL(sql);
		sql="CREATE TABLE item(_id Integer primary key autoincrement,channel_id Integer, title varchar(50), link varchar(100),"+
		           " author varchar(20), guid varchar(200),category varchar(100),pub_date varchar(50),comment varchar(100), "+
		           "desc varchar(300), save_nano long, readed Integer default 0)";
		db.execSQL(sql);
	}

	// 数据库版本号变更的时候调用该方法对数据库中的表结构以及表中的信息进行更行，以适应软件的升级。
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS book_channel");
		db.execSQL("DROP TABLE IF EXISTS item");
		onCreate(db);
	}
	
	

	@Override
	protected void finalize() throws Throwable {
		//TODO 对于sqlite数据库android.database.sqlite.DatabaseObjectNotClosedException的探索。
		this.getReadableDatabase().close();
	}
	
	
	
}
