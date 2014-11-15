package com.delphis.keepmyplace.utilites;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.delphis.keepmyplace.entity.KMLocation;
import com.google.android.gms.maps.model.LatLng;

public class Utilites {
	public static double distFrom(KMLocation location1, KMLocation location2)
	{	
		if(location1==null || location2 == null)
			throw new NullPointerException(Utilites.class.getSimpleName() +
                    " one of locations object passed is null"); 
	
	    double lat1=location1.get_coordinates().latitude;
	    double lng1=location1.get_coordinates().longitude;
	    double lat2=location2.get_coordinates().latitude;
	    double lng2=location2.get_coordinates().longitude;
	    
		double earthRadius = 6371.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    return dist;
	    
	    }
	
	public static LatLng convertLocationToLatLng(Location location)
	{
		if(location==null)
			throw new NullPointerException("Location must be not null");
				
		LatLng lg=new LatLng(location.getLatitude(), location.getLongitude());
		return lg;
	}
	
	public static String getLocationLink(LatLng coordinates) throws UnsupportedEncodingException
	{
		if(coordinates==null)
			throw new NullPointerException("locaiton is null");
		
		return "http://maps.google.com/?q="+ URLEncoder.encode(coordinates.latitude+","+ coordinates.longitude, "UTF-8");		
	}
	
	public static synchronized String getAddress(Context ctx, LatLng coordinates) {
		
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
       
        try {
            List<Address> addresses = geocoder.getFromLocation(coordinates.latitude,
            		coordinates.longitude, 1);            
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                                    "\n");
                }
                int iN=strReturnedAddress.lastIndexOf("\n");
                strReturnedAddress.delete(iN, iN+2);
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address",
                        "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
	
	public static synchronized List<Address> getPointsByLocation(Context ctx, String coordinates) {
		
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        
       try {
		return geocoder.getFromLocationName("Herzel 65", 666);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       return null;
       
    }
	
}
