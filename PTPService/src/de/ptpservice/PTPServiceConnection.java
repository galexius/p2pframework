package de.ptpservice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class PTPServiceConnection implements ServiceConnection {

	private static final String TAG = "PTPServiceConnection";

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder binder) {
		 Log.i(TAG, "Service " + componentName + " Connected");
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		Log.w(TAG, "Service " + componentName + " Disconnected");
	}	

}
