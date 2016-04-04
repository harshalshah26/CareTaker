package com.project.caretaker.service;

import java.io.IOException;
import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.project.caretaker.utility.LifeLogUtility;

public class ActivityWriteService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//writeInFile();
	}
	
	
	 @Override
	    public void onStart(Intent intent, int startId) {
	  
		 Log.d("WRITE SERVICE START","TRUE");
		 int confidence = 0;
		// TODO Auto-generated method stub
		 String activity = intent.getStringExtra("ACTIVITY") ;
		 long time = intent.getLongExtra("TIME",0);
		 double latitude = intent.getDoubleExtra("LATITUDE", 0);
		 double longitude = intent.getDoubleExtra("LONGITUDE", 0);
		 double altitude = intent.getDoubleExtra("ALTITUDE", 0);
		 int step = (int) intent.getDoubleExtra("STEP", 0);
		 
		 try {
			 //if(confidence > 80)
			LifeLogUtility.writeActivityInFile(time,activity,latitude,longitude,altitude,step,LifeLogUtility.createFileName(Calendar.getInstance()));
			if(!activity.equals("Still")&&!activity.equals("Tilting")&&!activity.equals("Unknown"))
			LifeLogUtility.writeActivityInCloud(this,time,activity,latitude,longitude,altitude,step);
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Exception in writing in file","TRUE");
			e.printStackTrace();
		}
		 finally{
			
		 stopService(intent);
		 }		
	}
	 
//		public void writeInFile (String activity,int confidence,long time,String filename) throws IOException
//			{		
//				//Log.d("PATH",Environment.DIRECTORY_RINGTONES+"/lifetracker.txt");		
//				//Log.d("Path",file.getAbsolutePath());
//		//		Log.d("longitude", ""+longitude);
//		//		Log.d("latitude", ""+latitude);
//		//		Log.d("altitude", ""+altitude);
//				BufferedWriter writer=null;
//				//String path="sdcard/LifeTracker/lifetracker.csv";
//				File dir=new File(Environment.getExternalStorageDirectory()+"/LifeTracker2");
//				Log.d("Directory PATH", dir.getAbsolutePath());
//				boolean flag=dir.mkdir();
//				Log.d("Directory created?",""+flag);
//				File file=new File(dir.getAbsolutePath(),filename);
//							if(file.exists()==false)
//							file.createNewFile();			
//							writer=new BufferedWriter(new FileWriter(file,true));
//							writer.write(new Date(time).toString()+","+activity+","+confidence);
//							writer.newLine();
//							Log.d("Appended","True");
//							writer.flush();
//							writer.close();
//			}
		
	
			
}
