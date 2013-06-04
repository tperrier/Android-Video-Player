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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.VideoView;

public class PlayVideoActivity extends Activity {

	public TreeMap<Integer, ArrayList<String>> allTags;
	public VideoView videoView;
	public ListView lv;
	private ActionBar actionBar;
	private SeekBar sb;
	private boolean isPause = false;
	private ImageButton playPause;
	private TextView sbTime;
	private LinearLayout ll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);
		actionBar = getActionBar();
		actionBar.hide();
		this.setTitle(R.string.title_now_playing);
		Intent intent = getIntent();
		String action = intent.getAction();
		String path = "";
		if(Intent.ACTION_VIEW.equals(action)){
			path = intent.getData().getPath();
		} else {
			path = intent.getStringExtra(ListFileActivity.EXTRA_MESSAGE);
		}
		//Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
		processJson(path.substring(0, path.length() - 4) + ".json");		
		processListView(path);
		processVideoView(path);
		processSeekBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_play_video, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // CAUTION: Compatibility Issue
        // Must not attach any functions to any menu items.
        // Instead, attach them all here for the app to work on
        // some special tablets like SCOMP...
       switch (item.getItemId()) {
           case R.id.browse_files_play:
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
                alist, R.layout.movie_frame_item, 
                new String[] {"tag","time"},
                new int[] {R.id.item_text1, R.id.item_text2});
        lv.setAdapter(adapter);
        lv.setVisibility(View.INVISIBLE);
        
        //listview listener
        lv.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        		  int jumpTo = (Integer) allTags.keySet().toArray()[position];
        		  if (!(jumpTo * 1000 > videoView.getDuration())) {
        		    // pause current frame
        			videoView.pause();
        			// change frame
        			videoView.seekTo(jumpTo * 1000); //change seconds to milliseconds
        			
        			// CAUTION: Compatibility Issue
        			// The following two lines seem stupid, but needed
        			// for the app to work on some special tablets like SCOMP...
                    videoView.start();
                    videoView.pause();
                    isPause = true;
                    playPause.setImageResource(R.drawable.play);
        		  }
        	  }
        	}); 
        
        
    }
    
    private void processVideoView(String path){
        playPause = (ImageButton) findViewById(R.id.button_pause_play);
        ll = (LinearLayout) findViewById(R.id.customized_media_controller_layout);
        sbTime = (TextView) findViewById(R.id.seek_bar_time);
        sbTime.setText(convertMilliSecondToHumanReadable(0));
    	videoView = (VideoView) findViewById(R.id.video_view);
		videoView.setVideoPath(path);
		videoView.start();
		videoView.requestFocus();
		videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                processSeekBar();
                processButtons();
                TextView totalTime = (TextView) findViewById(R.id.total_time);
                totalTime.setText(convertMilliSecondToHumanReadable(videoView.getDuration()));
            }		    
		});
		
		videoView.setOnTouchListener(new OnTouchListener() {
			private boolean lvShowed = false;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
            	if (lvShowed) {
            		lv.setVisibility(View.INVISIBLE);
            		ll.setVisibility(View.INVISIBLE);
                    actionBar.hide();
                    lvShowed = false;
            	} else {
            		lv.setVisibility(View.VISIBLE);
            		ll.setVisibility(View.VISIBLE);
                    actionBar.show();
                    lvShowed = true;
            	}
				return false;
			}
		});	
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
    
    private void processSeekBar() {
        sb = (SeekBar) findViewById(R.id.seek_bar);
        sb.setMax(videoView.getDuration());
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            
            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    videoView.seekTo(progress); //change seconds to milliseconds
                    
                    // CAUTION: Compatibility Issue
                    // The following two lines seem stupid, but needed
                    // for the app to work on some special tablets like SCOMP...
                    videoView.start();
                    videoView.pause();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                videoView.pause();
                isPause = true;
                playPause.setImageResource(R.drawable.play);
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                isPause = false;
                videoView.start();
                playPause.setImageResource(R.drawable.pause);
            }
            
        });
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    sb.setProgress(videoView.getCurrentPosition());
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sbTime.setText(convertMilliSecondToHumanReadable(videoView.getCurrentPosition()));                        
                        }
                        
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }).start();
    }
    
    private void processButtons() {
        ImageButton rewind = (ImageButton) findViewById(R.id.button_jump_left);
        rewind.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                videoView.pause();
                int currentPosition = videoView.getCurrentPosition();
                videoView.seekTo(currentPosition - 30000);
                videoView.start();
            }
        });
        
        ImageButton forward = (ImageButton) findViewById(R.id.button_jump_right);
        forward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                videoView.pause();
                int currentPosition = videoView.getCurrentPosition();
                videoView.seekTo(currentPosition + 30000);
                videoView.start();
            }
        });
        
        playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isPause) {
                    isPause = false;
                    videoView.start();
                    playPause.setImageResource(R.drawable.pause);
                } else {
                    isPause = true;
                    videoView.pause();
                    playPause.setImageResource(R.drawable.play);
                }
            }
        });
    }
    
    private static String convertMilliSecondToHumanReadable(int timeInMillis) {
        int time = timeInMillis / 1000;
        int second = time % 60;
        int minute = (time / 60) % 60;
        int hour = time / 3600; String timeString = "";
        if (hour > 0) { timeString += hour + ":"; }
        if (minute < 10) {
            timeString += "0";
        }
        timeString += minute + ":";
        if (second < 10) {
            timeString += "0";
        }
        timeString += second;
        return timeString;
    }
}
