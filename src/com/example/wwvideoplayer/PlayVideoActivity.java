package com.example.wwvideoplayer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.VideoView;

public class PlayVideoActivity extends Activity {

	public TreeMap<Integer, ArrayList<String>> allTags;
	public VideoView videoView;
	public ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);
		this.setTitle(R.string.title_now_playing);
		Intent intent = getIntent();
		String path = intent.getStringExtra(ListFileActivity.EXTRA_MESSAGE);
		processJson(path.substring(0, path.length() - 4) + ".json");
		
		
		processVideoView(path);
		processListView(path);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_play_video, menu);
		return true;
	}
	
    public void listFile(MenuItem i) {
        // Do something in response to button
    	Intent intent = new Intent(this, ListFileActivity.class);
    	startActivity(intent);
    }
    
    private void processListView(String path){
    	lv=(ListView)findViewById(R.id.listView1);
        ArrayList<HashMap<String, String>> alist=new ArrayList<HashMap<String, String>>();
        	for (Integer i : allTags.keySet()) {
                HashMap<String, String> hMap=new HashMap<String, String>();
                hMap.put("tag", allTags.get(i).get(0));
                hMap.put("time", allTags.get(i).get(1));
                alist.add(hMap);
        	}
        
        SimpleAdapter adapter=new SimpleAdapter(this, 
                alist, android.R.layout.two_line_list_item, 
                new String[] {"tag","time"},
                new int[] {android.R.id.text1,android.R.id.text2});
        lv.setAdapter(adapter);
        
        //listview listener
        lv.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        		  int jumpTo = (Integer) allTags.keySet().toArray()[position];
        		  if (!(jumpTo * 1000 > videoView.getDuration())) {
        			videoView.pause();
        			videoView.seekTo(jumpTo * 1000); //change seconds to milliseconds
        			//videoView.start();
        		  }
        	  }
        	}); 
        
        
    }
    
    private void processVideoView(String path){
    	videoView = (VideoView) findViewById(R.id.video_view);
		MediaController mc = new MediaController(this, true);
		//mc.setAnchorView(videoView);
		
		videoView.setMediaController(mc);  			

		videoView.setVideoPath(path);
		videoView.start();  
		videoView.requestFocus(); 
    }

    private void processJson(String path) {
    	JSONParser parser = new JSONParser();
    	allTags = new TreeMap<Integer, ArrayList<String>>();
		try {
			Object obj = parser.parse(new FileReader(new File(path)));
			

			JSONObject rootObject = (JSONObject) obj;
			JSONArray tags = (JSONArray)rootObject.get("Tags");
			
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> it = tags.iterator();

			
			while (it.hasNext()) {

				JSONObject currTag = it.next();
				ArrayList<String> list = new ArrayList<String>();
				list.add(currTag.get("name").toString());

				String timeString = currTag.get("times").toString();

				int insertTime = 0;
				if (timeString.length() > 5) {
					insertTime += Integer.parseInt(timeString.substring(0, timeString.length() - 6)) * 3600;
				}

				if (timeString.length() > 4) {
					insertTime += Integer.parseInt(timeString.substring(timeString.length() - 5, timeString.length() - 3)) * 60;
				}else if (timeString.length() > 3) {
					insertTime += Integer.parseInt(timeString.substring(timeString.length() - 4, timeString.length() - 3)) * 60;
				}

				if (timeString.length() > 1) {
					insertTime += Integer.parseInt(timeString.substring(timeString.length() - 2, timeString.length()));
				} else {
					insertTime += Integer.parseInt(timeString);
				}

				list.add(timeString);
				list.add(currTag.get("insert_sentence").toString());
				allTags.put(insertTime, list);	
			}
			
		} catch (Exception e){
			ArrayList<String> list = new ArrayList<String>();
			list.add("ERROR");
			list.add("000");
			list.add("sentence ERROR");
			allTags.put(111, list);
			e.printStackTrace();
		}
    }
    
}
