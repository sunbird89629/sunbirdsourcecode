package cn.itcast.rss;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;
import cn.itcast.rss.util.G;
import cn.itcast.rss.util.MainUtil;

public class SetBgActivity extends Activity {

	private ImageSwitcher is;
	private Gallery gl;
	private ImageButton ibOk;
	private ImageButton ibBack;
	
	private int curBgId;
	
	private Integer[] mThumbIds={R.drawable.main_bg1, R.drawable.main_bg2, R.drawable.main_bg3, R.drawable.main_bg4};
	private Integer[] mImageIds={R.drawable.main_bg1, R.drawable.main_bg2, R.drawable.main_bg3, R.drawable.main_bg4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_bg);
		MainUtil.initConfig(this,R.id.set_bg_rl);
		
		LinearLayout layout=(LinearLayout) this.findViewById(R.id.set_bg_ll_menu);
		layout.getBackground().setAlpha(G.BG_ALPHA);
		
		is=(ImageSwitcher) this.findViewById(R.id.set_bg_is);
		gl=(Gallery) this.findViewById(R.id.set_bg_gl);
		ibOk=(ImageButton) this.findViewById(R.id.set_bg_ib_ok);
		ibBack=(ImageButton) this.findViewById(R.id.set_bg_ib_back);
		
		is.setFactory(new ViewFactory(){
			@Override
			public View makeView() {
				ImageView i = new ImageView(SetBgActivity.this);
				i.setBackgroundColor(0xFF000000);
				i.setScaleType(ImageView.ScaleType.FIT_CENTER);
				i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				i.setScaleType(ScaleType.FIT_XY);
				return i;
			}
		});
		
		gl.setAdapter(new ImageAdapter());
		gl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				curBgId=mImageIds[position];
				is.setImageResource(mImageIds[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		ibOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sp=SetBgActivity.this.getSharedPreferences(G.MAIN_CONFIG_SP_NAME,Context.MODE_PRIVATE );
				sp.edit().putInt(G.BG_SP_MANE,curBgId ).commit();
				setResult(G.SET_BG_OK_RESULT_CODE);
				SetBgActivity.this.finish();
			}
		});
		ibBack.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				SetBgActivity.this.finish();
			}
		});
	}
	
	private class ImageAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return  mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			return mThumbIds[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = new ImageView(SetBgActivity.this);
			}
			ImageView i=(ImageView) convertView;

			i.setImageResource(mThumbIds[position]);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setScaleType(ScaleType.FIT_XY);
			return i;
		}
		
	}
	
}
