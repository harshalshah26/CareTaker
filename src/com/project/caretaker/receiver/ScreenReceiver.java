/**
 * 
 * @author harshal
 */
package com.project.caretaker.receiver;

import java.util.Date;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.project.caretaker.service.InteractionRecognitionService;
import com.project.caretaker.service.SleepWriteService;
import com.project.caretaker.utility.LifeLogUtility;
import com.project.caretaker.utility.SleepUtility;
import com.project.caretaker.utility.UserInteraction;




public class ScreenReceiver extends BroadcastReceiver {	
	
	boolean screenLock;
	Context context;
	IntentFilter screenStateFilter;
	
	  public ScreenReceiver(Context context) {
	        this.context = context;
	        screenStateFilter   = new IntentFilter();
	        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
	        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
	    }
	    public void registerReciever(){
	        context.registerReceiver(this, screenStateFilter);
	    }
	    public void unRegisterReciever(){
	        context.unregisterReceiver(this);
	    }

	@Override
	public void onReceive(Context context, Intent intent) {
		screenLock = isScreenLock(context); //get screen lock status
		UserInteraction.isCycleContinue = isCallGoingOn(context);
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			//CODE for OFF screen        	
			//set end time of interaction
			UserInteraction.endTime = new Date(System.currentTimeMillis()) ;
			Log.d("SCREEN STATUS", "OFF");
			Log.d("LOCK STATUS",""+screenLock);
			Log.d("CALL STATUS",""+UserInteraction.isCycleContinue);
			Log.d("WAS CALL GOING ON",""+UserInteraction.wasCallGoingOn);
			Log.d("ISRINGING STATUS",""+UserInteraction.isRinging);
			Log.d("Screen Off DATE/TIME",SleepUtility.getTimeInStringFormat(UserInteraction.endTime));
			
			//set global constant LAST_INTERACTION
			InteractionRecognitionService.LAST_INTERACTION = new Date();
			Log.d("Last Interaction Found",InteractionRecognitionService.LAST_INTERACTION.toString());
			
		
			if(!UserInteraction.isCycleContinue && UserInteraction.startTime != null)
			{
				UserInteraction.wasCallGoingOn = false;  //make the flag false for next start time assign
				UserInteraction.duration = SleepUtility.getDuration(UserInteraction.startTime.getTime(),UserInteraction.endTime.getTime());
				//User activity found if screen is not locked before goes to light off
				if(UserInteraction.duration > 30 || !screenLock)
				{
					Log.d("USER INTERACTION", "TRUE");
					Log.d("Activity Found for ", SleepUtility.getDurationFormat(UserInteraction.duration));
					InteractionRecognitionService.counter +=1;
					Log.d("COUNTER", ""+InteractionRecognitionService.counter);
					startWriteService(context, UserInteraction.duration);
				}				
				UserInteraction.startTime = null;
			}
			
			

		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			
			//GlobalConstants.counter +=1;			
			Log.d("SCREEN STATUS", "ON");
			Log.d("LOCK STATUS",""+screenLock);
			Log.d("CALL STATUS",""+UserInteraction.isCycleContinue);
			Log.d("WAS CALL GOING ON",""+UserInteraction.wasCallGoingOn);
			Log.d("ISRINGING STATUS",""+UserInteraction.isRinging);

			if(!UserInteraction.isCycleContinue && !UserInteraction.wasCallGoingOn) //start time may be null here
			{
				Log.d("Normal case", "TRUE");
				if(UserInteraction.startTime != null)
				{
					//case when caller cut the phone , light then turn on so put it in file and start new cycle
					Log.d("Caller cut the phone","true");
					UserInteraction.endTime = new Date(System.currentTimeMillis());
					UserInteraction.duration = SleepUtility.getDuration(UserInteraction.startTime.getTime(),UserInteraction.endTime.getTime());
					startWriteService(context, UserInteraction.duration);
					
				}
				//here means start a new interaction cycle
				UserInteraction.startTime = new Date(System.currentTimeMillis());
				Log.d("New Cycle", "TRUE");
			}			
			else if(UserInteraction.isCycleContinue && !UserInteraction.wasCallGoingOn)
			{
				//case when screen off and phone rings turn light on
				Log.d("else If ", "TRUE");
				
				if(UserInteraction.startTime!=null)
				{
					//case if phone is already in use and ring comes
					Log.d(" If 2 ", "TRUE");
					UserInteraction.endTime =new Date(System.currentTimeMillis());
					UserInteraction.duration = SleepUtility.getDuration(UserInteraction.startTime.getTime(),UserInteraction.endTime.getTime());
					startWriteService(context, UserInteraction.duration);
				}
				UserInteraction.startTime = new Date(System.currentTimeMillis());
					
			}			
			else if(UserInteraction.startTime == null) //this case must be at last of all cases
			{
				//if none of the case then start time must be initialized
				Log.d("In last case","TRUE");
				UserInteraction.startTime = new Date(System.currentTimeMillis());
			}
			
			if(UserInteraction.startTime!=null)
			Log.d("Screen On DATE/TIME", SleepUtility.getTimeInStringFormat(UserInteraction.startTime));


		}
	}

	public boolean isScreenLock(Context context)
	{
		KeyguardManager km = (KeyguardManager)context. getSystemService(Context.KEYGUARD_SERVICE);
		boolean lock = km.inKeyguardRestrictedInputMode();
		return lock;
	}
	
	
	//function to determine whether cycle is continue or not
	public boolean isCallGoingOn(Context context)
	{
		TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		switch(tm.getCallState())
		{
			case TelephonyManager.CALL_STATE_OFFHOOK:				
				UserInteraction.wasCallGoingOn = true; //flag for stop next start time assign
				UserInteraction.isRinging = false;
				return true;
			case TelephonyManager.CALL_STATE_RINGING:	
				UserInteraction.isRinging = true;
			case TelephonyManager.DATA_ACTIVITY_INOUT:
				
				return true;
			case TelephonyManager.CALL_STATE_IDLE:
				UserInteraction.wasCallGoingOn = false;
				
				
		}
		
		return false;
	}



	//start service to write user interaction in files
	public void startWriteService(Context context,long duration)
	{
		String email = SleepUtility.readPrefernce(context,"USER_SETTING","EMAIL", "");
		Intent writeService = new Intent(context, SleepWriteService.class);
		Bundle bundle = new Bundle();
		Log.d("TEST", UserInteraction.startTime.toString());
		bundle.putString("START TIME", SleepUtility.getTimeInStringFormat(UserInteraction.startTime));
		bundle.putString("END TIME",   SleepUtility.getTimeInStringFormat(UserInteraction.endTime));
		bundle.putLong("DURATION", duration);
		bundle.putString("EMAIL",email);
		writeService.putExtra("INTERACTION BUNDLE", bundle);
		context.startService(writeService);
		UserInteraction.startTime = null; //it is necessary for start of next cycle
	}

//	private static class UserInteraction {
//	
//	public static Date startTime;
//	public static Date endTime ;
//	public static long duration; 
//	public static boolean isCycleContinue;
//	public static boolean wasCallGoingOn; 
//	public static boolean isRinging;//flag to check call was goin on or not
//
//}

	
	




}