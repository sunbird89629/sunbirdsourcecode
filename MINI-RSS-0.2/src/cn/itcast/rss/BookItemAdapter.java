package cn.itcast.rss;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookItemAdapter extends BaseAdapter{
	private Context context;
	private List<BookItem> bookItemList;
	private LayoutInflater inflater;
	
	public BookItemAdapter(Context context){
		this.context=context;
		inflater=LayoutInflater.from(context);
		bookItemList=new ArrayList<BookItem>();
	}
	
	public void addItem(int imgResource,int strResource){
		bookItemList.add(new BookItem(imgResource, strResource));
	}
	
	@Override
	public int getCount() {
		return bookItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return bookItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=inflater.inflate(R.layout.book_item, null);
		}
		ImageView ivItemLogo = (ImageView) convertView.findViewById(R.id.bi_iv_itemlogo);
		ivItemLogo.setImageResource(bookItemList.get(position).getImgResource());
		TextView tvItemName=(TextView) convertView.findViewById(R.id.bi_iv_itemname);
		tvItemName.setText(bookItemList.get(position).getStrResource());
		return convertView;
	}
	
	private class BookItem{
		private int imgResource;
		private int strResource;
		public BookItem(int imgResource,int strResource){
			this.imgResource=imgResource;
			this.strResource=strResource;
		}
		public int getImgResource() {
			return imgResource;
		}
		public void setImgResource(int imgResource) {
			this.imgResource = imgResource;
		}
		public int getStrResource() {
			return strResource;
		}
		public void setStrResource(int strResource) {
			this.strResource = strResource;
		}
	}
}
