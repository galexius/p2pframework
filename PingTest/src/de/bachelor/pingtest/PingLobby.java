package de.bachelor.pingtest;

import de.ptpservice.views.LobbyActivity;

public class PingLobby extends LobbyActivity{

	@Override
	protected Class<?> getHostSessionView() {
		return MainActivity.class;
	}

	@Override
	protected Class<?> getJoinSessionView() {
		return MainActivity.class;
	}

}
