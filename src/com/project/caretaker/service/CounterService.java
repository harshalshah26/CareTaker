package com.project.caretaker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class CounterService extends Service implements SensorEventListener {

	 private SensorManager sensorManager;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } 
	}
	
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(this,"Step detected:"+String.valueOf(event.values[0]),Toast.LENGTH_LONG).show();
		LifeLogService.stepCount = event.values[0];
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sensorManager.unregisterListener(this); 
	}
	

}
