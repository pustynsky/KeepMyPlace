package com.delphis.keepmyplace.db;

import com.delphis.keepmyplace.R;
import com.delphis.keepmyplace.application.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {

	private Activity _act;
	public static int ADD_PHOTO_ON_SHARE=0;
	public static int ADD_SNAPSHOT_ON_SHARE=1;
	public static int ADD_NOTHING_ON_SHARE=2;
	
	public SettingsHelper(Activity act)
	{
		_act=act;
	}
	public Settings getSettings()
	{
		Settings settings=new Settings();
			
		SharedPreferences sharedPref = _act.getPreferences(Context.MODE_PRIVATE);
										
		settings.setImageShareMode(sharedPref.getInt(_act.getString(R.string.SendImageOnShareMode), SettingsHelper.ADD_NOTHING_ON_SHARE));
		
		return settings;		
	}
	
	public void setSettings(Settings settings)
	{		
		
		if(settings==null)
			throw new NullPointerException();
		
				
		SharedPreferences sharedPref = _act.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(_act.getString(R.string.SendImageOnShareMode), settings.getImageShareMode());
		editor.commit();		
	
	}
	
}
