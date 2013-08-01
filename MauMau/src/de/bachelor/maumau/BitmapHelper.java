package de.bachelor.maumau;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {
	
	private static Bitmap allCards = null;

	public static Bitmap getBitmap(Context context,Card card){
		if(allCards == null){
			allCards = BitmapFactory.decodeResource(context.getResources(),
	             R.drawable.cards);
		}			
		int cardWidth = allCards.getWidth()/13;
		int cardHeight = allCards.getHeight()/5;
		int value = card.value;
		if(value == 14) value = 1; //Ace is positioned at the beginning of the bitmap
		Bitmap cardBitmap = Bitmap.createBitmap(allCards,(value-1)*(cardWidth), (card.suit)*(cardHeight),cardWidth, cardHeight);
		return cardBitmap;			
	}
	
	public static Bitmap getCardsBackBitmap(Context context){
		Card card = new Card();
		card.suit = 4;
		card.value = 3;
		return getBitmap(context, card);			
	}
}
