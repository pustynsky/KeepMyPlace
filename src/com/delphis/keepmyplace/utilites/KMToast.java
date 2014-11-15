package com.delphis.keepmyplace.utilites;

import android.content.Context;
import android.widget.Toast;

public class KMToast {
	
	private static Context _context;
	private static KMToast _toast;
	
	private KMToast() { } 
	
	public static void initialize(Context context) {
			_context=context;
	}
	
	public static KMToast getInstance()
	{		
		if(_toast==null)
		{
			_toast=new KMToast();
		}
		
		return _toast;
	}
			
	public void show(String message) {
		
		if(_context==null) {
			throw new IllegalStateException(KMToast.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
		}
		
		Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
	}
}
