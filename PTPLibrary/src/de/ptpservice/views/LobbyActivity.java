package de.ptpservice.views;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.p2pservice.R;
import de.ptpservice.PTPHelper;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class LobbyActivity extends AbstractLobbyActivity {

	private ListView sessionList;
	private Button refreshButton;
	private Button createButtn;
	
	protected abstract Class<?> getJoinSessionView();
	protected abstract Class<?> getHostSessionView();
     
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.lobby_activity);       
        PTPHelper.getInstance().addLobbyObserver(this);
        createButtn = (Button) findViewById(R.id.create_button);
        refreshButton = (Button) findViewById(R.id.refresh_button);
        sessionList = (ListView) findViewById(R.id.join_session_list);
        setupListView();
        updateUIState(PTPHelper.getInstance().getConnectionState());
        PTPHelper.getInstance().connectAndStartDiscover();
    }
	
	@Override
	protected void onDestroy() {
		PTPHelper.getInstance().removeLobbyObserver(this);
		super.onDestroy();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    public void create(View view){
    	EditText channel = (EditText)findViewById(R.id.create_session_name);
    	EditText player = (EditText)findViewById(R.id.player_name);
    	String playerName = player.getText().toString();
    	String channelName = channel.getText().toString();
    	if(playerName.isEmpty() || channelName.isEmpty()){
    		Context context = getApplicationContext();
	    	CharSequence text = context.getResources().getText(R.string.field_empty);
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
    	}else{
    		PTPHelper.getInstance().setHostSessionName(channelName);
    		PTPHelper.getInstance().setPlayerName(playerName);
    		PTPHelper.getInstance().hostStartSession();
    		updateUIState(PTPHelper.SESSION_HOSTED);
    	}

    }
    
    public void quit(View view){
    	PTPHelper.getInstance().quit();
    }
    
    public void refresh(View view){
    	setupListView();
    }
    
    private void setupListView(){
        ArrayAdapter<String> sessionListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);    	

    	sessionList.setOnItemClickListener(new ListView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	EditText player = (EditText)findViewById(R.id.player_name);
	        	String playerName = player.getText().toString();
	        	if(playerName.isEmpty()){
	        		Context context = getApplicationContext();
	    	    	CharSequence text = context.getResources().getString(R.string.player_name_empty);
	    	    	int duration = Toast.LENGTH_SHORT;

	    	    	Toast toast = Toast.makeText(context, text, duration);
	    	    	toast.show();
	        	}else{
					String name = sessionList.getItemAtPosition(position).toString();
					PTPHelper.getInstance().setClientSessionName(name);
					PTPHelper.getInstance().setPlayerName(playerName);
					PTPHelper.getInstance().joinSession();
					updateUIState(PTPHelper.SESSION_JOINED);
	        	}
			}
    	});
    	
    	
        sessionList.setAdapter(sessionListAdapter);
        
		List<String> sessions = PTPHelper.getInstance().getFoundSessions();
        for (String session : sessions) {
        	sessionListAdapter.add(session);
        }
	    sessionListAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void connectionStateChanged(final int connectionState){
		updateUIState(connectionState);
		if(connectionState == PTPHelper.SESSION_HOSTED){
			Intent intent = new Intent(LobbyActivity.this, getHostSessionView());
			LobbyActivity.this.startActivity(intent);
		}
		if(connectionState == PTPHelper.SESSION_JOINED){
			Intent intent = new Intent(LobbyActivity.this, getJoinSessionView());
			LobbyActivity.this.startActivity(intent);
		}
		if(connectionState == PTPHelper.SESSION_NAME_EXISTS){
			Context context = getApplicationContext();
	    	CharSequence text = getResources().getText(R.string.game_exists);
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
		}
    }
	private void updateUIState(int connectionState) {
		boolean connected = connectionState == PTPHelper.CONNECTED || connectionState == PTPHelper.SESSION_CLOSED || connectionState == PTPHelper.SESSION_LEFT;
		createButtn.setEnabled(connected);
		refreshButton.setEnabled(connected);
		sessionList.setEnabled(connected);		
	}    
}
