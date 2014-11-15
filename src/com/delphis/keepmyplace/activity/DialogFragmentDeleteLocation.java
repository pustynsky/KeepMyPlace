package com.delphis.keepmyplace.activity;

import com.delphis.keepmyplace.R;
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

public class DialogFragmentDeleteLocation extends DialogFragment 

{
		
	private Button btnDelete;
	private Button btnCancel;	
	
	private DialogFragment fragment;	
	private IDialogFragmentDeleteClickListener iClickListener;
	private KMLocation locationDelete;
	private Button.OnClickListener onDeleteListener;
	private Button.OnClickListener onCancelListener;
	private Bundle bundle;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		iClickListener=(IDialogFragmentDeleteClickListener)activity;
	}
	
	public void dialogInit(final KMLocation location)
	{		
		locationDelete=location;			
	}	
			
	@Override
	public void dismiss() {
		super.dismiss();
				
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		if(savedInstanceState!=null)
			locationDelete=(KMLocation)savedInstanceState.getSerializable("locationDelete");	
		
		onDeleteListener=new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {			
				iClickListener.onDeleteLocationListener(locationDelete);				
			}
		};
		
		onCancelListener=new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {
				iClickListener.onDeleteCancelListener();				
			}
		};
	
		
		if(iClickListener==null || onDeleteListener == null)
			throw new NullPointerException(DialogFragment.class.getSimpleName() +
                    " method dialogInit(..) must be called before"); 
		
			fragment=this;
		
		    View dialoglayout=null;		  		    
		    dialoglayout=inflater.inflate(R.layout.delete_location, null);
		    getDialog().setTitle(getString(R.string.stringDeleteDialogTitle));
		    
		    btnDelete= (Button)dialoglayout.findViewById(R.id.buttonDeleteDelete);
		    btnCancel= (Button)dialoglayout.findViewById(R.id.buttonDeleteCancel);
		    
		    btnDelete.setOnClickListener(onDeleteListener);
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
		bundle.putSerializable("locationDelete", locationDelete);		
	}

	
}
