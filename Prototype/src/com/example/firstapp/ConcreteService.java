package com.example.firstapp;

import de.p2pservice.P2PService;

public class ConcreteService extends P2PService<GraphInterface> {

	@Override
	protected Class<GraphInterface> getBusObjectInterface() {
		return GraphInterface.class;
	}

}
