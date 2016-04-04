package com.project.caretaker.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;
import com.parse.ParseObject;

public class LifeLogUtility {
	
	public static String getActivityName(int type) {
		switch (type)
		{
		case DetectedActivity.WALKING:
			return "Walking";
		case DetectedActivity.IN_VEHICLE:
			return "In Vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "On Bicycle";
		case DetectedActivity.ON_FOOT:
			return "On Foot";		
		case DetectedActivity.STILL:
			return "Still";
		case DetectedActivity.TILTING:
			return "Tilting";
		case DetectedActivity.RUNNING:
			return "Running";
		case DetectedActivity.UNKNOWN:
			return "Unknown";
		}
		return "N/A";
	}


	public static Date addFiveMinutes(Date date) {

	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.MINUTE, 5);
	    return cal.getTime();
	}
	
	 
	 public static void writeActivityInFile (long time,String activity,double latitude,double longitude,double altitude,double step,String filename) throws IOException
		{	
			
			BufferedWriter writer=null;			
			File dir=new File(Environment.getExternalStorageDirectory()+"/LifeLog");
			Log.d("Directory PATH", dir.getAbsolutePath());
			boolean flag=dir.mkdir();
			Log.d("Directory created?",""+flag);
			File file=new File(dir.getAbsolutePath(),filename);
						if(file.exists()==false)
						{
							file.createNewFile();	
							writer=new BufferedWriter(new FileWriter(file,true));
							writer.write("Time,Activity,Latitude,Longitude,Altitude,Step");
							writer.newLine();
							writer.write(new Date(time).toString()+","+activity+","+latitude+","+longitude+","+altitude+","+step);
							writer.newLine();							
						}
						else
						{						
						writer=new BufferedWriter(new FileWriter(file,true));						
						writer.write(new Date(time).toString()+","+activity+","+latitude+","+longitude+","+altitude+","+step);
						writer.newLine();
						Log.d("Appended","True");
						}
						writer.flush();
						writer.close();
						
		}
	 
	 
	 public static void writeActivityInCloud (Context context,long time,String activity,double latitude,double longitude,double altitude,double step) throws IOException
	 {
		    String email = LifeLogUtility.readPrefernce(context,"USER_SETTING","EMAIL", "");
			ParseObject interaction = new ParseObject("Activity");
			interaction.put("email",email);		
			interaction.put("longitude",longitude);
			interaction.put("latitude",latitude);
			interaction.put("altitude",altitude);
			interaction.put("activity",activity);
			interaction.put("step",step);
			interaction.put("date",new Date(time));
			interaction.saveEventually();
			Log.d("Successfully write in cloud", "TRUE");
	 }
	 
	 public static void writeLifeLogCountInFile (int walking,int running,int vehicle,int bicycle,String filename) throws IOException
		{		
			Date date = new Date();
			BufferedWriter writer=null;			
			File dir=new File(Environment.getExternalStorageDirectory()+"/LifeLog");
			Log.d("Directory PATH", dir.getAbsolutePath());
			boolean flag=dir.mkdir();
			Log.d("Directory created?",""+flag);
			File file=new File(dir.getAbsolutePath(),filename);
						if(file.exists()==false)
						{
							file.createNewFile();	
							writer=new BufferedWriter(new FileWriter(file,true));
							writer.write("Date,Walking,Running,Bicycle,Vehicle");
							writer.newLine();
							writer.write(date.toString()+","+walking+","+running+","+bicycle+","+vehicle);
							writer.newLine();							
						}
						else
						{						
						writer=new BufferedWriter(new FileWriter(file,true));						
						writer.write(date.toString()+","+walking+","+running+","+bicycle+","+vehicle);
						writer.newLine();
						Log.d("Appended","True");
						}
						writer.flush();
						writer.close();
						
		}
	 
	 
	 public static void writeLifeLogCountInCloud(long time,int walking,int running,int vehicle,int bicycle,Context context)
		{
					
		 	String email = LifeLogUtility.readPrefernce(context,"USER_SETTING","EMAIL", "");
			ParseObject interaction = new ParseObject("LifeLog");
			interaction.put("email",email);
			interaction.put("walking",walking);
			interaction.put("running",running);
			interaction.put("vehicle",vehicle);
			interaction.put("bicycle",bicycle);
			interaction.put("date",new Date(time));
			interaction.saveEventually();
			Log.d("Successfully write in cloud", "TRUE");	
			
		}
	 
	 
	 
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
				editor.putString(key,value);
				editor.commit();
		 }
	 
		public static String createFileName(Calendar cal)
		{
			DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
			String fileName = dateFormat.format(cal.getTime())+".csv";
			Log.d("FILE NAME",fileName);		
			return fileName; 
		}
		
		
		public static Date subtractDay(Date date) {
		
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    cal.add(Calendar.DAY_OF_MONTH, -1);
			    return cal.getTime();
			}
		
//		public static Date getStartTime()
//		{
//			Calendar c = Calendar.getInstance();
//			 return new Date(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),1,32,0);
//		}
	
		
}
