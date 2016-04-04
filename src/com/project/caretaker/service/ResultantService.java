/**
 * @author harshal
 * 
 */
package com.project.caretaker.service;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.project.caretaker.utility.SleepUtility;

public class ResultantService extends Service {
	
	long diff;  //end time - start time
	double actual_sleep; // diff-(counter*30min)
	double difference;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onStart(Intent intent, int startId) {

		//assign constant end time of service
		InteractionRecognitionService.SERVICE_END_TIME = new Date();		
		
		//count variable v2,v3,v4
		countVariable2();
		countVariable3();
		countVariable4();	
		
		//start variable write service
		startVariableWriteService();
		
		//initialize all globals in shared preference
		
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V1","-1");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V2", "-1");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V3", "-1");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V4", "-1");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"COUNTER","0");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"LAST_INTERACTION","");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"SLEEP_START_TIME", "");
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"SLEEP_END_TIME", "");
		
		//stop this service
		stopService(intent);

	}
	
	
	public void countVariable2()
	{
		if(InteractionRecognitionService.counter==0)
			InteractionRecognitionService.v2 = 0;
		else if(InteractionRecognitionService.counter <= 2)
			InteractionRecognitionService.v2 = 1;
		else if(InteractionRecognitionService.counter==3)
			InteractionRecognitionService.v2 = 2;
		else
			InteractionRecognitionService.v2 = 3;
	}
	
	public void countVariable3()
	{
		diff = InteractionRecognitionService.SERVICE_END_TIME.getTime() - InteractionRecognitionService.SERVICE_START_TIME.getTime();
		//res = actual sleep
		long res = diff - (InteractionRecognitionService.counter*30*60*1000);
		if(res>0)
		actual_sleep = Math.floor((res/1000)/3600); //in hour
		else
		actual_sleep = 0;
		difference = (diff/1000)/3600; //in hour
		if(actual_sleep>7)
			InteractionRecognitionService.v3 = 0;
		else if(actual_sleep>=6)
			InteractionRecognitionService.v3 = 1;
		else if(actual_sleep==5)
			InteractionRecognitionService.v3 = 2;
		else
			InteractionRecognitionService.v3 = 3;		
	}
	
	
	public void countVariable4()
	{
		double score = (actual_sleep/difference) * 100;
		if(score > 84)
			InteractionRecognitionService.v4 = 0;
		else if(score >75 && score <= 84)
			InteractionRecognitionService.v4 = 1;
		else if(score > 65 && score <= 75)
			InteractionRecognitionService.v4 = 2;
		else
			InteractionRecognitionService.v4 = 3;
		
	}
	
	private void startVariableWriteService()
	{
		
		Intent intent = new Intent(this,VariableWriteService.class);
		Bundle bundle = new Bundle();
		bundle.putInt("V1",InteractionRecognitionService.v1);
		bundle.putInt("V2",InteractionRecognitionService.v2);
		bundle.putInt("V3",InteractionRecognitionService.v3);
		bundle.putInt("V4",InteractionRecognitionService.v4);
		bundle.putInt("COUNTER", InteractionRecognitionService.counter);
		bundle.putDouble("SLEEP", actual_sleep);
		bundle.putDouble("DIFF", difference);
		intent.putExtra("VARIABLES", bundle);
		startService(intent);
	}
	
}
