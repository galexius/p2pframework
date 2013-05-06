package com.example.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    public static final String EXTRA_MESSAGE = "com.example.firstapp.MESSAGE";
	private MainApplication application;

    
     
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
        application = (MainApplication) getApplication();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void connect(View view){
    	EditText edit = (EditText)findViewById(R.id.connect_channel_name);
    	application.setChannelName(edit.getText().toString());
    	application.joinChannel();
    	Intent intent = new Intent(this, Draw.class);
    	startActivity(intent);    	
    }
    
    public void create(View view){
    	EditText edit = (EditText)findViewById(R.id.create_channel_name);
    	application.setChannelName(edit.getText().toString());
    	application.setHostChannelName(edit.getText().toString());
    	application.hostInitChannel();    	
    	application.hostStartChannel();
    	Intent intent = new Intent(this, Draw.class);
    	startActivity(intent);  
    }
    
    public void disconnect(View view){
    	MainApplication application = (MainApplication) getApplication();
    	application.leaveChannel();
    	application.hostStopChannel();
    }
    
}
