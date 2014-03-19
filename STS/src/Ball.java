/* A class that implements a soccer ball. 

   Author: Spiros Mancoridis (c) 2011
 */

public class Ball {
	private Point2D position; 		/* current ball position */


	/* Get the ball coordinates on a 2D pitch. 
	 */
	public Point2D getCoordinates () {
		return position;
	}


	/* Set the ball coordinates on a 2D pitch. 
	 */
	public void setCoordinates (Point2D newPosition) {
		assert newPosition != null : "Null position passed to setCoordinates().";
		 
		position = newPosition;
	}


	/* Construct and initialize a ball. 
 	 */
	public Ball (Point2D newPosition) {
		setCoordinates(newPosition);
	}
} /* Ball */
