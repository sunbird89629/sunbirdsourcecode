package cn.itcast.rss;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.itcast.rss.rsslib4j.RSSItem;

public class ItemItemAdapter extends BaseAdapter {
	
	private Context context;
	private List<RSSItem> iList;
	
	private LayoutInflater inflater;
	
	public ItemItemAdapter(Context context,List<RSSItem> iList){
		this.context=context;
		this.iList=iList;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return iList.size();
	}

	@Override
	public Object getItem(int position) {
		return iList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RSSItem i=iList.get(position);
		if(convertView==null){
			convertView = inflater.inflate(R.layout.item_item, null);
		}
		TextView tv=(TextView) convertView.findViewById(R.id.item_item_tv_title);
		tv.setText("        "+i.getTitle());
		return convertView;
	}

}
