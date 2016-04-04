/**
 * @author harshal
 */
package com.project.caretaker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.project.caretaker.service.InteractionRecognitionService;
import com.project.caretaker.utility.SleepUtility;

public class AlarmReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) { 
		Log.d("Alarm service Started","True");
		if(InteractionRecognitionService.LAST_INTERACTION != null)
		{
			Log.d("Last time",InteractionRecognitionService.LAST_INTERACTION.toString());
			//find diff in milli seonds.
			long diff = InteractionRecognitionService.LAST_INTERACTION.getTime() - InteractionRecognitionService.SERVICE_START_TIME.getTime();
			long diffinmin = (diff/1000)/60;
			if(diffinmin<=15)
				InteractionRecognitionService.v1 = 0;
			else if(diffinmin<=30)
				InteractionRecognitionService.v1 = 2;
			else if(diffinmin<=60)
				InteractionRecognitionService.v1 = 3;
			else 
				InteractionRecognitionService.v1 = 4;

			//store to preference in cash service crashed
			SleepUtility.storeToPreference(context, InteractionRecognitionService.FILE_NAME,"V1", String.valueOf(InteractionRecognitionService.v1));
		}
		else
		{
			InteractionRecognitionService.v1 = 0;
		}

		Log.d("Successfully unregister Alarm Receiver","True");
	}
}