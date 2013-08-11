package de.bachelor.pingtest;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.ptpservice.PTPManager;

public class MainActivity extends Activity implements PingObserver {

	private PingData pingData;
	private ListView sessionList;
	private ArrayAdapter<String> sessionListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sessionList = (ListView) findViewById(R.id.listView1);
		sessionListAdapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item);     	   	
        sessionList.setAdapter(sessionListAdapter);
		
		MainApplication application = (MainApplication) getApplication();
		pingData = application.getPingData();
		pingData.addObserver(this);
	}
	
	public void sendPing(View view){
		Log.i("MainActivity", "sendPing pressed");
		long currentTime = System.currentTimeMillis();
		PTPManager.getInstance().sendDataToAllPeers(PingData.PING, new String[]{""+currentTime, PTPManager.getInstance().getUniqueID()});
	}

	
	@Override
	public void pingTableChanged() {

        HashMap<String, Long> pingTable = pingData.getPingTable();
        for (String key : pingTable.keySet()) {
			sessionListAdapter.add("ID: " + key + " Ping: " + pingTable.get(key));
		}		
	    sessionListAdapter.notifyDataSetChanged();	
	}
	
	
	
}
