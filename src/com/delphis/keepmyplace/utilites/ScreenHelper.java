package com.delphis.keepmyplace.utilites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;

public class ScreenHelper {
	
	public static Bitmap loadBitmapFromView(Context context, View v) {
	    DisplayMetrics dm = context.getResources().getDisplayMetrics(); 
	    v.measure(MeasureSpec.makeMeasureSpec(dm.widthPixels, MeasureSpec.EXACTLY),
	            MeasureSpec.makeMeasureSpec(dm.heightPixels, MeasureSpec.EXACTLY));
	    v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
	    Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
	            v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas(returnedBitmap);
	    v.draw(c);

	    return returnedBitmap;
	}
	
	public void takeScreen(Context context, View view) {
	    Bitmap bitmap = loadBitmapFromView(context, view); //get Bitmap from the view
	    String mPath = Environment.getExternalStorageDirectory() + File.separator + "screen_" + System.currentTimeMillis() + ".jpeg";
	    File imageFile = new File(mPath);
	    OutputStream fout = null;
	    try {
	        fout = new FileOutputStream(imageFile);
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
	        fout.flush();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

}
