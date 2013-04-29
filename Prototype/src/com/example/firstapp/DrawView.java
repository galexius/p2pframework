package com.example.firstapp;

import org.alljoyn.bus.BusException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class DrawView extends View implements OnTouchListener,Observer {
    private static float TOUCH_SIZE;

	private static final String TAG = "DrawView";

       
    Paint paint = new Paint();
    boolean pointHit = false;
    Node redPoint = new Node();
    Node bluePoint = new Node();   

	private Bitmap redButton;
	private Bitmap blueButton;

	private MainApplication application;

	private int displayWidth;
	private int displayHeight;

	private int offset;

	private Node hitPoint;
    
    @SuppressWarnings("deprecation")
	public DrawView(Context context, MainApplication application) {
        super(context);
		this.application = application;
		application.addObserver(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();        
        
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
               
        TOUCH_SIZE = displayWidth * 0.2f;
        
        paint.setColor(Color.RED);
        this.setOnTouchListener(this);

        redButton = BitmapFactory.decodeResource(getResources(),
                R.drawable.red_button);
        blueButton = BitmapFactory.decodeResource(getResources(),
        		R.drawable.blue_button);
        offset = redButton.getWidth()/2;
    }
    


	@Override
    public void onDraw(Canvas canvas) {
		Graph graph = application.getGraph();
		Log.d(TAG, "onDraw()");
		try {
			for (Edge edge : graph.getAllEdge()) {
				canvas.drawLine(edge.getFrom().x*displayWidth, edge.getFrom().y*displayHeight, edge.getTo().x*displayWidth, edge.getTo().y*displayHeight, paint);
			}
		
			for (Node point : graph.getAllNodes()) {
				if(!point.getOwner().isEmpty() && !application.getUserId().equals(point.getOwner())){				
					canvas.drawBitmap(redButton, (point.x*displayWidth)-offset, (point.y*displayHeight)-offset, null);	
				}else{
					canvas.drawBitmap(blueButton, (point.x*displayWidth)-offset, (point.y*displayHeight)-offset, null);
				}
				
			}
		} catch (BusException e) {
			e.printStackTrace();
		}
    }
    

    public boolean onTouch(View view, MotionEvent event) {
		Graph graph = application.getGraph();
		try {
	         if(event.getAction() == MotionEvent.ACTION_DOWN){
				for (Node point : graph.getAllNodes()) {
					 if(Math.abs(point.x*displayWidth - event.getX()) < TOUCH_SIZE && Math.abs(point.y* displayHeight - event.getY()) < TOUCH_SIZE){
						 if(point.getOwner().equals(application.getUserId()) || point.getOwner().isEmpty()){
							 pointHit = true;
							 hitPoint = point;
							 graph.ChangeOwnerOfNode(hitPoint.getId(), application.getUserId());
							 Log.d(TAG, "hit");	 
						 }
						 
					 }
				}	        	 
	         }
	         if(event.getAction() == MotionEvent.ACTION_UP && pointHit){
	        	 graph.ChangeOwnerOfNode(hitPoint.getId(), "");
	        	 pointHit = false;
	        	 return false;
	         }
	         if(pointHit && hitPoint != null){
		        hitPoint.x = event.getX()/displayWidth;
		        hitPoint.y = event.getY()/displayHeight;
		        graph.MoveNode(hitPoint.getId(), hitPoint.x, hitPoint.y);
		        invalidate();
	         }
		} catch (BusException e) {
			e.printStackTrace();
		}
        return true;
    }

	@Override
	public void update(Observable o, Object arg) {
		String qualifier = (String) arg;

		if (qualifier.equals(MainApplication.REMOTE_POINT_CHANGED_EVENT)) {
			Log.d(TAG, "Point changed notified");
			postInvalidate();
		}
		
	}
}