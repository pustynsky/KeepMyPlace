package com.delphis.keepmyplace.db;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.delphis.keepmyplace.activity.ILocationSyncEvents;
import com.delphis.keepmyplace.entity.KMLocation;
import com.delphis.keepmyplace.utilites.Utilites;


public class SaveLocationAsync {
	
	private ILocationSyncEvents _parent;
	private KMLocation _location;
	private Handler hndl;
	
	public SaveLocationAsync(ILocationSyncEvents parent)
	{
		_parent=parent;
	}
		
	public long SaveLocation(final Context _context, final KMLocation location) throws Exception
	{
		synchronized (location) {
		
		long locationId=0;
		final DatabaseManager db=new DatabaseManager(_context);		
		locationId=db.insertLocation(location);
		
		class CustomRunnable implements Runnable
		{

			private long _locationId=0;
			
			public void setLocationId(long locId)
			{
				_locationId=locId;
			}
			
			@Override
			public void run() {
				KMLocation loc;
				try {
					loc=(KMLocation) location.clone();					
					loc.set_address(Utilites.getAddress(_context, loc.get_coordinates()));	
					loc.set_id(_locationId);
					db.updateLocation(loc);
					_location=loc;
					hndl.sendEmptyMessage(0);
					
					
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
				
		hndl= new Handler(){
			@Override
			public void handleMessage(Message msg) {
				_parent.LocationSynchronized(_location);
			}
		};
		
		
		CustomRunnable custom=new CustomRunnable();
		custom.setLocationId(locationId);
		Thread thread=new Thread(custom);
				
		
			
		
		thread.start();
		//thread.setPriority(Thread.MAX_PRIORITY);
		
		return locationId;
		}
	}
}
