package com.delphis.keepmyplace.activity;

import com.delphis.keepmyplace.entity.KMLocation;

public interface IDialogFragmentClickListener {
	public void onSaveLocationListener(KMLocation location);
	public void onCancelListener();	
}
