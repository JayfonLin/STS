/* A class that implements a soccer player.

   Author: Spiros Mancoridis (c) 20011
 */

import java.lang.Math;
import java.util.Random;
import java.util.LinkedList;

public class Player {
	private Team team;		   					/* Reference to the player's team.		   			*/	
	private int id = 0; 		   				/* Identifier (number) of the player. 
					      				   		   Goal keepers always, and exclusively, have 
					      				   		   id = 1.   				   						*/
	private Point2D position;	   				/* Current position of the player on the pitch. 	*/
	private int scoredOwnGoals;	   				/* Number of own goals scored by the player.		*/ 
	private int scoredGoals;	   					/* Number of goals scored by the player.	   	*/
	private int touches;		   					/* Number of touches player has had on the ball.*/
	private LinkedList<Point2D> ballPositions;  	/* List of pitch positions where player has     */
					   							/* possession of the ball.			   				*/
	private int pitchWidth; 	   					/* Width of the pitch where the player exists.  */
	private int pitchLength;	   					/* Length of the pitch where the player exists. */

	private Point2D [] homeRoaming;	   			/* Home roaming rectangle:  			     
					          						homeRoaming[0] = (x1,y1), bottom-left  	     
					          						homeRoaming[1] = (x2,y2), bottom-right  	     
					          						homeRoaming[2] = (x3,y3), top-left      
					          						homeRoaming[3] = (x4,y4), top-right   			*/

	private Point2D [] awayRoaming;	   			/* Away roaming rectangle:  			     
					          						awayRoaming[0] = (x1,y1), bottom-left  	     
					          						awayRoaming[1] = (x2,y2), bottom-right  	     
					          						awayRoaming[2] = (x3,y3), top-left      
					          						awayRoaming[3] = (x4,y4), top-right   			*/

	private Point2D [] roaming;	   				/* Set roaming variable to either homeRoaming     
					      			       		   or awayRoaming depending on whether the team   
					      			       		   is playing on the home or the away side of     
					      			       		   the pitch.       				   				*/


	/* Set the roaming coordinates of the player to the pre-calculated home roaming 
	  coordinates.
	 */
	public void setRoamingToHome () {
		roaming = homeRoaming;
	}


	/* Set the roaming coordinates of the player to the pre-calculated away roaming 
	  coordinates.
	 */
	public void setRoamingToAway () {
		roaming = awayRoaming;
	}


	/* Return a list of ball position, which is a LinkedList of 2D points on the 
	   pitch where the player has had possession of the ball.
	*/
	public LinkedList<Point2D> getBallPositions () {
		return ballPositions;
	} 

	
	/* Return a 4-element array of 2D points that defines the rectangle in which a 
	   a player may roam during the game.
	 */
	public Point2D[] getRoamingCoordinates () {
		return roaming;
	}


	/* Return the current position of the player on the pitch.
	 */
	public Point2D getCoordinates () {
		return position;
	}


	/* Move the player to a new point on the pitch. 
	 */
	public void setCoordinates (Point2D newPosition) {
		assert newPosition != null : "Null position passed to setCoordinates().";

		int x = newPosition.getX();
		int y = newPosition.getY();

		assert (0 <= x && x < pitchWidth && 0 <= y && y < pitchLength) :
			"Cannot move player out of pitch bounds.";

		position = newPosition;
	}



	/* Set the team of a player. 
	 */
	public void setTeam (Team newTeam) {
		assert (newTeam != null) : "Cannot assign a null team to a player.";

		team = newTeam;
	}


	/* Return the identifier of the player. Goal keepers always and exclusively have id = 1. 
	*/
	public int getId () {
		return id;
	}


	/* Return the team name of the player.
	 */ 
	public Team getTeam () {
		return team;
	}


	/* Set a roaming region for a player, but perform a series of sanity 
	   checks before committing to the calculated roaming region. 
	 */
	private Point2D [] initRoamingArray (int x1, int y1, int x4, int y4) {
        /* The bottom-right (x2,y2) and top-left (x3,y3) points are 
           stored as well for efficiency purposes.                  
		 */
        int x2 = x4;
        int y2 = y1;
        int x3 = x1;
        int y3 = y4;

        /* The computed x1, x2, x3, x4 values are between 0 and 
           the width of the pitch.
		 */
        assert 	0 <= x1 && x1 < pitchWidth &&
                0 <= x2 && x2 < pitchWidth &&
                0 <= x3 && x3 < pitchWidth &&
                0 <= x4 && x4 < pitchWidth : "Bad x1, x2, x3, x4 roaming coordinates: " + x1 + ", " + x2 + ", " + x3 + ", " + x4;

        /* The computed y1, y2, y3, y4 values are between 0 and  
           the length of the pitch.
		 */
        assert 	0 <= y1 && y1 < pitchLength &&
               	0 <= y2 && y2 < pitchLength &&
               	0 <= y3 && y3 < pitchLength &&
               	0 <= y4 && y4 < pitchLength : "Bad y1, y2, y3, y4 roaming coordinates: " + y1 + ", " + y2 + ", " + y3 + ", " + y4;

        /* Roaming coordinates must form a rectangle. 
		 */
        assert  x1 == x3 && x2 == x4 && y1 == y2 && y3 == y4 &&
               	Math.abs(x2-x1) == Math.abs(x4-x3) &&
               	Math.abs(y3-y1) == Math.abs(y4-y2) :
                		"Roaming coordinates do not form a rectangle.";

        /* Each player's roaming region is stored in a 4-element array of 2D points.                                   
		 */
         Point2D [] newRoaming = new Point2D[4];
         newRoaming[0] = new Point2D(x1, y1);
         newRoaming[1] = new Point2D(x2, y2);
         newRoaming[2] = new Point2D(x3, y3);
         newRoaming[3] = new Point2D(x4, y4);

         return newRoaming;
	}


	/* Set the away roaming coordinates using the home roaming coordinates that
	   are passed as parameters. The away coordinates are first calculated as the
	   mirror image of the passed points. Then, (x1,y1) and (x4,y4) are swapped
	   to make all roaming rectangles consistent in the absolute pitch coordinates
	   (i.e, (x1, y1) is the top-left and (x4, y4) is the bottom-right in absolute
	   coordinates). The area of the away roaming region is returned by the function.
	 */
	int setAwayRoamingArea (int homeX1, int homeY1, int homeX4, int homeY4) {
		int x1 = (pitchWidth-1)  - homeX1;
        int y1 = (pitchLength-1) - homeY1;
        int x4 = (pitchWidth-1)  - homeX4;
        int y4 = (pitchLength-1) - homeY4;

        awayRoaming = initRoamingArray(x4, y4, x1, y1);
        return Math.abs(x4-x1) * Math.abs(y1-y4);
	}


	/* Compute and set the roaming region for a goal keeper. This method is called
	   before every game, as the position of the goal keeper varies depending on
	   whether a team is a home team or an away team.
	 */
    public void setGoalkeeperRoaming (Point2D [] customRoamingCoordinates) {
    	int x1, y1, x4, y4;

		if (customRoamingCoordinates == null) {
			/* Goal keepers always have a roaming region of the penalty box. 
		 	*/
			x1 = pitchWidth / 6;	
			x4 = pitchWidth - x1;
			y1 = 0; 
			y4 = y1 + (pitchLength / 6);
		} else {
			x1 = customRoamingCoordinates[0].getX();
			y1 = customRoamingCoordinates[0].getY();
			x4 = customRoamingCoordinates[1].getX();
			y4 = customRoamingCoordinates[1].getY();
		}

        /* Area of roaming rectangle is equal to the penalty box. 
		 */
		int homeRoamingArea = Math.abs(x4-x1) * Math.abs(y1-y4);

		/* Set the home roaming coordinates.
		 */
		homeRoaming = initRoamingArray(x1, y1, x4, y4);

		/* Set the away roaming coordinates.
		 */
		int awayRoamingArea = setAwayRoamingArea(x1, y1, x4, y4); 

		/* Make sure that the home and away roaming rectangles have an equal area.
		   Also, that their areas are equal to the pitch penalty box area.
		 */
        assert (awayRoamingArea == homeRoamingArea) : "Roaming rectangles for goalkeepers are not equal.";

        assert (homeRoamingArea == (pitchWidth*4/6) * (pitchLength/6)) : 
        	"Roaming rectangles for goalkeepers are not equal to penalty box.";
	}


    /* Create a roaming area for the player by establishing the player's 	   
       roaming coordinates. The method attempts to create a roaming region   
       for each player that is less than or equal to the maximum roaming region. 
	   In some cases it settles for a smaller roaming region, however. After
	   calculating the home roaming region, the away roaming region is calculated
	   as the mirror image of the home roaming region. 
	 */
    public void setPlayerRoaming (Point2D [] customRoamingCoordinates) {
    	Random r = new Random();

		int teamSize = team.getSize();
        int maxPlayerArea = (pitchWidth * pitchLength) / teamSize;
        int minPlayerWidth = 1;
        int maxPlayerWidth = pitchWidth - 1;
		int x1, y1, x4, y4;

		if (customRoamingCoordinates == null) {
			/* set up the roaming region for the player. 
		 	 */
            int playerWidth = r.nextInt(maxPlayerWidth-minPlayerWidth) + 1;
            int playerLength = maxPlayerArea / playerWidth;

            if (playerLength > pitchLength)
            	playerLength = pitchLength-1;

            /* Get random starting point (bottom-left corner) of a player's 
               rectangular roaming region.                               
             */
            x1 = r.nextInt(pitchWidth-playerWidth);
            y1 = r.nextInt(pitchLength-playerLength);

            /* Calculate the ending point (top-right) of a player's 
               rectangular roaming region.                             
             */
            x4 = x1 + playerWidth;
            y4 = y1 + playerLength;
		} else {
			x1 = customRoamingCoordinates[0].getX();
			y1 = customRoamingCoordinates[0].getY();
			x4 = customRoamingCoordinates[1].getX();
			y4 = customRoamingCoordinates[1].getY();
		}

        /* Area of roaming rectangle is no larger than maxPlayerArea. 
		 */
		int homeRoamingArea = Math.abs(x4-x1) * Math.abs(y1-y4);

		homeRoaming = initRoamingArray(x1, y1, x4, y4);

		/* Next, create the away roaming coordinates. 
		 */
		int awayRoamingArea = setAwayRoamingArea(x1, y1, x4, y4);

		/* Make sure that the home and away roaming rectangles have an equal area.
		   Also, that their areas are not larger than the maximum player roaming area.
		 */
        assert homeRoamingArea == awayRoamingArea : "Player home and away roaming areas are unequal.";
        assert homeRoamingArea <= maxPlayerArea : "Player roaming area too large.";
    } /* setPlayerRoaming */


	/* Increment the number of own goals scored by the player.
	 */
	public void incrementOwnGoals () {
		scoredOwnGoals++;
    }


	/* Increment the number of goals scored by the player.
	 */
	public void incrementGoals () {
		scoredGoals++;
    }

	
	/* Increment the number of touches a player has had on the ball.
	 */
	public void incrementTouches () {
		touches++;
		ballPositions.add(position);
    }


	/* Return the number of goals scored by the player.
	 */
	public int getOwnGoals () {
		return scoredOwnGoals;
    }


	/* Return the number of goals scored by the player.
	 */
	public int getGoals () {
		return scoredGoals;
    }


	/* Return the number of touches the player has had on the ball.
	 */
	public int getTouches () {
		return touches;
    }


	/* Return true of the player is a goal keeper, otherwise return false.
	 */
	public boolean isGoalkeeper () {
		return id == 1;
	}


	/* Return a 2D point, which represents a pitch coordinate where the player had
	   his ith possession of the ball.
	 */
	public Point2D getBallPossessionPosition (int i) {
		assert (0 <= i && i <= getTouches()) : "Invalid ball possession.";

		return (Point2D) ballPositions.get(i);
	}


	/* Construct and initialize a soccer player.
	 */
	public Player (Team t, int newId, int newPitchWidth, int newPitchLength, Point2D [] customRoamingCoordinates) {
		setTeam(t);
		id = newId;
		pitchWidth = newPitchWidth;
		pitchLength = newPitchLength;

		Point2D position = new Point2D(0, 0);
		setCoordinates(position);

		scoredOwnGoals = 0;
		scoredGoals = 0;
		touches = 0;

		ballPositions = new LinkedList<Point2D>();
		
		/* Both home and away roaming coordinates are calculated at player 
		   construction time. One of the two coordinates will be selected 
		   prior to game, time depending on whether the team is playing at 
		   home or away.
		 */
		if (id == 1)
			setGoalkeeperRoaming(customRoamingCoordinates);
		else
			setPlayerRoaming(customRoamingCoordinates);
	}
} /* Player */

