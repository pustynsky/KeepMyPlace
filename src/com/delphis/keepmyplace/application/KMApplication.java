package com.delphis.keepmyplace.application;

import com.delphis.keepmyplace.utilites.KMToast;

import android.app.Application;

public class KMApplication extends Application {

	private static KMToast _Ktoast;
	private static Object _object;
	
	public static KMToast getToast()
	{
		return _Ktoast;
	}
	
	public static void storeObject(Object object)
	{
		_object=object;
	}
	
	public static Object obtainObject()
	{
		return _object;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();		

		KMToast.initialize(getApplicationContext());
		this._Ktoast=KMToast.getInstance();
	}
	
	

}
