package com.example.androidagain;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@ViewA(R.id.main_lv_linkmans)ListView lvLinkmans;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		ActivityHelper.findViewById(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	Toast.makeText(MainActivity.this, "onSaveInstanceState", Toast.LENGTH_LONG).show();
    	super.onSaveInstanceState(outState);
    }
}
