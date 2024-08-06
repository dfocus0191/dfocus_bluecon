package com.example.remix.handler;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.widget.Toast;

public class BackPressCloseHandler extends Activity {

	    private long backKeyPressedTime = 0;
	    private Toast toast;
	 
	    private Activity activity;
	 
	    public BackPressCloseHandler(Activity context) {
	        this.activity = context;
	    }
	 
	    public void onBackPressed() {
	        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
	            backKeyPressedTime = System.currentTimeMillis();
	            showGuide();
	            return;
	        }
	        
	        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
	            
	        	
	        	
	        	
	        	//finish();
	        	 
	            int nSDKVersion = Integer.parseInt(Build.VERSION.SDK);
	            if(nSDKVersion < 8)    //2.1이하
	            {
	                  ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	                  actMng.restartPackage(getPackageName());
	            }
	            else
	            {
	            	  ActivityManager activityManager  = (ActivityManager)activity.getSystemService(Activity.ACTIVITY_SERVICE);
	            	  activityManager.killBackgroundProcesses(activity.getPackageName());
	            }
	            
	            toast.cancel();
	        }
	    }
	 
	    private void showGuide() {
	        toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르시면 종료됩니다.",
	                Toast.LENGTH_SHORT);
	        toast.show();
	    }

	
}
