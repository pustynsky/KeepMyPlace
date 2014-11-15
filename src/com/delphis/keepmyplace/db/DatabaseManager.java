package com.delphis.keepmyplace.db;

import java.util.ArrayList;
import java.util.List;
import com.delphis.keepmyplace.entity.KMLocation;
import com.google.android.gms.maps.model.LatLng;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {	
	
	protected static final String DATABASE_NAME = "kml.db";
	protected static final int DATABASE_VERSION = 1;
	protected static final String CREATE_TABLE_EXPRESSION="CREATE TABLE IF NOT EXISTS ";
	protected static final String DROP_TABLE_EXPRESSION="DROP TABLE IF EXISTS ";
	protected static final String LOCATIONS_TABLE_NAME="Locations";
	 // **************** TABLE CREATION SQL EXPRESSIONS ***********************
	protected static final String TableLocationsCreateSQL= CREATE_TABLE_EXPRESSION + " " + LOCATIONS_TABLE_NAME +" (_id integer primary key autoincrement, Name TEXT, Notes TEXT, Latitude DOUBLE, Longitude DOUBLE, Address TEXT, AdditionDate DATETIME)";
	 // ***********************************************************************
	
	private Context context;
	private SQLiteDatabase db;
	private OpenHelper openHelper = null;	 
	 
	 public DatabaseManager(Context oContext)
	 {
		 if (db == null || !db.isOpen()){
			 getDB(oContext);
		 }	
	 }
	 
	 private void getDB(Context oContext)
	 { 		
		 if (db != null && db.isOpen()) db.close();
		 if (openHelper != null) openHelper.close();
		 this.context=oContext;		 
		 openHelper = new OpenHelper(this.context);		 
		 db = openHelper.getWritableDatabase();		
	 }
	 
	 public void close() {		    
		    if (db != null) {
		    	db.close();
		    }
		}
	 
	 
	 // **************************** INSERT SQL EXPRESSIONS *******************
	 // insert location and return _id
	 public long insertLocation(KMLocation location) throws Exception
	 {	
		 if(!db.isOpen()) getDB(context);
		 
		 long lResult=-1;
		 ContentValues initialValues = new ContentValues();
		 
			 
		 initialValues.put("Name", location.get_name());
		 initialValues.put("Latitude",location.get_coordinates().latitude);
		 initialValues.put("Longitude",location.get_coordinates().longitude);
		 initialValues.put("Notes",location.get_notes());
		 initialValues.put("Address",location.get_address());
		 
		 try
		 {
			 lResult= db.insertOrThrow("Locations", null, initialValues);

		 }
		 catch(Exception e)
		 {
			 throw new Exception(e);
		 }
		 finally
		 {
			 this.close();
		 }
		 return lResult;		 
	 }
	  
	 
	 public int updateLocation(KMLocation location) throws Exception
	 {
		 if(!db.isOpen()) getDB(context);
		 
		 int lResult=-1;
		 ContentValues initialValues = new ContentValues();
		 
		 initialValues.put("Name", location.get_name());
		 initialValues.put("Latitude",location.get_coordinates().latitude);
		 initialValues.put("Longitude",location.get_coordinates().longitude);
		 initialValues.put("Notes",location.get_notes());
		 initialValues.put("Address",location.get_address());
		 
		 
		 try
		 {
			 String[] sWhereVariable=new String[]{Long.toString(location.get_id())};				 
			 lResult= db.update("Locations", initialValues, "_id=?", sWhereVariable);			 			 
		 }
		 catch(Exception e)
		 {
			 throw new Exception(e);
		 }
		 finally
		 {
			 this.close();
		 }
		 
		 return lResult;		 
	 }
	 
	 public int DeleteLocation(long locationId)
	 {		
		 if(!db.isOpen()) getDB(context);
		 
		 int lResult=-1;
		 ContentValues initialValues = new ContentValues();
		 
		// initialValues.put("IsDeleted", 1);
		 
		 try
		 {
			 String[] sWhereVariable=new String[]{Long.toString(locationId)};				 
			 lResult= db.delete("Locations", "_id=?", sWhereVariable);	
			 //db.del
		 }
		 catch(Exception e)
		 {
			 //LogManager.WriteExceptionLog("Stars: DBAdapter - DeleteBook", e, context);
		 }
		 finally
		 {
			 this.close();
		 }
		 
		 return lResult;		 
	 }
	 
	 public KMLocation getLocationById(int locationId) throws Exception {
		 
		 KMLocation location = null;
		 
		 if(!db.isOpen()) getDB(context);
				 				 
		 Cursor cursor=null;
		 String sqlExpression="select * FROM Locations WHERE _id= "+ locationId;		
		 
		 try
		 {
		 cursor =  db.rawQuery(sqlExpression , null);
		 
		 if(cursor!=null)
		 {			 
			 if(cursor.moveToFirst())
			 {				
				 location=new KMLocation();
				 
				 LatLng coordinates=new LatLng(cursor.getDouble(cursor.getColumnIndex("Latitude")), cursor.getDouble(cursor.getColumnIndex("Longitude")));
								 
				 location.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
				 location.set_name(cursor.getString(cursor.getColumnIndex("Name")));
				 location.set_notes(cursor.getString(cursor.getColumnIndex("Notes")));
				 location.set_coordinates(coordinates);
				 location.set_address(cursor.getString(cursor.getColumnIndex("Address")));
				 
			 }	 
		 }
		 
		 }
		 catch(Exception e)
		 {
			 throw new Exception(e);			
		 }		 
		 finally
		 {
			 if(cursor!=null)
				  cursor.close();
			 
				 this.close();
		 }
		 
		 return location;
	 }
	 
public KMLocation getLocationByLatLng(LatLng coords) throws Exception {
		 
		 KMLocation location = null;
		 
		 if(!db.isOpen()) getDB(context);
				 				 
		 Cursor cursor=null;
		 String sqlExpression="select * FROM Locations WHERE Latitude= "+ coords.latitude + " AND Longitude="+coords.longitude;
		 
		 try
		 {
		 cursor =  db.rawQuery(sqlExpression , null);
		 
		 if(cursor!=null)
		 {			 
			 if(cursor.moveToFirst())
			 {				
				 location=new KMLocation();
				 
				 LatLng coordinates=new LatLng(cursor.getDouble(cursor.getColumnIndex("Latitude")), cursor.getDouble(cursor.getColumnIndex("Longitude")));
								 
				 location.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
				 location.set_name(cursor.getString(cursor.getColumnIndex("Name")));
				 location.set_notes(cursor.getString(cursor.getColumnIndex("Notes")));
				 location.set_coordinates(coordinates);
				 location.set_address(cursor.getString(cursor.getColumnIndex("Address")));
				 
			 }	 
		 }
		 
		 }
		 catch(Exception e)
		 {
			 throw new Exception(e);			
		 }		 
		 finally
		 {
			 if(cursor!=null)
				  cursor.close();
			 
				 this.close();
		 }
		 
		 return location;
	 }
	 
	 
 public List<KMLocation> getLocations() throws Exception {
		 
		 List<KMLocation> locations = null;
		 
		 if(!db.isOpen()) getDB(context);
				 				 
		 Cursor cursor=null;
		 String sqlExpression="select * FROM Locations";	
		 
		 try
		 {
			 
		 cursor =  db.rawQuery(sqlExpression , null);
		 
		 if(cursor!=null)
		 {			 
			 while(cursor.moveToNext())
			 {	
				 if(locations==null) locations=new ArrayList<KMLocation>();
				 KMLocation location =new KMLocation();
				 
				 LatLng coordinates=new LatLng(cursor.getDouble(cursor.getColumnIndex("Latitude")), cursor.getDouble(cursor.getColumnIndex("Longitude")));
								 
				 location.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
				 location.set_name(cursor.getString(cursor.getColumnIndex("Name")));
				 location.set_notes(cursor.getString(cursor.getColumnIndex("Notes")));
				 location.set_coordinates(coordinates);
				 location.set_address(cursor.getString(cursor.getColumnIndex("Address")));
				 
				 locations.add(location);
				 
			 }	 
		 }
		 
		 }
		 catch(Exception e)
		 {
			 throw new Exception(e);			
		 }		 
		 finally
		 {
			 if(cursor!=null)
				  cursor.close();
			 
				 this.close();
		 }
		 
		 return locations;
	 }
	 
	 
	 // ***************** DELETE TABLE SQL EXPRESSIONS ***********************
	 public void dropTable(String tableName)
	 {
		  if(!db.isOpen()) getDB(context);
		  
		 	db.execSQL(DROP_TABLE_EXPRESSION + " "+ LOCATIONS_TABLE_NAME);
		 	db.execSQL(DatabaseManager.TableLocationsCreateSQL);		 		 	
		 	this.close();
	 }	 
	 
	 class OpenHelper extends SQLiteOpenHelper {

	      OpenHelper(Context context) {
	         super(context, DatabaseManager.DATABASE_NAME, null, DatabaseManager.DATABASE_VERSION);
	      }

	      @Override
	      public void onCreate(SQLiteDatabase db) {	    	  
	    	  db.execSQL("DROP TABLE IF EXISTS Locations");	    	 
	    	  db.execSQL(DatabaseManager.TableLocationsCreateSQL);	    	
	      }

	      // this method will drop all DB tables
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         onCreate(db);
	      }
	 }
}



  

