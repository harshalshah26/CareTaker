package com.project.caretaker.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.project.caretaker.utility.SleepUtility;

public class SleepWriteService extends Service {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("WRITE SERVICE STARTED", ""+"TRUE");
		Bundle bundle = intent.getBundleExtra("INTERACTION BUNDLE");
		String startTime = bundle.getString("START TIME");
		String endTime = bundle.getString("END TIME");
		Long duration = bundle.getLong("DURATION");
		String email = bundle.getString("EMAIL");
			try {
				//this will write to file
				SleepUtility.writeInteractionInFile(getApplicationContext(),startTime,endTime,duration,SleepUtility.createFileName(Calendar.getInstance()));
				}catch(Exception e)
			{
				Log.d("UNABLE TO WRITE IN FILE due to unknown exception", "Exception:"+e.toString());
			}	
			SleepUtility.writeInteractionInCloud(email,startTime, endTime, duration);
			stopService(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	
}
