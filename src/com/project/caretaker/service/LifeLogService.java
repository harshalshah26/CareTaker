/***
 * @author harshal
 * 
 */
package com.project.caretaker.service;



import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.project.caretaker.receiver.AlarmReceiver5Minutes;
import com.project.caretaker.receiver.AlarmReceiverEveryday;
import com.project.caretaker.utility.LifeLogUtility;



public class LifeLogService extends Service implements ConnectionCallbacks,OnConnectionFailedListener  {

	public static GoogleApiClient mAPIClient ;
	Intent serviceIntent;
	PendingIntent pendingIntent;
	Intent stepCountService;
	PendingIntent alarmAfter5minutes;
	PendingIntent alarmEveryDay;
	public static Boolean flag;	
	public static Map<Integer,Integer> activityMap = new HashMap<Integer, Integer>();
	public static Date startTime;
	public static double stepCount;
	public static double previousStepCount;
	public static int WALKING_COUNT;
	public static int RUNNING_COUNT;
	public static int VEHICLE_COUNT;
	public static int BICYCLE_COUNT;
	public static String FILE_NAME = "USER_SETTING";
	public static int ACC_STEP_COUNT;


	@Override
	public void onCreate() {
		super.onCreate();
		//Check Google Play Service Available
		if(isPlayServiceAvailable()) {
			mAPIClient = new GoogleApiClient.Builder(this)
			.addApi(ActivityRecognition.API)
			.addApi(LocationServices.API)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();
			//Connect to Google API
			mAPIClient.connect();
		}else{
			Toast.makeText(this, "Google Play Service not Available", Toast.LENGTH_LONG).show();
		}


		LifeLogService.startTime = new Date();

		//initialize Activity Map
		LifeLogService.initializeActivityMap(); 

		//initialize activity count
		initializeCount();	

		/* Retrieve a PendingIntent that will perform a broadcast */
		Intent alarmIntent = new Intent(this, AlarmReceiver5Minutes.class);
		alarmIntent.setAction("every5minutes");
		alarmAfter5minutes = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

		Intent alarmIntent2 = new Intent(this, AlarmReceiverEveryday.class);
		alarmIntent2.setAction("everyday");
		alarmEveryDay = PendingIntent.getBroadcast(this, 0, alarmIntent2, 0);
		Log.d("LifeLog SERVICE CREATED", "TRUE");
	}	


	public void onConnected(Bundle connectionHint) {
		Log.d("CONNECTED","TRUE");
		//LifeLogService.GOOGLE_API_CLIENT = mAPIClient;		


		//register activityrecognition service
		int time = 15*1000;//15 second
		serviceIntent=new Intent(this,ActivityRecognitionIntentService.class);
		pendingIntent=PendingIntent.getService(this,0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mAPIClient,time,pendingIntent );
		//Toast.makeText(this, "Service start",Toast.LENGTH_SHORT).show();


		//start step count service
		startStepCount();



		//start alarm after five minutes of start service time
		startAlarmAfterFiveMinutes();

		//start alarm everyday for writing data in cloud
		startAlarmEveryDay();
	}
	

	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.d("CONNECTED","FALSE");
		Toast.makeText(this,"ERROR in ACTIVITY detection due to Connection", Toast.LENGTH_SHORT).show();
	}

	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("LifeLog SERVICE STARTED", "TRUE");
		flag = true;
		//initialize Activity Map
		LifeLogService.initializeActivityMap(); 

		//initialize activity count
		initializeCount();
		
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
		LifeLogUtility.storeToPreference(getApplicationContext(), LifeLogService.FILE_NAME,"WALKING",String.valueOf(LifeLogService.WALKING_COUNT));
		LifeLogUtility.storeToPreference(getApplicationContext(), LifeLogService.FILE_NAME,"RUNNING",String.valueOf(LifeLogService.RUNNING_COUNT));
		LifeLogUtility.storeToPreference(getApplicationContext(), LifeLogService.FILE_NAME,"VEHICLE",String.valueOf(LifeLogService.VEHICLE_COUNT));
		LifeLogUtility.storeToPreference(getApplicationContext(), LifeLogService.FILE_NAME,"BICYCLE",String.valueOf(LifeLogService.BICYCLE_COUNT));
		//LifeLogUtility.storeToPreference(getApplicationContext(), LifelogConstants.FILE_NAME,"STEPCOUNT",String.valueOf(LifelogConstants.ACC_STEP_COUNT));
		//stopStepCount();
		stopAlarmAfterFiveMinutes();
		stopAlarmEveryDay();
		flag = false;
		ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mAPIClient, pendingIntent);
		stopService(serviceIntent);
		super.onDestroy();
		Log.d("Lifelod destroyed","true");
	}

	//user defined methods

	
	/***
	 * this method will check if Google play service is available or not
	 * 
	 * 
	 */

	private boolean isPlayServiceAvailable() {
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
	}	

		public void startStepCount()
		{
			stepCountService = new Intent(this,CounterService.class);
			startService(stepCountService);
		}
		
		public void stopStepCount()
		{
			//stepCountService = new Intent(this,CounterService.class);
			stopService(stepCountService);
		}

	private void startAlarmAfterFiveMinutes() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Date date = LifeLogService.startTime;        
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,date.getTime(),5*60*1000, alarmAfter5minutes);        
		Log.d("Alarm After 5 minutes set",date.toString());       
	}

	private void stopAlarmAfterFiveMinutes() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		Intent alarmIntent2 = new Intent(this, AlarmReceiverEveryday.class);
//		alarmIntent2.setAction("everyday");
//		alarmEveryDay = PendingIntent.getBroadcast(this, 0, alarmIntent2, 0);
		alarmManager.cancel(alarmAfter5minutes);

	}

	private void startAlarmEveryDay()
	{
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);    
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE, 45); 
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),24*60*60*1000, alarmEveryDay);        
		Log.d("Alarm for everyday set",calendar.toString()); 
	}

	private void stopAlarmEveryDay()
	{
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);    
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY,23);
//		calendar.set(Calendar.MINUTE, 58); 
		alarmManager.cancel(alarmEveryDay);        
		//Log.d("Alarm for everyday set",calendar.toString()); 
	}

	public void initializeCount()
	{
		LifeLogService.WALKING_COUNT = Integer.valueOf(LifeLogUtility.readPrefernce(getApplicationContext(),LifeLogService.FILE_NAME,"WALKING", "0"));
		LifeLogService.RUNNING_COUNT = Integer.valueOf(LifeLogUtility.readPrefernce(getApplicationContext(),LifeLogService.FILE_NAME,"RUNNING", "0"));
		LifeLogService.VEHICLE_COUNT = Integer.valueOf(LifeLogUtility.readPrefernce(getApplicationContext(),LifeLogService.FILE_NAME,"VEHICLE", "0"));
		LifeLogService.BICYCLE_COUNT = Integer.valueOf(LifeLogUtility.readPrefernce(getApplicationContext(),LifeLogService.FILE_NAME,"BICYCLE", "0"));
		//LifelogConstants.ACC_STEP_COUNT = Integer.valueOf(LifeLogUtility.readPrefernce(getApplicationContext(),LifelogConstants.FILE_NAME,"STEPCOUNT", "0"));

	}
	
	public static void initializeActivityMap() 
	{
		activityMap.clear();
		//activityMap = new HashMap<Integer, Integer>();		
		activityMap.put(DetectedActivity.IN_VEHICLE,-1);
		activityMap.put(DetectedActivity.ON_BICYCLE,-1);
		activityMap.put(DetectedActivity.ON_FOOT,-1);
		activityMap.put(DetectedActivity.RUNNING,-1);
		activityMap.put(DetectedActivity.STILL,0);
		activityMap.put(DetectedActivity.TILTING,-1);
		activityMap.put(DetectedActivity.UNKNOWN,-1);
		activityMap.put(DetectedActivity.WALKING,-1);		
	}




}
