package de.p2pservice.views;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.p2pservice.P2PApplication;
import de.p2pservice.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class LobbyActivity extends Activity {

	private P2PApplication application;
	
	protected abstract Class<?> getJoinChannelView();
	protected abstract Class<?> getHostChannelView();
     
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
        application = (P2PApplication) getApplication();
        setupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    public void create(View view){
    	EditText channel = (EditText)findViewById(R.id.create_channel_name);
    	EditText player = (EditText)findViewById(R.id.player_name);
    	String playerName = player.getText().toString();
    	String channelName = channel.getText().toString();
    	if(playerName.isEmpty() || channelName.isEmpty()){
    		Context context = getApplicationContext();
	    	CharSequence text = "Player name or channel name is empty";
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
    	}else{
			application.setChannelName(channelName);
	    	application.setHostChannelName(channelName);
	    	application.setPlayerName(playerName);
	    	application.doAction(P2PApplication.HOST_CHANNEL);
	    	Intent intent = new Intent(this, getJoinChannelView());
	    	startActivity(intent);  
    	}

    }
    
    public void quit(View view){
    	application.doAction(P2PApplication.QUIT);
    }
    
    public void refresh(View view){
    	setupListView();
    }
    
    private void setupListView(){
        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
    	final ListView channelList = (ListView)findViewById(R.id.join_channel_list);

    	channelList.setOnItemClickListener(new ListView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	EditText player = (EditText)findViewById(R.id.player_name);
	        	String playerName = player.getText().toString();
	        	if(playerName.isEmpty()){
	        		Context context = getApplicationContext();
	    	    	CharSequence text = "Player name is empty";
	    	    	int duration = Toast.LENGTH_SHORT;

	    	    	Toast toast = Toast.makeText(context, text, duration);
	    	    	toast.show();
	        	}else{
					String name = channelList.getItemAtPosition(position).toString();
					application.setChannelName(name);
					application.doAction(P2PApplication.JOIN_CHANNEL);
					application.setPlayerName(playerName);
					Intent intent = new Intent(LobbyActivity.this, getJoinChannelView());
					LobbyActivity.this.startActivity(intent);
	        	}
			}
    	});
    	
        channelList.setAdapter(channelListAdapter);
        
	    List<String> channels = application.getFoundChannels();
        for (String channel : channels) {
        	channelListAdapter.add(channel);
        }
	    channelListAdapter.notifyDataSetChanged();
    }
    
}
