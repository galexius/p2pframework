package de.bachelor.maumau;

import de.p2pservice.P2PService;

public class MauMauService extends P2PService<GameManagerInterface> {

	
	@Override
	protected Class<GameManagerInterface> getBusObjectInterface() {
		return GameManagerInterface.class;
	}

}
