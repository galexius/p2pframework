package de.bachelor.graphgame;

public class GraphCalculator {

	private static final double EPSILON = 0.00001d;
	
	public static double abs(double value){
		if(value < 0) return value* (-1);
		return value;
	}
	
	public static double min(double value1,double value2){
		if(value1 <= value2) return value1;
		return value2;
	}
	
	public static double max(double value1,double value2){
		if(value1 >= value2) return value1;
		return value2;
	}

	
	public static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
	      // Return false if either of the lines have zero length	
		
	      if (abs(x1-x2) < EPSILON && abs(y1 - y2) < EPSILON ||
	            abs(x3-x4) < EPSILON && abs(y3-y4) < EPSILON){
	         return false;
	      }
	      
	      //check for bounding boxes intersection
	      double leftSeg1 = min(x1, x2);
	      double rightSeg1 = max(x1, x2);
	      double upSeg1 = max(x1, x2);
	      double downSeg1 = min(x1, x2);
	      
	      double leftSeg2 = min(x3, x4);
	      double rightSeg2 = max(x3, x4);
	      double upSeg2 = max(x3, x4);
	      double downSeg2 = min(x3, x4);
	      
	      if(leftSeg1 < leftSeg2){
	    	 if(rightSeg1 < leftSeg2) return false;
	      }else if(rightSeg2 < leftSeg1) return false;
	      
	      if(upSeg1 > upSeg2){
	    	  if(downSeg1 > upSeg2) return false;
	      }else if(downSeg2 > upSeg1) return false;
	      
	      double vectorAx = x2-x1;
	      double vectorAy = y2-y1;
	      double vectorBx = x4-x3;
	      double vectorBy = y4-y3;
	      
	      double vector31x = x1-x3;
	      double vector31y = y1-y3;
	      double vector32x = x2-x3;
	      double vector32y = y2-y3;
	      
	      double vector13x = x3-x1;
	      double vector13y = y3-y1;
	      double vector14x = x4-x1;
	      double vector14y = y4-y1;
	      
	      
	      double crossProductBothSeg = vectorAy*vectorBx - vectorAx*vectorBy;
	      double crossProduct1WithB = vectorBy*vector31x - vectorBx*vector31y;
	      double crossProduct2WithB = vectorBy*vector32x - vectorBx*vector32y;
	      double crossProduct1WithA = vectorAy*vector13x - vectorAx*vector13y;
	      double crossProduct2WithA = vectorAy*vector14x - vectorAx*vector14y;
	      
	      
	      //are segments parallel?
	      if (crossProductBothSeg > EPSILON || crossProductBothSeg < (0-EPSILON)){	    	  
	    	//if the two crossProducts have different sings, the line segments straddle, means they intersect
	    	  boolean aStruddleB = false;
	    	  if(crossProduct1WithB < 0 && crossProduct2WithB < 0) return false;
    	      if(crossProduct1WithB > 0 && crossProduct2WithB > 0) return false;
    	      if(abs(crossProduct1WithB) < EPSILON || abs(crossProduct2WithB) < EPSILON) return false;
    	      aStruddleB = true;
    	      
    	      if(crossProduct1WithA < 0 && crossProduct2WithA < 0) return false;
  	      if(crossProduct1WithA > 0 && crossProduct2WithA > 0) return false;
  	      if(abs(crossProduct1WithA) < EPSILON || abs(crossProduct2WithA) < EPSILON) return false;
  	      if(aStruddleB) return true; //A struddles B, B struddle A, means they intersect
	      }
	      
       // The segments are parallel.
       // Check if they're collinear.
       double collinearity = x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2);   // see http://mathworld.wolfram.com/Collinear.html
       
       if ( collinearity < EPSILON && collinearity > (0-EPSILON)){
          // The lines are collinear. Now check if they overlap.
          if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 ||
                x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 ||
                x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2){
             if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 ||
                   y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 ||
                   y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2){
                return true;
             }
          }
       }
       return false;
	   }
}
