package com.project.caretaker;

import android.app.Application;

import com.parse.Parse;
import com.project.caretaker.utility.SleepUtility;

public class MyApplication extends Application {

	private String APPLICATION_ID;
	private String CLIENT_KEY;


	@Override
	public void onCreate() {
		super.onCreate();
		//APPLICATION_ID 
		APPLICATION_ID = getResources().getString(R.string.app_id);
		CLIENT_KEY = getResources().getString(R.string.client_id);	  
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

		String userEmail = SleepUtility.getEmail(getApplicationContext());
		SleepUtility.storeToPreference(getApplicationContext(),"USER_SETTING", "EMAIL", userEmail);	
		
		
	}

}
