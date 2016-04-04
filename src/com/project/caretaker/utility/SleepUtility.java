package com.project.caretaker.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseObject;
import com.project.caretaker.receiver.ScreenReceiver;

public class SleepUtility {

	
	static public String getEmail(Context context) {
	    AccountManager accountManager = AccountManager.get(context); 
	    Account account = getAccount(accountManager);

	    if (account == null) {
	      return null;
	    } else {
	      return account.name;
	    }
	  }

	  private static Account getAccount(AccountManager accountManager) {
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    Account account;
	    if (accounts.length > 0) {
	      account = accounts[0];      
	    } else {
	      account = null;
	    }
	    return account;
	  }
	
	public static String readPrefernce(Context context,String fileName,String key,String defaultValue)
	{
		SharedPreferences preferences=context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
		return preferences.getString(key,defaultValue);	
		
	}
	
	public static void storeToPreference(Context context,String filename, String key, String value)
	{
		SharedPreferences preferences=context.getSharedPreferences(filename,context.MODE_PRIVATE);
			SharedPreferences.Editor editor=preferences.edit();
			//Log.d("ACCESS TOKEN", token);
			//if(token!=null)
			editor.putString(key,value);
			editor.commit();
	 }
	
	public static String getTimeInStringFormat(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return dateFormat.format(date);
	}
	
	
	//return differnce between two times in seconds
	public static long getDuration(long d1,long d2)
	{
		long diff = d2- d1+1;
		long diffSeconds = diff / 1000;
		return diffSeconds;
	}
	
	public static String getDurationFormat(long sec)
	{
		long hour = sec / 3600 ;
		long minute = sec/60;
		long second = sec % 60 ;
		return  (hour < 10 ?"0"+String.valueOf(hour):String.valueOf(hour))+":"
				+(minute < 10 ?"0"+String.valueOf(minute):String.valueOf(minute))+":"
		         +(second < 10 ?"0"+String.valueOf(second):String.valueOf(second));
		
	}
	
	
	public static String createFileName(Calendar cal)
	{
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		String fileName = dateFormat.format(cal.getTime())+".csv";
		Log.d("FILE NAME",fileName);		
		return fileName; 
	}
	
	
	public static void writeInteractionInFile (Context context,String startTime,String endTime,long duration,String filename) throws IOException,Exception
	{		
		
		BufferedWriter writer=null;
		//String path="sdcard/LifeTracker/lifetracker.csv";
		File dir=new File("sdcard/SleepLog");
		boolean flag=dir.mkdir();
		//Log.d("Directory created?",""+flag);
		File file=new File(dir.getAbsolutePath(),filename);
					if(file.exists()==false)
					{
//						Intent service = new Intent(context,DataBaseService.class);
//						context.startService(service);
						file.createNewFile();			
						writer=new BufferedWriter(new FileWriter(file,true));
						writer.write("Start Time,End Time,Duration");
						writer.newLine();
						writer.write(startTime+","+endTime+","+duration);
					}
					else
					{
						writer=new BufferedWriter(new FileWriter(file,true));
						writer.newLine();						
						writer.write(startTime+","+endTime+","+duration);
					}
					Log.d("Appended","True");
					writer.flush();
					writer.close();
										
					
						
	}
	

	public static void writeVariableInFile (Context context,int v1,int v2,int v3,int v4,int counter,double sleep,double diff,String filename) throws IOException,Exception
	{		
		
		BufferedWriter writer=null;
		//String path="sdcard/LifeTracker/lifetracker.csv";
		File dir=new File("sdcard/SleepLog");
		boolean flag=dir.mkdir();
		//Log.d("Directory created?",""+flag);
		File file=new File(dir.getAbsolutePath(),filename);
					if(file.exists()==false)
					{
//						Intent service = new Intent(context,DataBaseService.class);
//						context.startService(service);
						file.createNewFile();			
						writer=new BufferedWriter(new FileWriter(file,true));
						writer.write("DATE,V1,V2,V3,V4,counter,Sleep,Difference");
						writer.newLine();
						writer.write(new Date()+","+v1+","+v2+","+v3+","+v4+","+counter+","+sleep+","+diff);
					}
					else
					{
						writer=new BufferedWriter(new FileWriter(file,true));
						writer.newLine();						
						writer.write(new Date()+","+v1+","+v2+","+v3+","+v4+","+counter+","+sleep+","+diff);
					}
					Log.d("Variable Appended","True");
					writer.flush();
					writer.close();				
						
	}
	
	
	public static void writeVariableInCloud(String email,int v1,int v2,int v3, int v4,int counter,double actual_sleep,double  difference)
	{
				
		ParseObject interaction = new ParseObject("SleepLog");
		interaction.put("email",email);
		interaction.put("V1",v1);
		interaction.put("V2",v2);
		interaction.put("V3",v3);
		interaction.put("V4",v4);
		interaction.put("counter",counter);
		interaction.put("difference",difference);
		interaction.put("actual_sleep",actual_sleep);		
		interaction.put("date",new Date());
		interaction.saveEventually();
		Log.d("Successfully write in cloud", "TRUE");	
		
	}
	
	
	
	
	
	public static void writeInteractionInCloud(String email,String startTime,String endTime,long duration)
	{		
		ParseObject interaction = new ParseObject("Interaction");
		interaction.put("email",email);
				
		try {
			Log.d("TEST START TIME", new Date(startTime).toString());
			interaction.put("starttime",  new Date(startTime));
			interaction.put("endtime",  new Date(endTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		interaction.put("duration",duration);
		interaction.saveEventually();
		Log.d("Successfully write in cloud", "TRUE");
		
		
  }
//	
//	
//	/**
//	 * isBetween
//	 * @param checkTime
//	 * @param time1
//	 * @param time2
//	 * @return true if checktime is between time1 and time2. Otherwise false
//	 */
////	public static boolean isBetween(String checkTime,String time1,String time2 )
//	{
//		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
//	
//		try {
//			Date time = sdf2.parse(checkTime);
//			Date startTime = sdf2.parse(time1);
//			Date endTime = sdf2.parse(time2);
//			Log.d("time",sdf2.format(time));
//			Log.d("startTime",sdf2.format(startTime));
//			Log.d("endTime",sdf2.format(endTime));
//			Log.d("Before", ""+time.before(startTime));
//			Log.d("After", ""+time.after(startTime));
//			
//			if(startTime.after(endTime))
//			{
//				Log.d("In 1 if", "true");
//				if (time.after(startTime) || time.before(endTime))
//					return true;
//				
//				else
//					return false;
//			}
//			else
//			{
//				Log.d("In 2 if", "true");
//				if (time.after(startTime) && time.before(endTime)) 
//				    return true;
//				 else  
//				    return false; 
//				
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//		
//		
//		
//	}
//	
	
	
//	public static Date subtractDay(Date date) {
//
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(date);
//	    cal.add(Calendar.DAY_OF_MONTH, -1);
//	    return cal.getTime();
//	}
	
	
	
	
	

	
	
	
}
