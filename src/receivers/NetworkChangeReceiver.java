package receivers;

import com.delphis.keepmyplace.utilites.AddressUpdater;
import com.delphis.keepmyplace.utilites.UtilitesConnection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

	// updates unupdated addresses of places
	
	@Override
	public void onReceive(final Context arg0, Intent arg1) {
		
		final AddressUpdater addrUpd=new AddressUpdater(arg0);
		
		Thread trd=new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(UtilitesConnection.hasInternetAccess(arg0))
					addrUpd.run();	
			 ((Activity)arg0).runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(arg0, "BRRUN!", Toast.LENGTH_SHORT).show();
				}
			});
			}
		});
		
		trd.start();
		
	}

}
