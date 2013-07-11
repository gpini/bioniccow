package it.bova.bioniccow.utilities;

import it.bova.bioniccow.InterProcess;
import it.bova.bioniccow.R;
import it.bova.bioniccow.R.id;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SmartFragmentManager_old implements InterProcess {

	public static void startFragment(SherlockFragmentActivity activity,
			SherlockFragment fragment, int containerId, String tag) {
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(fm.findFragmentById(containerId) == null)
		//if(attachedFragmentMap.get(activity) == null)
			ft.add(containerId, fragment, tag).commit();
		else
			ft.replace(containerId, fragment, tag).commit();
		//attachedFragmentMap.put(activity, fragment);
	}
	
	public static String getAttachedFragmentTag(SherlockFragmentActivity activity, int containerId) {
		FragmentManager fm = activity.getSupportFragmentManager();
		Fragment sf = fm.findFragmentById(containerId);
		if(sf != null)
			return sf.getTag();
		else return null;
	}
	
	public static Fragment getAttachedFragment(SherlockFragmentActivity activity, int containerId) {
		FragmentManager fm = activity.getSupportFragmentManager();
		return fm.findFragmentById(containerId);
	}	
	
	public static void startFragmentToBackStack(SherlockFragmentActivity activity,
			SherlockFragment fragment, int containerId, String tag) {
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		ft.replace(R.id.fragmentContainer, fragment, tag).commit();
	}
	

}
