package com.project.caretaker.receiver;

import java.util.Date;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.project.caretaker.service.ActivityWriteService;
import com.project.caretaker.service.CounterService;
import com.project.caretaker.service.LifeLogService;
import com.project.caretaker.utility.LifeLogUtility;
import com.project.caretaker.utility.ValueComparator;

public class AlarmReceiver5Minutes extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) { 

		Log.d("Alarm service Started","True");		

			TreeMap<Integer, Integer> sortedMap = new TreeMap<>(new ValueComparator(LifeLogService.activityMap));
			sortedMap.putAll(LifeLogService.activityMap);
			//int firstkey=0;
			//if(sortedMap!=null)
			try{
			int firstkey = sortedMap.firstKey();
			double finalStep = LifeLogService.stepCount-LifeLogService.previousStepCount;
			String activity = "N/A";
			//LifelogConstants.ACC_STEP_COUNT += finalStep;
			if(firstkey == DetectedActivity.STILL || firstkey == DetectedActivity.UNKNOWN || firstkey == DetectedActivity.TILTING)
			{
				if(finalStep>75)
					activity = LifeLogUtility.getActivityName(DetectedActivity.WALKING);	
				else if(finalStep>150)
					activity = LifeLogUtility.getActivityName(DetectedActivity.RUNNING);	
				else
					activity = LifeLogUtility.getActivityName(firstkey);

			}
			else
			activity = LifeLogUtility.getActivityName(firstkey);
			incrementCount(firstkey);
			Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(LifeLogService.mAPIClient);
			Double latitude = null,longitude=null,altitude=null;
			if(mLastLocation!=null)
			{
				latitude = mLastLocation.getLatitude();
				longitude = mLastLocation.getLongitude();
				altitude = mLastLocation.getAltitude();
				Log.d("LOCATION",mLastLocation.toString());

			}				
			startWriteService(context,activity, LifeLogService.startTime,latitude,longitude,altitude,finalStep);
			LifeLogService.startTime = LifeLogUtility.addFiveMinutes(LifeLogService.startTime);
			LifeLogService.initializeActivityMap();				
			Log.d("TreeMap",sortedMap.toString());
			LifeLogService.previousStepCount = LifeLogService.stepCount;
			Log.d("Total Step:",String.valueOf(LifeLogService.stepCount));
			
		Log.d("Successfully unregister Alarm Receiver","True");
			}catch(Exception e)
			{
				Log.d("Exception catch from Alarmreceiver", e.toString());
			}

	}

	public void startWriteService(Context context,String activity,Date time,Double latitude,Double longitude,Double altitude,double step)
	{
		Intent writeService = new Intent(context,ActivityWriteService.class);
		writeService.putExtra("ACTIVITY",activity);
		writeService.putExtra("TIME",time.getTime());
		writeService.putExtra("LATITUDE",latitude);
		writeService.putExtra("LONGITUDE",longitude);
		writeService.putExtra("ALTITUDE",altitude);
		writeService.putExtra("STEP",step);
		context.startService(writeService);
	}

	public void incrementCount(int activity)
	{
		switch(activity)
		{
		case DetectedActivity.IN_VEHICLE:
			LifeLogService.VEHICLE_COUNT++;
			break;
		case DetectedActivity.WALKING:
			LifeLogService.WALKING_COUNT++;
			break;
		case DetectedActivity.RUNNING:
			LifeLogService.RUNNING_COUNT++;
			break;
		case DetectedActivity.ON_BICYCLE:
			LifeLogService.BICYCLE_COUNT++;
			break;
		}
	}
	
	

}