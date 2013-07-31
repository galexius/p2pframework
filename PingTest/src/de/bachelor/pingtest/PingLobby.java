package de.bachelor.pingtest;

import android.app.Activity;
import de.ptpservice.views.LobbyActivity;

public class PingLobby extends LobbyActivity{

	@Override
	protected Class<? extends Activity> getHostSessionView() {
		return MainActivity.class;
	}

	@Override
	protected Class<? extends Activity> getJoinSessionView() {
		return MainActivity.class;
	}

}
