package org.opencv.samples.tutorial3;

import com.yangyjRex.finalwork.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/*
	 *  turn to CameraActivity
	 */
	public void capture(View v){
		Intent intent =new Intent(MainActivity.this,VideoCaptureActivity.class);
	    startActivity(intent); 
	}
	
	/*
	 * turn to LabActivity
	 */
	public void manage(View v){
		Intent intent =new Intent(MainActivity.this,LabActivity.class);
	    startActivity(intent); 
	}
	
}
