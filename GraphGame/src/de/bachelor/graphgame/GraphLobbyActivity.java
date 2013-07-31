package de.bachelor.graphgame;

import android.app.Activity;
import de.ptpservice.views.LobbyActivity;

public class GraphLobbyActivity extends LobbyActivity {

	@Override
	protected Class<? extends Activity> getJoinSessionView() {
		return DrawActivity.class;
	}

	@Override
	protected Class<? extends Activity> getHostSessionView() {
		return  DrawActivity.class;
	}

}
