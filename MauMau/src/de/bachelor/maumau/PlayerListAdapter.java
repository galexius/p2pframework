package de.bachelor.maumau;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.ptpservice.PTPHelper;

final class PlayerListAdapter extends ArrayAdapter<String> {

	private GameManager gameManager;
	ArrayList<String> playerList = new ArrayList<String>();

	PlayerListAdapter(MauMauApplication application,Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		gameManager = application.getGameManager();
		refresh();
	}

	public void refresh() {
		playerList.clear();
		Map<String, String> joinedPlayers = gameManager.getJoinedPlayers();
		
		for (String key : joinedPlayers.keySet()) {
			String playerName = PTPHelper.getInstance().getUniqueID().equals(key) ? "Me" : joinedPlayers.get(key);
			int numberOfCards = gameManager.getCardsForPlayersId(key).size();
			String currrentTurn = gameManager.getCurrentPlayersID().equals(key) ? "CurrentTurn" : "";
			
			playerList.add(playerName+", Cards: " + numberOfCards + " " + currrentTurn );
		}
	}

	@Override
	public int getCount() {
		 return playerList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
         TextView textView = new TextView(getContext());	
         textView.setTextColor(Color.WHITE);
         textView.setText(playerList.get(position));         
         return textView;
	}
	
}