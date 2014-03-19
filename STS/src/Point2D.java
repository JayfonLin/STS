/* A class that implements a point in 2D Euclidean space.

   Author: Spiros Mancoridis (c) 2011
 */

public class Point2D {
	private int x;		/* x coordinate */
	private int y;		/* y coordinate */
	

	/* Return the x coordinate value of the 2D point. 
	 */
	public int getX () {
		return x;
	}


	/* Set the x coordinate value of the 2D point.
	 */
	public void setX (int newX) {
		x = newX;
	}


	/* Return the y coordinate value of the 2D point. 
	 */
	public int getY () {
		return y;
	}


	/* Set the y coordinate value of the 2D point.
	 */
	public void setY (int newY) {
		y = newY;
	}


	/* Construct a 2D point (x,y).
	 */
	public Point2D (int newx, int newy) {
		x = newx;
		y = newy;
	}

} /* Point2D */ 

