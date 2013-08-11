package de.ptpservice.views;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
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
import de.ptpservice.PTPManager;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class LobbyActivity extends AbstractLobbyActivity {

	private ListView sessionList;
	private Button refreshButton;
	private Button createButtn;
	
	protected abstract Class<? extends Activity> getJoinSessionView();
	protected abstract Class<? extends Activity> getHostSessionView();
     
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.lobby_activity);       
        PTPManager.getInstance().addLobbyObserver(this);
        createButtn = (Button) findViewById(R.id.create_button);
        refreshButton = (Button) findViewById(R.id.refresh_button);
        sessionList = (ListView) findViewById(R.id.join_session_list);
        setupListView();
        updateUIState(PTPManager.getInstance().getConnectionState());
        PTPManager.getInstance().connectAndStartDiscover();
    }
	
	@Override
	protected void onDestroy() {
		PTPManager.getInstance().removeLobbyObserver(this);
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
    		PTPManager.getInstance().setPlayerName(playerName);
    		PTPManager.getInstance().hostStartSession(channelName);
    		updateUIState(PTPManager.SESSION_HOSTED);
    	}

    }
    
    public void quit(View view){
    	PTPManager.getInstance().quit();
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
					String sessionName = sessionList.getItemAtPosition(position).toString();
					PTPManager.getInstance().setPlayerName(playerName);
					PTPManager.getInstance().joinSession(sessionName);
					updateUIState(PTPManager.SESSION_JOINED);
	        	}
			}
    	});
    	
    	
        sessionList.setAdapter(sessionListAdapter);
        
		List<String> sessions = PTPManager.getInstance().getFoundSessions();
        for (String session : sessions) {
        	sessionListAdapter.add(session);
        }
	    sessionListAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void connectionStateChanged(final int connectionState){
		updateUIState(connectionState);
		if(connectionState == PTPManager.SESSION_HOSTED){
			Intent intent = new Intent(LobbyActivity.this, getHostSessionView());
			LobbyActivity.this.startActivity(intent);
		}
		if(connectionState == PTPManager.SESSION_JOINED){
			Intent intent = new Intent(LobbyActivity.this, getJoinSessionView());
			LobbyActivity.this.startActivity(intent);
		}
		if(connectionState == PTPManager.SESSION_NAME_EXISTS){
			Context context = getApplicationContext();
	    	CharSequence text = getResources().getText(R.string.game_exists);
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
		}
    }
	private void updateUIState(int connectionState) {
		boolean connected = connectionState == PTPManager.CONNECTED || connectionState == PTPManager.SESSION_CLOSED || connectionState == PTPManager.SESSION_LEFT;
		createButtn.setEnabled(connected);
		refreshButton.setEnabled(connected);
		sessionList.setEnabled(connected);		
	}    
}
