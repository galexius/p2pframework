package de.bachelor.maumau;

import android.app.Activity;
import de.ptpservice.views.LobbyActivity;

public class MauMauLobbyView extends LobbyActivity {

	@Override
	protected Class<? extends Activity> getJoinSessionView() {
		return GameActivity.class;
	}

	@Override
	protected Class<? extends Activity> getHostSessionView() {
		return GameActivity.class;
	}

}
