package com.example.wwvideoplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LanguageSettingsActivity extends ListActivity {

	final String[] strings=new String[]{"English","简体中文 (Chinese Simplified)"};
	public static final String PREFS_NAME = "WWVPrefsFile";
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_choose_lang);
        
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(LanguageSettingsActivity.this, android.R.layout.simple_list_item_1, strings);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
        if (position == 0) {
        	editor.putString("language", "EN");
        } else {
        	editor.putString("language", "CN");
        }
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }
    
    

}
