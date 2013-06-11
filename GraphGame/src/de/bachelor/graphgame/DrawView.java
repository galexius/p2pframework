package de.bachelor.graphgame;

import org.alljoyn.bus.BusException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
import de.p2pservice.P2PHelper;

@SuppressLint({ "NewApi", "ViewConstructor" })
public class DrawView extends View implements OnTouchListener, GraphObserver {	
	SparseIntArray colorMap = new SparseIntArray ();	
    private static float TOUCH_SIZE;
	private static final String TAG = "DrawView";
       
    Paint paint = new Paint();
    Paint numbersPaint = new Paint();
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

	private Graph graph;

	private boolean isWaitingForConnection;

	private boolean isFinished = false;
	private boolean startTimeSet = false;
	private long timeStarted;
    
    @SuppressWarnings("deprecation")
	public DrawView(Context context, MainApplication application) {
        super(context);
		this.application = application;
		setupColorMap();
		graph = application.getGraph();
		graph.setupPoints();
		graph.addObserver(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();        
        
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
               
        TOUCH_SIZE = displayWidth * 0.2f;
        
        paint.setColor(Color.RED);
        paint.setStrokeWidth(displayWidth/50);
        float textSize = 0.05f*displayWidth;
		paint.setTextSize(textSize);
		numbersPaint.setTextSize(textSize);
        numbersPaint.setColor(Color.WHITE);
        this.setOnTouchListener(this);

        redButton = BitmapFactory.decodeResource(getResources(),
                R.drawable.red_button);
        blueButton = BitmapFactory.decodeResource(getResources(),
        		R.drawable.blue_button);
        offset = redButton.getWidth()/2;
    }
    
	private void setupColorMap() {
		colorMap.append(0, Color.BLUE);
		colorMap.append(1, Color.CYAN);
		colorMap.append(2, Color.GREEN);
		colorMap.append(3, Color.MAGENTA);
		colorMap.append(4, Color.RED);
		colorMap.append(5, Color.YELLOW);
	}

	@Override
    public void onDraw(Canvas canvas) {
		if(isFinished) return;
		
		if(P2PHelper.getInstance().getSignalEmitter()==null){
			canvas.drawText("Waiting for connection!", displayWidth/2, displayHeight/2, paint);
			isWaitingForConnection = true;
			return;
		}
		
		isWaitingForConnection = false;
		Graph graph = application.getGraph();
		
		try {
			int colorIndex = 0;
			for (Edge edge : graph.getAllEdge()) {
				paint.setColor(colorMap.get(colorIndex % colorMap.size()));
				colorIndex++;
				canvas.drawLine((float)edge.getFrom().x*displayWidth,(float) edge.getFrom().y*displayHeight,(float) edge.getTo().x*displayWidth,(float) edge.getTo().y*displayHeight, paint);
			}		
			for (Node point : graph.getAllNodes()) {
				if(!point.getOwner().isEmpty() && !P2PHelper.getInstance().getUniqueID().equals(point.getOwner())){				
					canvas.drawBitmap(redButton,(float) (point.x*displayWidth)-offset,(float) (point.y*displayHeight)-offset, null);	
				}else{
					canvas.drawBitmap(blueButton,(float) (point.x*displayWidth)-offset,(float) (point.y*displayHeight)-offset, null);
				}				
				canvas.drawText(""+point.getId(),(float) point.x*displayWidth - offset/4,(float) point.y*displayHeight + offset/3, numbersPaint);
			}
		} catch (BusException e) {
			e.printStackTrace();
		}
		
		if(graph.isGraphFinished()){
			isFinished = true;
			Log.i(TAG, "YOU WIN");
			Context context = getContext();
	    	CharSequence text = "Cleared in " + ((System.currentTimeMillis() - timeStarted) / 1000) + " sec, press back to return to lobby";
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
		}
    }

    public boolean onTouch(View view, MotionEvent event) {
    	if(isFinished){    		
    		return true;
    	}
    	
		if(isWaitingForConnection){
			invalidate();
			return false;
		}
		if(!startTimeSet){
			startTimeSet = true;
			timeStarted = System.currentTimeMillis();
		}
		
		Graph graph = application.getGraph();
		try {
	         if(event.getAction() == MotionEvent.ACTION_DOWN){
				for (Node point : graph.getAllNodes()) {
					 if(Math.abs(point.x*displayWidth - event.getX(0)) < TOUCH_SIZE && Math.abs(point.y* displayHeight - event.getY(0)) < TOUCH_SIZE){
						 if(!pointHit && (point.getOwner().equals(P2PHelper.getInstance().getUniqueID()) || point.getOwner().isEmpty())){
							 pointHit = true;
							 hitPoint = point;
							 graph.ChangeOwnerOfNode(hitPoint.getId(), P2PHelper.getInstance().getUniqueID(), P2PHelper.getInstance().getUniqueID());
							 Log.d(TAG, "hit");	 
						 }						 
					 }
				}	        	 
	         }
	         if(event.getAction() == MotionEvent.ACTION_UP && pointHit){
	        	 graph.ChangeOwnerOfNode(hitPoint.getId(), "", P2PHelper.getInstance().getUniqueID());
	        	 pointHit = false;
	        	 return false;
	         }
	         if(pointHit && hitPoint != null){
	        	Node nodeMove = new Node();
		        nodeMove.x = event.getX()/displayWidth - hitPoint.x;
		        nodeMove.y = event.getY()/displayHeight - hitPoint.y;
		        graph.MoveNode(hitPoint.getId(), nodeMove.x, nodeMove.y, P2PHelper.getInstance().getUniqueID());
		        invalidate();
	         }
		} catch (BusException e) {
			e.printStackTrace();
		}
        return true;
    }    

	@Override
	public void update(int args) {
		if(Graph.GRAPH_CHANGED == args)
			postInvalidate();
		
	}
}