package de.bachelor.graphgame;

import de.ptpservice.views.LobbyActivity;

public class GraphLobbyActivity extends LobbyActivity {

	@Override
	protected Class<?> getJoinChannelView() {
		return DrawActivity.class;
	}

	@Override
	protected Class<?> getHostChannelView() {
		return  DrawActivity.class;
	}

}
