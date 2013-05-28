package com.example.firstapp;

import de.p2pservice.views.LobbyActivity;

public class MyLobbyActivity extends LobbyActivity {

	@Override
	protected Class<?> getJoinChannelView() {
		return Draw.class;
	}

	@Override
	protected Class<?> getHostChannelView() {
		return  Draw.class;
	}

}
