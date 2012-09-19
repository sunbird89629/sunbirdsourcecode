package zyf.myAnimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class myAnimation extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	private Button button_toTestAnimation;
	private Button bt_last, bt_replay, bt_next, bt_list, bt_exit;
	protected Animation animation;
	private int[] ID;
	private MenuItem back_Menu, next_Menu, restart_Menu, selsect_Menu,
			exit_Menu;
	private final int Back_Menu_ID = Menu.FIRST + 1;
	private final int Next_Menu_ID = Menu.FIRST + 2;
	private final int Selsect_Menu_ID = Menu.FIRST + 3;
	private final int Restart_Menu_ID = Menu.FIRST + 4;
	private final int Exit_Menu_ID = Menu.FIRST + 5;
	private int the_Animation_ID = 0;
	private int i = 0;
	private int requestCode = 110;
	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ID = new int[] { R.anim.myanimation_simple, R.anim.my_alpha_action,
				R.anim.my_scale_action, R.anim.my_translate_action,
				R.anim.my_rotate_action, R.anim.alpha_scale,
				R.anim.alpha_translate, R.anim.alpha_rotate,
				R.anim.scale_translate, R.anim.scale_rotate,
				R.anim.translate_rotate, R.anim.alpha_scale_translate,
				R.anim.alpha_scale_rotate, R.anim.alpha_translate_rotate,
				R.anim.scale_translate_rotate,
				R.anim.alpha_scale_translate_rotate, R.anim.myown_design };
		findMyButton();
		myButtonSetListener();
	}

	private void myButtonSetListener() {
		// TODO Auto-generated method stub
		bt_last.setOnClickListener(this);
		bt_replay.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		bt_list.setOnClickListener(this);
		bt_exit.setOnClickListener(this);
		button_toTestAnimation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void findMyButton() {
		// TODO Auto-generated method stub
		button_toTestAnimation = (Button) findViewById(R.id.Button01);
		bt_last = (Button) findViewById(R.id.button_Last);
		bt_replay = (Button) findViewById(R.id.button_Replay);
		bt_next = (Button) findViewById(R.id.button_Next);
		bt_list = (Button) findViewById(R.id.button_List);
		bt_exit = (Button) findViewById(R.id.button_Exit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		back_Menu = menu.add(0, this.Back_Menu_ID, 1, "上一个动画");
		next_Menu = menu.add(0, this.Next_Menu_ID, 2, "下一个动画");
		restart_Menu = menu.add(0, this.Restart_Menu_ID, 3, "重新播放");
		selsect_Menu = menu.add(0, this.Selsect_Menu_ID, 4, "选择动画");
		exit_Menu = menu.add(0, this.Exit_Menu_ID, 5, "退出");
		back_Menu.setIcon(R.drawable.ic_menu_back);
		next_Menu.setIcon(R.drawable.ic_menu_next);
		restart_Menu.setIcon(R.drawable.ic_menu_restart);
		selsect_Menu.setIcon(R.drawable.ic_menu_select);
		exit_Menu.setIcon(R.drawable.ic_power);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == this.Exit_Menu_ID) {
			finish();
		} else if (item.getItemId() == this.Selsect_Menu_ID) {
			Intent forIntent = new Intent();
			forIntent.setClass(myAnimation.this, MYListActivity.class);
			startActivityForResult(forIntent, requestCode);
		} else {
			switch (item.getItemId()) {
			case Back_Menu_ID: {
				toLastIndex();
			}
				break;
			case Next_Menu_ID: {
				toNextIndex();
			}
				break;
			default:
				break;
			}
			load_start_Animation(i);
		}
		return super.onOptionsItemSelected(item);
	}


	private void load_start_Animation(int i) {

		the_Animation_ID = ID[i];
		if (the_Animation_ID != 0) {
			animation = AnimationUtils.loadAnimation(this, the_Animation_ID);
			button_toTestAnimation.startAnimation(animation);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == this.requestCode) {
			if (resultCode == RESULT_OK) {
				try {

					bundle = data.getExtras();
					i = bundle.getInt("INDEX");
					load_start_Animation(i);

				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "返回取消", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "无返回!", Toast.LENGTH_LONG).show();
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(bt_exit)) {
			finish();
		} else if (v.equals(bt_list)) {
			Intent forIntent = new Intent();
			forIntent.setClass(myAnimation.this, MYListActivity.class);
			startActivityForResult(forIntent, requestCode);
		} else {
			switch (v.getId()) {
			case R.id.button_Last: {
				toLastIndex();
			}
				break;
			case R.id.button_Next: {
				toNextIndex();
			}
				break;
			default:
				break;
			}
			load_start_Animation(i);
		}
	}

	private void toLastIndex() {
		--i;
		if (i < 0) {
			i = ID.length - 1;
		}
	}

	private void toNextIndex() {
		++i;
		if (i >= ID.length) {
			i = 0;
		}
	}
}