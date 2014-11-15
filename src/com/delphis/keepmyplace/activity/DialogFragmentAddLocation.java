package com.delphis.keepmyplace.activity;

import com.delphis.keepmyplace.R;
import com.delphis.keepmyplace.application.KMApplication;
import com.delphis.keepmyplace.entity.KMLocation;
import com.delphis.keepmyplace.utilites.Utilites;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DialogFragmentAddLocation extends DialogFragment 

{
		
	private Button btnSave;
	private Button btnCancel;	
	
	private DialogFragment fragment;	
	private IDialogFragmentClickListener iClickListener;
	private LatLng locationAdding;
	private Button.OnClickListener onSaveListener;
	private Button.OnClickListener onCancelListener;
	private Bundle bundle;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		iClickListener=(IDialogFragmentClickListener)activity;
	}
	
	public void dialogInit(final LatLng locationCoords)
	{		
		locationAdding=locationCoords;			
	}	
			
	@Override
	public void dismiss() {
		super.dismiss();				
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		this.setRetainInstance(true);
		if(savedInstanceState!=null)
		//	locationAdding=(LatLng)KMApplication.obtainObject();
			locationAdding=(LatLng)savedInstanceState.getParcelable("locationAdding");	
		
		onSaveListener=new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				View view=fragment.getView();
				
				KMLocation location=new KMLocation();
				location.set_name(((TextView)view.findViewById(R.id.editTextLocationName)).getText().toString());
				location.set_notes(((TextView)view.findViewById(R.id.editTextLocationDescription)).getText().toString());
				location.set_coordinates(locationAdding);				
				
				iClickListener.onSaveLocationListener(location);				
			}
		};
		
		onCancelListener=new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {
				iClickListener.onCancelListener();				
			}
		};
	
		
		if(iClickListener==null || onSaveListener == null)
			throw new NullPointerException(DialogFragment.class.getSimpleName() +
                    " method dialogInit(..) must be called before"); 
		
			fragment=this;
		
		    View dialoglayout=null;		  		    
		    dialoglayout=inflater.inflate(R.layout.add_location, null);
		    getDialog().setTitle(getString(R.string.stringSaveDialogTitle));
		    
		    btnSave= (Button)dialoglayout.findViewById(R.id.buttonSave);
		    btnCancel= (Button)dialoglayout.findViewById(R.id.buttonCancel);
		    
		    btnSave.setOnClickListener(onSaveListener);
		    btnCancel.setOnClickListener(onCancelListener);
		    		    	    
		    return dialoglayout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		bundle=savedInstanceState;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
	//	KMApplication.storeObject(locationAdding);
		
		bundle.putParcelable("locationAdding", locationAdding);		
	}

	
}
