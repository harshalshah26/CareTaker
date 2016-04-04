package com.project.caretaker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.project.caretaker.service.LifeLogService;
import com.project.caretaker.service.WriteCountService;
import com.project.caretaker.utility.LifeLogUtility;

public class AlarmReceiverEveryday extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) { 
		Log.d("In everyday","True");		
		Log.d("Successfully unregister Alarm Receiver","True");
		startWriteCountService(context);
		LifeLogService.WALKING_COUNT = 0;
		LifeLogService.RUNNING_COUNT = 0;
		LifeLogService.VEHICLE_COUNT = 0;
		LifeLogService.BICYCLE_COUNT = 0;
		//LifelogConstants.ACC_STEP_COUNT=0;
		LifeLogUtility.storeToPreference(context, LifeLogService.FILE_NAME,"WALKING","0");
		LifeLogUtility.storeToPreference(context, LifeLogService.FILE_NAME,"RUNNING","0");
		LifeLogUtility.storeToPreference(context, LifeLogService.FILE_NAME,"VEHICLE","0");
		LifeLogUtility.storeToPreference(context, LifeLogService.FILE_NAME,"BICYCLE","0");
		LifeLogUtility.storeToPreference(context, LifeLogService.FILE_NAME,"STEP","0");
	}
	
	public void startWriteCountService(Context context)
	{
		Intent intent = new Intent(context,WriteCountService.class);
		intent.putExtra("WALKING", LifeLogService.WALKING_COUNT);
		intent.putExtra("RUNNING", LifeLogService.RUNNING_COUNT);
		intent.putExtra("VEHICLE", LifeLogService.VEHICLE_COUNT);
		intent.putExtra("BICYCLE", LifeLogService.BICYCLE_COUNT);
		//intent.putExtra("STEP",LifelogConstants.ACC_STEP_COUNT);
		context.startService(intent);
		
	}

	
	

}