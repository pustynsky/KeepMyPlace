package com.delphis.keepmyplace.application;

public class Settings {
	
	private int _imageShareMode;
	
	public Settings()
	{
		
	}
	
	public Settings(int ImageShareMode)
	{
		_imageShareMode=ImageShareMode;
	}

	public int getImageShareMode() {
		return _imageShareMode;
	}

	public void setImageShareMode(int ImageShareMode) {
		this._imageShareMode = ImageShareMode;
	}
	
}
