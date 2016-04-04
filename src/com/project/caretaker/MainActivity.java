package com.project.caretaker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.project.caretaker.service.InteractionRecognitionService;
import com.project.caretaker.service.LifeLogService;
import com.project.caretaker.service.ResultantService;
public class MainActivity extends Activity {

	Button start,stop;
	Intent interactionService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button)findViewById(R.id.startButton);
		stop = (Button)findViewById(R.id.stopButton);
		
		if(isMyServiceRunning(InteractionRecognitionService.class))
		{
			start.setEnabled(false);
		}
		else
		{
			stop.setEnabled(false);
		}
		
		start.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startInteractionService();			
				Toast.makeText(MainActivity.this,"Sleep Service Start",Toast.LENGTH_SHORT).show();
				start.setEnabled(false);
				stop.setEnabled(true);				
			}
		});
		
		
		stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(isMyServiceRunning(InteractionRecognitionService.class))
				{
					stopInteractionService();													
					startVariableCountService();
					Toast.makeText(MainActivity.this,"Sleep Service stop",Toast.LENGTH_SHORT).show();
					start.setEnabled(true);
					stop.setEnabled(false);	
					
				}
			}
		});
		
		if(!isMyServiceRunning(LifeLogService.class)){
		Intent intent = new Intent(this, LifeLogService.class);
		startService(intent);
		}
		
		
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
	
	
	/**
	 * user defined methods 
	 *	 
	 */
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	public void startInteractionService()
	{
		interactionService = new Intent(MainActivity.this, InteractionRecognitionService.class);
		startService(interactionService);
	}
	
	public void stopInteractionService()
	{

		interactionService = new Intent(MainActivity.this, InteractionRecognitionService.class);
		stopService(interactionService);
	}
	
	public void startVariableCountService()
	{
		interactionService = new Intent(MainActivity.this, ResultantService.class);
		startService(interactionService);
	}
	
	
	
	
}
