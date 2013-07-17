package de.bachelor.maumau;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class OwnCardsAdapter extends BaseAdapter {
	private final Context context;
	private GameManager gameManager;

	 public OwnCardsAdapter(final Context c,GameManager gameManager) {
		 context = c;
		 this.gameManager = gameManager;
	 }

	 @Override
	 public int getCount() {
		 int size = gameManager.getOwnCards().size();
		 return size;
	 }
	
	 @Override
	 public Object getItem(final int position) {
		 return gameManager.getOwnCards().get(position);
	 }
	
	 @Override
	 public long getItemId(final int position) {
		 return position;
	 }
	
	 @Override
	 public View getView(final int position, final View convertView, final ViewGroup parent) {	 
		  ImageView imageView = new ImageView(context);
		  Card card = gameManager.getOwnCards().get(position);
		  Bitmap bitmap = gameManager.getBitmap(context, card);
		  imageView.setImageBitmap(bitmap);
		  imageView.setTag(card);
		  
		  imageView.setLayoutParams(new Gallery.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
		  return imageView;		  
	 }
}