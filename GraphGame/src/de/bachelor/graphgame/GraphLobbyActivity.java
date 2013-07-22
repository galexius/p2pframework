package de.bachelor.graphgame;

import de.ptpservice.views.LobbyActivity;

public class GraphLobbyActivity extends LobbyActivity {

	@Override
	protected Class<?> getJoinSessionView() {
		return DrawActivity.class;
	}

	@Override
	protected Class<?> getHostSessionView() {
		return  DrawActivity.class;
	}

}
