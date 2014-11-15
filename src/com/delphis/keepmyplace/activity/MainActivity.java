package com.delphis.keepmyplace.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import receivers.NetworkChangeReceiver;

import com.delphis.keepmyplace.R;
import com.delphis.keepmyplace.application.KMApplication;
import com.delphis.keepmyplace.application.Settings;
import com.delphis.keepmyplace.db.DatabaseManager;
import com.delphis.keepmyplace.db.SaveLocationAsync;
import com.delphis.keepmyplace.db.SettingsHelper;
import com.delphis.keepmyplace.entity.KMLocation;
import com.delphis.keepmyplace.utilites.Utilites;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements 
	LocationListener, 
	OnMapClickListener, 
	OnInfoWindowClickListener,
	OnMarkerClickListener,
	IDialogFragmentClickListener,
	IConnectionCheckerListener,
	ILocationSyncEvents,
	OnMyLocationButtonClickListener,
	IDialogFragmentDeleteClickListener
	
	{
	
	private GoogleMap googleMap;
	private Location location;
	private DialogFragmentAddLocation dialogAddLocation;
	private DialogFragmentDeleteLocation dialogDeleteLocation;
	private boolean boolIsFirstRun=true;
	private float fMapZoom;
	private Marker myClickedPlace;
	private LocationManager service;	
	private List<KMLocation> mySavedLocations;
	private DatabaseManager dbm;
	private ListView drawerListView;
	private ArrayAdapter<KMLocation> adapter;
	private LatLng selectedPoint;
	private boolean DEVELOPER_MODE=false;
	private List<Marker> mapMarkers;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private String applicationName;
	private Menu _menu;
	private int ShareOptions=SettingsHelper.ADD_NOTHING_ON_SHARE;
	private SettingsHelper settings;
	
	private NetworkChangeReceiver networkChangeReceiver=new NetworkChangeReceiver();
	
	@Override
	protected void onStop() {		
		super.onStop();
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		
		if(service!=null)
			service.removeUpdates(this);
		if(networkChangeReceiver!=null)
			this.unregisterReceiver(networkChangeReceiver);	
		
	}

	private DialogFragmentAddLocation getAddLocationDialogInstance()
	{
		if(this.dialogAddLocation==null)
			dialogAddLocation= new DialogFragmentAddLocation();
		
		return dialogAddLocation;
			
	}
	
	private DialogFragmentDeleteLocation getDeleteLocationDialogInstance()
	{
		if(this.dialogDeleteLocation==null)
			dialogDeleteLocation= new DialogFragmentDeleteLocation();
		
		return dialogDeleteLocation;
			
	}
	
	
	
//	@Override
//    public Object onRetainCustomNonConfigurationInstance() {
//        return text;
//    }
	private void CheckConnection()
	{
		ConnectionChecker conn=null;        
        try {
			 conn=new ConnectionChecker(this);
			 conn.check();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   
	}
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (DEVELOPER_MODE) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }

        
        super.onCreate(savedInstanceState);
        
               
        this.getActionBar().setDisplayHomeAsUpEnabled(true);        
        setContentView(R.layout.activity_main);
        
       
        
        this.CheckConnection();
        
        this.initSettings();
        
       
                        
     //   List<Address> lst=Utilites.getPointsByLocation(this, "");
        
        
        applicationName=getResources().getString(R.string.app_name);
        
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.stringDrawerTitle,  /* "open drawer" description */
                R.string.stringDrawerTitle  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
        	@Override
            public void onDrawerClosed(View view) {
        		   super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
        	@Override
            public void onDrawerOpened(View drawerView) {
        		   super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
     
        
//        Log.d("MainActivity","My hashcode is "+this.hashCode());
//        
//        if(savedInstanceState!=null)
//        	text=(String)getLastCustomNonConfigurationInstance();
//        else
//        	text="test";
//        
//        Log.d("MainActivity","Text hashcode is "+text.hashCode()+ "and text contains: "+ text);
        	
        
            
       if(savedInstanceState!=null)
        {
        	dialogAddLocation=(DialogFragmentAddLocation)getSupportFragmentManager().getFragment(savedInstanceState, "dialogAddLocation");        	
        	dialogDeleteLocation=(DialogFragmentDeleteLocation)getSupportFragmentManager().getFragment(savedInstanceState, "dialogDeleteLocation");
        }
       
        dialogAddLocation=getAddLocationDialogInstance();
        dialogDeleteLocation=getDeleteLocationDialogInstance();
       
        SupportMapFragment map= (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap=map.getMap();  
        
        if(googleMap!=null)
        {
	        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	       
	        googleMap.setBuildingsEnabled(true);	 
	        googleMap.setTrafficEnabled(true);
	        googleMap.setMyLocationEnabled(true);	        
	        googleMap.setOnMapClickListener(this);
	        googleMap.setOnMarkerClickListener(this);
	     	googleMap.setOnInfoWindowClickListener(this);
	     	googleMap.setOnMyLocationButtonClickListener(this);
	     		     	
	        
	        fMapZoom=18;
	        
	        dbm=new DatabaseManager(getApplicationContext());
	        
	        this.drawMarkers();
	        	
	        	adapter=new ArrayAdapter<KMLocation>(this, android.R.layout.simple_list_item_1, mySavedLocations);
	        	
	        	drawerListView=(ListView)findViewById(R.id.left_drawer);
	        	drawerListView.setAdapter(adapter);
	        //	drawerListView.
	        	
	        	drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long id) {
						
						googleMap.moveCamera(CameraUpdateFactory.newLatLng(mySavedLocations.get(position).get_coordinates()));
			        	googleMap.animateCamera(CameraUpdateFactory.zoomTo(fMapZoom));
	        				        	
			        	drawNewMarker(mySavedLocations.get(position).get_coordinates(), mySavedLocations.get(position).get_name(), mySavedLocations.get(position).get_notes());	
					}        		
				});
	        	
	        	drawerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {			
						//Location loc=dbm.getLocationById(locationId);
						getDeleteLocationDialogInstance().dialogInit(mySavedLocations.get(arg2));			
						getDeleteLocationDialogInstance().show(getSupportFragmentManager(), "dialogDeleteLocation");									
						
						return true;
					}
				});
	        	
			
	        
	    
	   
	    	        
	        service = (LocationManager) getSystemService(LOCATION_SERVICE);
	       
	        Criteria criteria = new Criteria();
	        final String provider = service.getBestProvider(criteria, false);
	        location = service.getLastKnownLocation(provider);
	        
	        googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
				
				@Override
				public void onMapLoaded() {
					 location = service.getLastKnownLocation(provider);					 
					 onLocationChanged(location);
				}
			});
	                
//	        if(location!=null){
//	              onLocationChanged(location);
//	        }        
//        
	        service.requestLocationUpdates(provider, 5000, 0, this);	
        }
        else // no google maps
        {
        	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	    if(resultCode != ConnectionResult.SUCCESS)
    	    {	    	    
	    	      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 69);
	   	          dialog.setCancelable(false);	   	     
	   	          dialog.show();
    	    }
    	    else // play services is installed
    	    {
	        	Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("Please install Google Maps");
	        	builder.setCancelable(false);
	        	builder.setPositiveButton("Install", getGoogleMapsListener());
	        	AlertDialog dialog = builder.create();
	            dialog.show();
    	    }
        	//Toast.makeText(this, "Google maps is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }
    
    public OnClickListener getGoogleMapsListener()
    {
        return new OnClickListener() 
        {
            @Override
            public void onClick(DialogInterface dialog, int which) 
            {
            	try
            	{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
            	}
            	catch(Exception ex)
            	{
            		Toast.makeText(getApplicationContext(), "We encountered an error while trying to launch Google Play Application", Toast.LENGTH_LONG).show();
            	}

                //Finish the activity so they can't circumvent the check
                finish();
            }
        };
    }

    private void drawMarkers() {
    	
    	googleMap.clear();
    	try {
        	// get saved locations
        	mySavedLocations=dbm.getLocations();
        	
        	if(mySavedLocations==null)
        		mySavedLocations=new ArrayList<KMLocation>();
        	
        	adapter.clear();
        	adapter.addAll(mySavedLocations);
        	mapMarkers=new ArrayList<Marker>();
        	
        	
        	for (KMLocation location : mySavedLocations) {
        		        		
        		addMapMarker(location);	
			}
    	}
        	catch(Exception e) {
        		Log.d("dddd","Something is wrong: " +e.getMessage());
        		
        	}
	}

	private void initSettings() {
    	 settings=new SettingsHelper(this);	
    	 ShareOptions=settings.getSettings().getImageShareMode();
	}

	private void addMapMarker(KMLocation location)
    {
    	MarkerOptions mark=new MarkerOptions()
        .title(location.get_name())        
        .snippet(location.get_notes()).icon(BitmapDescriptorFactory.fromResource((R.drawable.footprint)))
        .position(location.get_coordinates());
		        		
		Marker mrk=googleMap.addMarker(mark);
		mapMarkers.add(mrk);
	//	mapMarkers.get(mapMarkers.size()-1).setTitle("suka");
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   
    	_menu=menu;    	         
        getMenuInflater().inflate(R.menu.main, menu);
        _menu.findItem(R.id.action_add_location).setVisible(false);
        
        if(settings.getSettings().getImageShareMode() == SettingsHelper.ADD_PHOTO_ON_SHARE)
         	_menu.findItem(R.id.action_send_image).setChecked(true); 
        else if(settings.getSettings().getImageShareMode() == SettingsHelper.ADD_SNAPSHOT_ON_SHARE)
        	_menu.findItem(R.id.action_send_shapshot_on_share).setChecked(true);
        else if(settings.getSettings().getImageShareMode() == SettingsHelper.ADD_NOTHING_ON_SHARE)
        	_menu.findItem(R.id.action_send_nothing).setChecked(true);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       
        int id = item.getItemId();
        
        if (id == R.id.action_send_image) {        	
        	settings.setSettings(new Settings(SettingsHelper.ADD_PHOTO_ON_SHARE));    
        	item.setChecked(true);
            return true;
        }
        else if(id== R.id.action_send_shapshot_on_share)
        {
        	settings.setSettings(new Settings(SettingsHelper.ADD_SNAPSHOT_ON_SHARE));
        	item.setChecked(true);
            return true;
        }
        else if(id== R.id.action_send_nothing)
        {
        	settings.setSettings(new Settings(SettingsHelper.ADD_NOTHING_ON_SHARE));
        	item.setChecked(true);
            return true;
        }
        
        
        if (id == R.id.action_settings_about) {
        	KMApplication.getToast().show(getString(R.string.string_copyright));        	
            return true;
        }
        else  if(id == R.id.action_add_location) { 
        	if(myClickedPlace!=null)
        		onInfoWindowClick(myClickedPlace);
        	else        		
        		KMApplication.getToast().show(getString(R.string.stringActionBarMenuAddNewLocation));   
          
        	return true;
        }
        else  if(id == R.id.action_share_location) { 
        	
        	if(myClickedPlace!=null) // any marker is selected
        	{           		
        		 this.Share(myClickedPlace.getPosition());	        	
	        }
        	else if(googleMap.isMyLocationEnabled() && googleMap.getMyLocation()!=null)
        	{
        		this.Share(Utilites.convertLocationToLatLng(googleMap.getMyLocation()));
        	}
        	else
        		Toast.makeText(MainActivity.this, "Nothing is selected. Click on map or saved place to select it for sharing", Toast.LENGTH_SHORT).show();
        	 
        	return true;
        	
        		   
        	
        }
        else if(id == android.R.id.home) {
        	
        	if(mDrawerLayout.isDrawerVisible(GravityCompat.START))
        		mDrawerLayout.closeDrawer(Gravity.LEFT);
        	else
        		mDrawerLayout.openDrawer(Gravity.LEFT);
        	
        	return true;
        }
        	
        return super.onOptionsItemSelected(item);
    }
    
    private void Share(LatLng latlng)
    {
    	if(settings.getSettings().getImageShareMode()==SettingsHelper.ADD_PHOTO_ON_SHARE)
    	{
    		this.addImageBeforeShare();        		
    	}
    	else if(settings.getSettings().getImageShareMode()==SettingsHelper.ADD_SNAPSHOT_ON_SHARE)
    	{
    		this.TakeMapSnapshot(latlng);
    	}
    	else
    	{
    		this.ShareLocation(latlng);
    	}
    }
    
    private void ShareLocation(LatLng coordinates) {

    		String emailText;
			try {
				emailText = "Please click this link to see the place I want to share: " + Utilites.getLocationLink(coordinates);
			
		        	
		KMLocation loc;
		try {
			//loc = dbm.getLocationByLatLng(myClickedPlace.getPosition());
			loc=dbm.getLocationByLatLng(coordinates);
			
			if(loc!=null && loc.get_address()!=null && !loc.get_address().trim().equals(""))
				emailText+="\nAddress: "+loc.get_address();			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
		
		Intent i = new Intent(Intent.ACTION_SEND);
		
		if(settings.getSettings().getImageShareMode()==SettingsHelper.ADD_PHOTO_ON_SHARE)
		{
			final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/picFolder/image.jpg"; 
		    File newdir = new File(dir); 
		    
			i.setType("image/*");
			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newdir));
		}
		else if(settings.getSettings().getImageShareMode()==SettingsHelper.ADD_SNAPSHOT_ON_SHARE)
		{
			final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/picFolder/snapshot.jpg"; 
		    File newdir = new File(dir); 

		    i.setType("image/*");
			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newdir));
		}
		else
			i.setType("text/plain");
		
		i.putExtra(Intent.EXTRA_SUBJECT, "Here's my place and its coordinates");
		i.putExtra("sms_body", "Here's my place and its coordinates"+"\n"+emailText); 
		i.putExtra(Intent.EXTRA_TEXT, emailText);
	
		try {
		    startActivity(Intent.createChooser(i, "Share selected location:"));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainActivity.this, "There are no share clients installed", Toast.LENGTH_SHORT).show();
		}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	}

    private File createTempImageFile(String name)
    {
    	//here,we are making a folder named picFolder to store pics taken by the camera using this application
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/picFolder/"; 
        File newdir = new File(dir); 
        newdir.mkdirs();
        
		String file = dir+name;
        File newfile = new File(file);
        try {
			Log.d("dddd", "fiename: "+newfile.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        	Log.d("dddd", e.getMessage());
        }       
        
        return newfile;
    }
    
	private void addImageBeforeShare() {
		
		File newfile=createTempImageFile("image.jpg");
		
		if(newfile!=null)
		{
	        Uri outputFileUri = Uri.fromFile(newfile);
	
	        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
	        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	    
	        startActivityForResult(cameraIntent, 555);
		}
		else
			Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
		
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		if (requestCode == 555 && resultCode == RESULT_OK) {
			
			if(myClickedPlace!=null)
				ShareLocation(myClickedPlace.getPosition());
			else if(googleMap.isMyLocationEnabled() && googleMap.getMyLocation()!=null)
			{
				this.ShareLocation(Utilites.convertLocationToLatLng(googleMap.getMyLocation()));
			}
			
	        Log.d("CameraDemo", "Pic saved");


	    }
    }

	@Override
	public void onLocationChanged(Location currentLocation) {

		if(currentLocation!=null)
		{
        // Getting latitude of the current location
        double latitude = currentLocation.getLatitude();

        // Getting longitude of the current location
        double longitude = currentLocation.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        if(boolIsFirstRun)
        {// Showing the current location in Google Map
        	googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));        
        	googleMap.animateCamera(CameraUpdateFactory.zoomTo(fMapZoom));        	
//        	googleMap.addMarker(new MarkerOptions()
//             .title("Hobbit's hole")
//             .snippet("Home sweet home")
//             .position(latLng));        	
        	        	 
        	boolIsFirstRun=false;
        }
        else
        	fMapZoom=googleMap.getCameraPosition().zoom;
        	
		}
       // KMToast.show(String.valueOf(latitude)+" : "+ String.valueOf(longitude));    
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	private void drawNewMarker(LatLng coords, String name, String description)
	{
		if(coords!=null)
		{
			if(myClickedPlace!=null && myClickedPlace.getTitle().equals(getString(R.string.stringNewLocationName)) && myClickedPlace.getSnippet().equals(getString(R.string.stringNewLocationDescription)))
			{
				myClickedPlace.remove();				
			}
			
			MarkerOptions marker=new MarkerOptions()
	        .title(name)
	        .snippet(description)
	        .icon(BitmapDescriptorFactory.fromResource((R.drawable.footprint)))
	        .position(coords);
			
			myClickedPlace=googleMap.addMarker(marker);
			myClickedPlace.showInfoWindow();
			
			//KMApplication.getToast().show(coords.latitude+ " : " + coords.longitude);
		}

	}
	
		
	@Override
	public void onMapClick(LatLng coords) 
	{		
		_menu.findItem(R.id.action_add_location).setVisible(true);
		drawNewMarker(coords, getString(R.string.stringNewLocationName), getString(R.string.stringNewLocationDescription));
	}

	@Override
	public void onInfoWindowClick(Marker marker) {	
		
		if(!(marker.getTitle().equals(getString(R.string.stringNewLocationName)) && marker.getSnippet().equals(getString(R.string.stringNewLocationDescription))))
		{ 
			KMLocation location;
			try {
				location = dbm.getLocationByLatLng(marker.getPosition());
				String address=location.get_address().trim();
				
				if(!address.equals(""))
					Toast.makeText(this, location.get_address(), Toast.LENGTH_SHORT).show();
				
				

				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}			
		else
		{
			// addes new location		
			selectedPoint=marker.getPosition();									
			
			getAddLocationDialogInstance().dialogInit(selectedPoint);			
			getAddLocationDialogInstance().show(getSupportFragmentManager(), "dialogAddLocation");
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		String TAG="dddd";
		
		if(savedInstanceState!=null)
		{
		Bundle viewHierarchy = savedInstanceState.getBundle("android:viewHierarchyState");
				      if (viewHierarchy != null) {
				          SparseArray views = viewHierarchy.getSparseParcelableArray("android:views");
				           if (views != null) {
				               for (int i = 0; i < views.size(); i++) {
				                Log.v(TAG, "key -->" + views.get(i));
				                   Log.v(TAG, "value --> " + views.valueAt(i));
				             }
				           }
				       } else {
				           Log.v(TAG, "no view data");
				       }	
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		if(getAddLocationDialogInstance() !=null && getAddLocationDialogInstance().isVisible())
		{
			  getSupportFragmentManager().putFragment(outState, "dialogAddLocation", dialogAddLocation);			  
		}
		
		if(getDeleteLocationDialogInstance() !=null && getDeleteLocationDialogInstance().isVisible())
		{
			  getSupportFragmentManager().putFragment(outState, "dialogDeleteLocation", dialogDeleteLocation);			  
		}
		
		super.onSaveInstanceState(outState);		
	}

	@Override
	public void onSaveLocationListener(final KMLocation location) {
		
		if(location.get_name().trim().equals(""))
			KMApplication.getToast().show("Please specify location name");
		else
		{
		String saveResult="Location added";
				
		try
		{
			SaveLocationAsync saver=new SaveLocationAsync(this);
			long iLocationId=saver.SaveLocation(this, location);
		
			drawNewMarker(location.get_coordinates(), location.get_name(), location.get_notes());
		
			location.set_id(iLocationId);
			mySavedLocations.add(location);
			addMapMarker(location);
			adapter.notifyDataSetChanged();		
			_menu.findItem(R.id.action_add_location).setVisible(false);
			
		} catch (Exception e) {
			saveResult="Failed";
			Log.e("MainActivity", e.getMessage());
		}
		finally
		{

			KMApplication.getToast().show(saveResult);
			getAddLocationDialogInstance().dismiss();
			dialogAddLocation=null;
		}		
		}
		
	}

	@Override
	public void onCancelListener() {
		getAddLocationDialogInstance().dismiss();
	}

	

	@Override
	public void ConnectionStateResponse(int what) {
		String message="☞     ";
				 
		if(what==ConnectionChecker.CONNECTION_FAULT)
			message+=getString(R.string.stringConnectionFault);
		else
			message+=getString(R.string.stringConnectionSuccess);
		
		if(service!=null)
			if(!service.isProviderEnabled(LocationManager.GPS_PROVIDER) && !service.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				message+="\n\n☞     "+getString(R.string.string_location_is_not_enabled);
		 		
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();		
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		_menu.findItem(R.id.action_add_location).setVisible(false);
		myClickedPlace=marker;
		return false;
	}

	@Override
	public void LocationSynchronized(KMLocation location) {
		//drawNewMarker(location.get_coordinates(), location.get_name(), location.get_notes()+"\n"+location.get_address());
	}

	@Override
	public boolean onMyLocationButtonClick() {
		if(!service.isProviderEnabled(LocationManager.GPS_PROVIDER) && !service.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		 Toast.makeText(this, "☞     "+getString(R.string.string_location_is_not_enabled), Toast.LENGTH_SHORT).show();
	 	
		return false;
	}

	@Override
	protected void onResume() {	
		super.onResume();
		 this.registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

		
	}
	
	

	@Override
	public void onDeleteLocationListener(KMLocation location) {
		
		int result=dbm.DeleteLocation(location.get_id());
		
		if(result==1)
			Toast.makeText(this, "Location was deleted", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Error due a deletion", Toast.LENGTH_SHORT).show();
		
//		if(myClickedPlace!=null && myClickedPlace.getTitle().equals(getString(R.string.stringNewLocationName)) && myClickedPlace.getSnippet().equals(getString(R.string.stringNewLocationDescription)))
//		{
//			myClickedPlace.remove();				
//		}
			
		getDeleteLocationDialogInstance().dismiss();
		
		mySavedLocations.remove(location);
		adapter.remove(location);
		this.drawMarkers();
			
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDeleteCancelListener() {
		getDeleteLocationDialogInstance().dismiss();		
	}
	
	private void TakeMapSnapshot(final LatLng latlng)
	{
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                FileOutputStream out = null;
                try {
                	//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/picFolder/"; 
                       out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/picFolder/snapshot.jpg");
                       bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                       ShareLocation(latlng);
              
                } catch (Exception e) {
                       e.printStackTrace();
                }
                finally
                {
                	try {
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                
                }
            }
        };

        googleMap.snapshot(callback);		

	}
	}

