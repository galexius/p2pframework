package de.bachelor.graphgame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import de.ptpservice.PTPManager;

public class DrawActivity extends Activity {
    DrawView drawView;
	private MainApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Set full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        application = (MainApplication) getApplication();
		drawView = new DrawView(this,application);
        setContentView(drawView);
        drawView.requestFocus();
    }
    
	@Override
	public void onBackPressed(){
	  super.onBackPressed();
	  application.getGraph().resetGraph();
	  PTPManager.getInstance().leaveSession();
	  PTPManager.getInstance().disconnect();
	  PTPManager.getInstance().connectAndStartDiscover();
	}
}