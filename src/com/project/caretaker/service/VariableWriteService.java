package com.project.caretaker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.project.caretaker.utility.SleepUtility;

public class VariableWriteService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onStart(Intent intent, int startId) {

		// TODO Auto-generated method stub
		Log.d("VariableWriteService STARTED", ""+"TRUE");
		Bundle bundle = intent.getBundleExtra("VARIABLES");
		int v1 = bundle.getInt("V1");
		int v2 = bundle.getInt("V2");
		int v3 = bundle.getInt("V3");
		int v4 = bundle.getInt("V4");
		int counter = bundle.getInt("COUNTER");
		double sleep =  bundle.getDouble("SLEEP");
		double diff =  bundle.getDouble("DIFF");
		String email = SleepUtility.readPrefernce(this,"USER_SETTING", "EMAIL","");
			try {
				//this will write to file
				SleepUtility.writeVariableInFile(getApplicationContext(),v1,v2,v3,v4,counter,sleep,diff,"Sleeplog.csv");
			}catch(Exception e)
			{
				Log.d("UNABLE TO WRITE IN FILE due to unknown exception", "Exception:"+e.toString());
			}	
			SleepUtility.writeVariableInCloud(email,v1,v2,v3,v4,counter,sleep,diff);			
			stopService(intent);

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V1", String.valueOf(-1));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"COUNTER", String.valueOf(0));
	}
	
}
