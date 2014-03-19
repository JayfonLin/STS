/* A class that implements a 2D soccer pitch (field). Each position on the pitch is a reference
   to an Object, which can be casted to a Player reference or can be null, if the pitch point 
   is not occupied by a player. A pitch can be used for a soccer game or by auxiliary validation 
   code to print player movement, team strategy etc. The pitch's Cartesian coordinate system	
   with	(0,0) as the bottom-left of the pitch and (maxWidth-1, maxLength-1) as the top-right
   f the rectangular pitch.	

   Author: Spiros Mancoridis (c) 2011
 */

public class Pitch {
	private int pitchLength;			/* The length of the pitch. 		*/
	private int pitchWidth;				/* The width of the pitch. 			*/
	private Object [][] pitch;			/* 2D pitch of Objects .			*/
	private Point2D [] goal1;			/* Goal 1 pitch coordinates.		*/
	private Point2D [] goal2;			/* Goal 2 pitch coordinates.		*/


	/* Return an array of 2 Points that define the coordinates of goal1.
	 */
	public Point2D [] getGoal1Coordinates() {
		return goal1;
	}


	/* Return an array of 2 Points that define the coordinates of goal2.
	 */
	public Point2D [] getGoal2Coordinates() {
		return goal2;
	}

  
	/* Check if a point on the pitch is available (i.e., is null) so that it may
	   become occupied by a player. 
	 */
	public boolean isNull(Point2D position) {
		assert position != null :"Null point passed to isNull().";

		int x = position.getX();
		int y = position.getY();

		return (pitch[x][y] == null);
	}


	/* Return an Object that occupies a specific position on the pitch.
	 */
	public Object getObject(Point2D position) {
		assert position != null : "Null point passed to getObject().";

		int x = position.getX();
		int y = position.getY();

		assert ((0 <= x) && (x < pitchWidth) && (0 <= y) && (y < pitchLength)) : "Out of pitch bounds in getObject().";

		return pitch[x][y];
	}


	/* Set a point on the pitch to an Object (e.g., a pointer to a player).
	 */
	public void setCoordinates(Point2D position, Object o) {
		assert position != null : "Null point passed to setCoordinates().";

		int x = position.getX();
		int y = position.getY();

		pitch[x][y] = o;	
	}


	/* Get the length of the pitch.
	 */
	public int getPitchLength() {
		return pitchLength;
	}


	/* Get the width of the pitch.
	 */
	public int getPitchWidth() {
		return pitchWidth;
	}


	/* If the ball is Goal 1, return 1, if it is in Goal 2, return 2,  
       otherwise return 0.                                            
	 */
	public int inGoal(Ball ball) {
		assert ball != null : "Null ball passed to inGoal().";

		int ballX = ball.getCoordinates().getX();	
		int ballY = ball.getCoordinates().getY();	

		int whichGoal = 0;

		int lowPost  = goal1[0].getX();	
		int highPost = goal1[1].getX();	

		if (lowPost < ballX && ballX < highPost) {
			if (ballY == goal1[0].getY()) {
				whichGoal = 1;
			} else if (ballY == goal2[0].getY()) {
				whichGoal = 2; 
			}
	 	}
		
		return whichGoal;
	}


	/* Construct and initialize a pitch.
	 */
	public Pitch(int width, int length) {
		assert width  >= 0 : "Negative pitch width passed to Pitch().";
		assert length >= 0 : "Negative length width passed to Pitch().";

		pitchLength = length;
		pitchWidth = width;
		int midWidth = pitchWidth/2;
		int halfGoalWidth = pitchWidth/16;

		/* set up the goal posts */
		int lowPost = midWidth - halfGoalWidth;
		int highPost = midWidth + halfGoalWidth; 

		goal1 = new Point2D[2];
		goal1[0] = new Point2D(lowPost, 0);
		goal1[1] = new Point2D(highPost, 0);

		goal2 = new Point2D[2];
		goal2[0] = new Point2D(lowPost, pitchLength-1);
		goal2[1] = new Point2D(highPost, pitchLength-1);

		pitch = new Object [pitchWidth][pitchLength];

		for (int i=0; i < pitchWidth; i++) 
			for (int j=0; j < pitchLength; j++)
				pitch[i][j] = null;
	}

} /* Pitch */

