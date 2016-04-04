/**
 * 
 * @author harshal
 */

package com.project.caretaker.service;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.project.caretaker.receiver.AlarmReceiver;
import com.project.caretaker.receiver.ScreenReceiver;
import com.project.caretaker.utility.SleepUtility;
import com.project.caretaker.utility.UserInteraction;

public  class InteractionRecognitionService extends Service {
	static public ScreenReceiver screenReceiver;
	private PendingIntent alarmAfter2Hours;

	//constants that keep track of start time and end time of service when button clicked
	public static Date SERVICE_START_TIME;
	public static Date SERVICE_END_TIME;

	//constant that keep track of last usage of phone
	public static Date LAST_INTERACTION;

	//counter that keep track of no of usage of phone
	public static int counter=0;

	//constants that will decide sleep quality index
	public static int v1 =-1;
	public static int v2 =-1;
	public static int v3 =-1;
	public static int v4 =-1;

	//Shared Preference File name
	public static final String FILE_NAME = "USER_SETTING";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("UPDATE SERVICE CREATED", "TRUE");


		//if screen already on while starting service assign start time for user interaction 
		if(isScreenon(InteractionRecognitionService.this))
			UserInteraction.startTime = new Date();

		//set constants of service value from shared preference
		fetchBackSleepGlobals();


		// register receiver that handles screen on and screen off logic
		screenReceiver = new ScreenReceiver(this);
		screenReceiver.registerReciever();

		/* Retrieve a PendingIntent that will perform a broadcast */
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		alarmAfter2Hours = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

		//start alarm after two hours of start service time
		startAlarmAfterTwoHours();





	}
	public static boolean isScreenon(Context context)
	{
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		return isScreenOn;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d("Interactrion SERVICE STARTED", "TRUE");
		fetchBackSleepGlobals();
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		 

		//store global constants with values
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V1", String.valueOf(InteractionRecognitionService.v1));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V2", String.valueOf(InteractionRecognitionService.v2));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V3", String.valueOf(InteractionRecognitionService.v3));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"V4", String.valueOf(InteractionRecognitionService.v4));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"COUNTER", String.valueOf(InteractionRecognitionService.counter));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"LAST_INTERACTION", String.valueOf(InteractionRecognitionService.LAST_INTERACTION));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"SLEEP_START_TIME", String.valueOf(InteractionRecognitionService.SERVICE_START_TIME));
		SleepUtility.storeToPreference(this,InteractionRecognitionService.FILE_NAME,"SLEEP_END_TIME", String.valueOf(InteractionRecognitionService.SERVICE_END_TIME));

		//unregister screen receiver
		Log.d("TAG",screenReceiver.toString());
		screenReceiver.unRegisterReciever();

		super.onDestroy();

	}


	public void startAlarmAfterTwoHours() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Date date = InteractionRecognitionService.SERVICE_START_TIME;       
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, (date.getHours()+2)%24);
		calendar.set(Calendar.MINUTE, date.getMinutes());        
		alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmAfter2Hours);        
		Log.d("Alarm After 2 hours set",calendar.toString());       
	}

	private void fetchBackSleepGlobals()
	{
		//initialize constants
		InteractionRecognitionService.v1 = Integer.valueOf(SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"V1","-1"));
		InteractionRecognitionService.counter = Integer.valueOf(SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"COUNTER","0"));		
		InteractionRecognitionService.v2 = Integer.valueOf(SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"V2","-1"));
		InteractionRecognitionService.v3 = Integer.valueOf(SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"V3","-1"));
		InteractionRecognitionService.v4 =Integer.valueOf(SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"V4","-1"));

		String last_interaction = SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"LAST_INTERACTION","");
		if(last_interaction==null || last_interaction.equals(""))
			InteractionRecognitionService.LAST_INTERACTION = null;
		else
			InteractionRecognitionService.LAST_INTERACTION = new Date(last_interaction);


		String start_time = SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"SLEEP_START_TIME","");
		if(start_time==null || start_time.equals(""))
			InteractionRecognitionService.SERVICE_START_TIME = new Date();
		else
			InteractionRecognitionService.SERVICE_START_TIME = new Date(start_time);


		String end_time = SleepUtility.readPrefernce(getApplicationContext(), InteractionRecognitionService.FILE_NAME,"SLEEP_END_TIME","");
		if(end_time==null || end_time.equals(""))
			InteractionRecognitionService.SERVICE_END_TIME = null;
		else
			InteractionRecognitionService.SERVICE_END_TIME = new Date(end_time);



	}

}