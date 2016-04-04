package com.project.caretaker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.project.caretaker.service.LifeLogService;

public class BootCompleteReceiver extends BroadcastReceiver {   

    @Override  
    public void onReceive(Context context, Intent intent) { 
    	 Intent service = new Intent(context, LifeLogService.class);  
         context.startService(service); 
    }  

}