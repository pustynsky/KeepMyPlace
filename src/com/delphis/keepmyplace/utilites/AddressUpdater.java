package com.delphis.keepmyplace.utilites;

import java.util.List;
import android.content.Context;
import com.delphis.keepmyplace.db.DatabaseManager;
import com.delphis.keepmyplace.entity.*;

public class AddressUpdater {

	// runs over all locations that have empty address field and tries to update them
		// with corresponding addresses
		
	private List<KMLocation> list;
	private DatabaseManager dbm;
	private Context _context;
		
	public AddressUpdater(Context ctx)
	{
		_context=ctx;	
	}
		
	public int run()
	{
		int iCommon=0;
		int iUpdated=0;
		
		dbm=new DatabaseManager(_context);
		try {
			list=dbm.getLocations();
			
			if(list==null) return 0;
			
			iCommon=list.size();
			for(KMLocation loc : list)
			{
				if(loc.get_address()!=null && !loc.get_address().trim().equals(""))
					continue;
				loc.set_address(Utilites.getAddress(_context, loc.get_coordinates()));
				iUpdated+= dbm.updateLocation(loc);
						
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return iUpdated;
	}
	}
	


