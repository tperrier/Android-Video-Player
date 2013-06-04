package com.example.wwvideoplayer;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "WWVPrefsFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		VideoPlayerInitialization();
		
		//display interface
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // CAUTION: Compatibility Issue
        // Must not attach any functions to any menu items.
	    // Instead, attach them all here for the app to work on
	    // some special tablets like SCOMP...
	   switch (item.getItemId()) {
	      case R.id.change_language_main:
	         changeLanguage(item);
	         return true;
          case R.id.browse_files_main:
             listFile(item);
             return true; 
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}
	
    public void listFile(MenuItem i) {
        // Do something in response to button
    	Intent intent = new Intent(this, ListFileActivity.class);
    	startActivity(intent);
    }
    
    public void changeLanguage(MenuItem i) {
        // Do something in response to button
    	Intent intent = new Intent(this, LanguageSettingsActivity.class);
    	startActivity(intent);
    }
    
	private void VideoPlayerInitialization() {
		initializeLanguage(); //choose a language to display
		setTitle(R.string.app_name); //Set the title for main activity
	}
	
	private void initializeLanguage(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String lang = settings.getString("language", "NO_LANG");
		if (!lang.equals("NO_LANG")) {
	        Resources resources = getResources(); 
	        Configuration config = resources.getConfiguration(); 
			if (lang.equals("CN")) {
				config.locale = Locale.SIMPLIFIED_CHINESE;
			} else {
				config.locale = Locale.ENGLISH;
			}
	        DisplayMetrics dm = resources.getDisplayMetrics(); 
	        resources.updateConfiguration(config, dm);
		}
	}

}
