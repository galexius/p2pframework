package de.bachelor.graphgame;

import de.p2pservice.views.LobbyActivity;

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
