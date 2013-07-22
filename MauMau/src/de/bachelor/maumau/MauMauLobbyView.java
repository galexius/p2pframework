package de.bachelor.maumau;

import de.ptpservice.views.LobbyActivity;

public class MauMauLobbyView extends LobbyActivity {

	@Override
	protected Class<?> getJoinSessionView() {
		return GameActivity.class;
	}

	@Override
	protected Class<?> getHostSessionView() {
		return GameActivity.class;
	}

}
