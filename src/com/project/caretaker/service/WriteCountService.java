package com.project.caretaker.service;

import java.io.IOException;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.project.caretaker.utility.LifeLogUtility;
import com.project.caretaker.utility.SleepUtility;

public class WriteCountService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//writeInFile();
	}


	@Override
	public void onStart(Intent intent, int startId) {

		Log.d("WRITE SERVICE START","TRUE");
		
		int walking = intent.getIntExtra("WALKING",0);
		int running = intent.getIntExtra("RUNNING",0);
		int vehicle = intent.getIntExtra("VEHICLE",0);
		int bicycle = intent.getIntExtra("BICYCLE",0);	
		int step = intent.getIntExtra("STEP",0);
		long time = new Date().getTime();

		try {			
			LifeLogUtility.writeLifeLogCountInFile(walking,running,vehicle,bicycle,"lifelog.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Exception in writing in file","TRUE");
			e.printStackTrace();
		}
		finally{
			LifeLogUtility.writeLifeLogCountInCloud(time,walking,running,vehicle,bicycle,this);
			stopService(intent);
		}		
	}





}
