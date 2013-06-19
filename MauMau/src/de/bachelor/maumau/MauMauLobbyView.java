package de.bachelor.maumau;

import de.ptpservice.views.LobbyActivity;

public class MauMauLobbyView extends LobbyActivity {

	@Override
	protected Class<?> getJoinChannelView() {
		return GameActivity.class;
	}

	@Override
	protected Class<?> getHostChannelView() {
		return GameActivity.class;
	}

}
