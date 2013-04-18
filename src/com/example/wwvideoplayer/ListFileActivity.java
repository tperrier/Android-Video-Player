package com.example.wwvideoplayer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListFileActivity extends ListActivity {
	public final static String EXTRA_MESSAGE = "com.example.wwvideoplayer.MESSAGE";
	
	private File currDir;
	ArrayList<HashMap<String, Object>> fileList = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		ListDir(currDir);
	}
	
	private void ListDir(File f){
		this.setTitle(getString(R.string.title_choose_file) + ": " + f.getAbsolutePath());
		
		File[] files = f.listFiles(new VideoFilter()); //uses inner class HiddenFilter, could be removed if want all files
		fileList.clear();
		
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		fileInfo.put("item_pic", R.drawable.icon_parent_directory);
		fileInfo.put("item_text", "Upper Directory");
		fileList.add(fileInfo);
		
		for (File file : files){
				HashMap<String, Object> fileInfo2 = new HashMap<String, Object>();
				if (file.isDirectory()) {
					fileInfo2.put("item_pic", R.drawable.icon_directory);
				} else{
					fileInfo2.put("item_pic", R.drawable.icon_file);
				}
				fileInfo2.put("item_text", file.getName());
				fileList.add(fileInfo2); 
		}
		  
		SimpleAdapter adapter=new SimpleAdapter(this, fileList, R.layout.list_file_item, 
												new String[] {"item_pic","item_text"}, 
												new int[] {R.id.item_pic, R.id.item_text});
	    setListAdapter(adapter);
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if (position == 0) {
    		if (currDir.getParentFile() != null) {
    			currDir = currDir.getParentFile();
    			ListDir(currDir);
    		} else {
    			Toast.makeText(getApplicationContext(), "Oops... We hit the root directory",
    				     Toast.LENGTH_SHORT).show();
    		}
    		
    	} else {
    		File targetDir = currDir.listFiles(new VideoFilter())[position - 1];
    		if (targetDir.isDirectory()){
    			currDir = targetDir;
        		ListDir(currDir);    		
        	} else {
				String filePath = targetDir.getAbsolutePath();
		    	Intent intent = new Intent(this, PlayVideoActivity.class);
		    	intent.putExtra(EXTRA_MESSAGE, filePath);
		    	startActivity(intent);
			}
    	}
    }
    
    // inner class to filter out non-JPEG and hidden files/Directories
	private class VideoFilter implements FileFilter {

		@Override
        public boolean accept(File file) {
        	//Log.w(file.getName(), "FILENAME");
        	if (file.isHidden()) {
        		return false;
        	}
        	if (file.isDirectory()) {
        		return true;
        	}
        	
        	String[] fileNameStrings = file.getName().split("\\.");
        	if (fileNameStrings.length > 1) {
        		if (fileNameStrings[fileNameStrings.length - 1].toLowerCase().equals("mp4")) {
        			
        			return true;
        		}
        	}
        	return false;
        }      
    }
	
}
