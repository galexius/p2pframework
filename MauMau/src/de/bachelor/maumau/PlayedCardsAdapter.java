package de.bachelor.maumau;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PlayedCardsAdapter extends BaseAdapter {
	private final Context context;
	private GameManager gameManager;

	 public PlayedCardsAdapter(final Context c,GameManager gameManager) {
		 context = c;
		 this.gameManager = gameManager;
	 }

	 @Override
	 public int getCount() {
		 return gameManager.getPlayedCard() != null ? 1: 0;
	 }
	
	 @Override
	 public Object getItem(final int position) {
		 if(position == 0)
			 return gameManager.getPlayedCard();
		 return null;
	 }
	
	 @Override
	 public long getItemId(final int position) {
		 return position;
	 }
	
	 @Override
	 public View getView(final int position, final View convertView, final ViewGroup parent) {	  	
	  ImageView imageView = new ImageView(context);
	  Card card = gameManager.getPlayedCard();
	  imageView.setImageBitmap(gameManager.getBitmap(context, card));
	  imageView.setTag(card);
	  return imageView;
	 }
}