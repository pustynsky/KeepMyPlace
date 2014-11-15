package com.delphis.keepmyplace.activity;

import java.io.PipedReader;
import java.util.concurrent.locks.ReentrantLock;

import com.delphis.keepmyplace.utilites.Utilites;
import com.delphis.keepmyplace.utilites.UtilitesConnection;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class ConnectionChecker {

	public static int CONNECTION_SUCCESS=1;
	public static int CONNECTION_FAULT=0;
	
	
	private IConnectionCheckerListener onConnectionStateResult;
	private Context mContext;
	private Thread connectionCheckThread;
	private static Handler connectionCheckHandler;
	
	public ConnectionChecker(Context context) throws Exception
	{
		if(context==null)
			throw new NullPointerException(ConnectionChecker.class.getSimpleName() + " you should pass not null context into constructor");
		
		if(!(context instanceof IConnectionCheckerListener))
			throw new Exception(ConnectionChecker.class.getSimpleName() + " the context you've passed must implement IConnectionCheckerListener interface");
		
		mContext=context;
		onConnectionStateResult=(IConnectionCheckerListener)context;
	}
	
	public void check()
	{
		if(connectionCheckThread==null)
			connectionCheckThread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					boolean result=UtilitesConnection.hasInternetAccess(mContext);
					int message;
					message = result==true? CONNECTION_SUCCESS: CONNECTION_FAULT;
										
					connectionCheckHandler.sendEmptyMessage(message);
				}
			});
		
		
		if(connectionCheckHandler==null)
			connectionCheckHandler=new CheckHandler(onConnectionStateResult);
		
		connectionCheckThread.start();
		
	}
	
	static class CheckHandler extends Handler {
		
		private IConnectionCheckerListener listener;
		
		public CheckHandler(IConnectionCheckerListener activity)
		{
			listener=activity;
		}
		
		 @Override
		    public void handleMessage(Message msg) {
			 listener.ConnectionStateResponse(msg.what);
		    }
	}
	
}
