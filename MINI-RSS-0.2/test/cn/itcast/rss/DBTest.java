package cn.itcast.rss;


import android.test.AndroidTestCase;
import cn.itcast.rss.db.DBOpenHelper;

public class DBTest extends AndroidTestCase {
	public void testDB(){
		DBOpenHelper helper=new DBOpenHelper(this.getContext());
		helper.getWritableDatabase();
	}
	public void testString(){
		
	}
}
