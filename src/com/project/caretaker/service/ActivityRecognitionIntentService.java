/***
 * @author harshal
 * 
 */
package com.project.caretaker.service;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionIntentService extends IntentService {

	
	public ActivityRecognitionIntentService() {
		// TODO Auto-generated constructor stub
		super("INTENTSERVICE");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("ONHANDLE CALLED","TRUE");
		
		//if(getBatteryLevel()>90.0f)
		//{
			doStuff(intent);
		//}
		//else
			//stopService(new Intent(getApplicationContext(),LifeLogService.class));
		

		
	}
	
	
	public float getBatteryLevel() {
	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }

	    return ((float)level / (float)scale) * 100.0f; 
	}
	
	private void doStuff(Intent intent)
	{
		if(LifeLogService.flag!=null && LifeLogService.flag==true)
		{
			if(ActivityRecognitionResult.hasResult(intent)) {
				Log.d("Activity recognized","True");

				//Extract the result from the Response 
				ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
				DetectedActivity detectedActivity = result.getMostProbableActivity();

				//Get the Confidence and Name of Activity 
				int confidence = detectedActivity.getConfidence();     

				if(detectedActivity.getType()==DetectedActivity.ON_FOOT)
				{
					detectedActivity=walkingOrRunning(result.getProbableActivities(),detectedActivity);
				}


				int key = detectedActivity.getType();
				if(LifeLogService.activityMap!=null)
				{
					try{
						int value = LifeLogService.activityMap.get(key);     
						Log.d("KEY VALUE",String.valueOf(key)+"  "+String.valueOf(value));
						LifeLogService.activityMap.put(key, ++value);
						Log.d("In map","True");
					}catch(Exception e)
					{
						Log.d("Exception Catch",e.toString());
					}
				
				}


				String mostProbableName = getActivityName(detectedActivity.getType());
				//long time =  result.getTime();


				Log.d("ACTIVITY",mostProbableName);
				
				//			Log.d("CONFIDENCE",""+confidence);
				//			Log.d("TIME",""+time);				
			}


		}
	}

	private String getActivityName(int type) {
		switch (type)
		{
		case DetectedActivity.WALKING:
			return "Walking";
		case DetectedActivity.IN_VEHICLE:
			return "In Vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "On Bicycle";
		case DetectedActivity.ON_FOOT:
			return "On Foot";		
		case DetectedActivity.STILL:
			return "Still";
		case DetectedActivity.TILTING:
			return "Tilting";
		case DetectedActivity.RUNNING:
			return "Running";
		case DetectedActivity.UNKNOWN:
			return "Unknown";
		}
		return "N/A";
	}




	private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities,DetectedActivity myActivity ) {
		int confidence = 0;
		for (DetectedActivity activity : probableActivities) {
			if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
				continue;

			if (activity.getConfidence() > confidence)
				myActivity = activity;
		}

		return myActivity;
	}







}
