package com.example.ctrain;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private Fragment fragment;
    private final Activity activity;
    private final String tag;
    private final Class<T> cls;
    
    public TabListener(
      Activity activity, String tag, Class<T> cls){
      this.activity = activity;
      this.tag = tag;
      this.cls = cls;
    }
      
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
      if(fragment == null){
        fragment = Fragment.instantiate(activity, cls.getName());
        FragmentManager fm = activity.getFragmentManager();  
        fm.beginTransaction().add(R.id.container, fragment, tag).commit();
      }
      else{
    	  if (fragment.isDetached()) {  
	          FragmentManager fm = activity.getFragmentManager();  
	          fm.beginTransaction().attach(fragment).commit();  
          }
      }      
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
      if(fragment != null){
    	  FragmentManager fm = activity.getFragmentManager();  
          fm.beginTransaction().detach(fragment).commit();
      }      
    }

}