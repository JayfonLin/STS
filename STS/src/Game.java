/* A class that implements a soccer game.

   Author: Spiros Mancoridis (c) 2011
 */

import java.lang.Math;
import java.util.Random;

class Game {
	private Random r;		  			/* Random number generator. 		             	*/
	private final int duration = 5400; 	/* Each game has 'duration' number of plays.      	*/
	private Team team1, team2;	  		/* Each game has 2 teams. 			     			*/
	private Ball ball;		  			/* Each game has 1 ball. 			     			*/	
	private Pitch pitch;		 		/* Each game is played on a single 2D pitch.       	*/
	private int shortHorizontalKick;  	/* Length of a short horizontal (X axis) kick.    	*/
	private int shortVerticalKick;    	/* Length of a short vertical (Y axis) kick.       	*/
	private int longHorizontalKick;   	/* Length of a long  horizontal (X axis) kick.    	*/
	private int longVerticalKick;	  	/* Length of a long vertical (X axis) kick.        	*/
	private int maxX;		  			/* Maximum X pitch coordinate.                     	*/
	private int maxY;		  			/* Maximum Y pitch coordinate.		     			*/
	private int minX;		  			/* Minimum X pitch coordinate.                     	*/
	private int minY;		  			/* Minimum Y pitch coordinate.                     	*/
	private int team1Score; 	  		/* Score of first team.			     				*/
	private int team2Score;	  	  		/* Score of second team.			     			*/


	/* Returns the home team's (team1) name. Used for printing purposes.
	 */
	public String getTeam1Name () {
		assert team1 != null : "Null team passed to getTeam1Name().";
		
		return team1.getName();
	}


	/* Returns the away team's (team2) name. Used for printing purposes.
	 */
	public String getTeam2Name () {
		assert team2 != null : "Null team passed to getTeam2Name().";

		return team2.getName();
	}


	/* Returns the home team's (team1) score.
	 */
	public int getTeam1Score () {
		return team1Score;
	}


	/* Returns the away team's (team2) score.
	 */
	public int getTeam2Score () {
		return team2Score;
	}


	/* Compute the distance between 2 points using the Pythagorean theorem. 
	 */
	private double getDistance (Point2D pnt1, Point2D pnt2) {
		assert (pnt1 != null && pnt2 != null) : "Null point passed to getDistance().";

		double x1 = pnt1.getX();
		double y1 = pnt1.getY();
		
		double x2 = pnt2.getX();
		double y2 = pnt2.getY();

		return Math.sqrt( Math.pow((x2-x1), 2.0) + Math.pow((y2-y1), 2.0) );
	}


	/* Return the player who is nearest to the ball. 
	*/
	/*@ requires team != null
	   @ ensures \result != null
	   @*/
	public Player getNearestPlayerToBall (Team team) {
		assert (team != null);
		Player nearest = null;
		double distance = pitch.getPitchLength() *  pitch.getPitchWidth();

		int tsize = team.getSize();

		for (int i=0; i < tsize; i++) {
			Player player = team.getPlayer(i);
			Point2D playerPosition = player.getCoordinates();
			Point2D ballPosition = ball.getCoordinates();
			double currDistance = getDistance(ballPosition, playerPosition);
			assert currDistance >= 0;
			if (currDistance < distance) {
				distance = currDistance;
				nearest = player;
			}
		}
		assert (nearest != null);
		return nearest;
	}


	/* Move a player to a new position in his roaming coordinates. 
	 */
	private Point2D findNewPlayerPosition (Player player) {
		assert player != null : "Null player passed to findNewPlayerPosition().";

		Point2D [] roaming = player.getRoamingCoordinates();
		
		int x1 = roaming[0].getX();
		int y1 = roaming[0].getY();
		
		int x4 = roaming[3].getX();
		int y4 = roaming[3].getY();

		int newx = x1 + r.nextInt(x4-x1+1);
		int newy = y1 + r.nextInt(y4-y1+1);
		Point2D newPosition = new Point2D(newx, newy);

		assert (x1 <= newx && newx <= x4 && y1 <= newy && newy <= y4) : "New position is out of roaming range.";

		return newPosition;
	}


	/* Move the player only if the new (proposed) pitch position is available (i.e, is null).  
	   Otherwise keep the player in his current position.
	 */
	private void tryToMovePlayer (Point2D currentPosition, Point2D newPosition, Player player) {
		assert (currentPosition != null && newPosition != null) : "Null point passed to tryMovePlayer().";
		assert player != null : "Null player passed to tryMovePlayer().";
		
		if(pitch.isNull(newPosition)) {
			pitch.setCoordinates(currentPosition, null);
			pitch.setCoordinates(newPosition, player);
			player.setCoordinates(newPosition);
		}
	}


	/* Try to move each player on the team to a new position in their roaming 
	   area. A player stays put if there is another player already in his    
       proposed new position.						  
	*/
	private void movePlayers (Team team) {
		assert team != null : "Null team passed to movePlayers().";

		int tsize = team.getSize();

		for (int i=0; i < tsize; i++) {
			Player player = team.getPlayer(i);
			Point2D currentPosition = player.getCoordinates();
			Point2D newPosition = findNewPlayerPosition(player); 

			/* Try to find a new position that does not conflict
			   with the position of other players.
			 */
			final int maxTries = tsize; 
			int attempt = 0;
			
			while(!pitch.isNull(newPosition) && ++attempt < maxTries)
				newPosition = findNewPlayerPosition(player); 
	
			tryToMovePlayer(currentPosition, newPosition, player);
		}
	}

	
	/* Set the new ball coordinates to the (x,y) position only if that position is 
	   within the pitch bounds. If (x,y) is out of bounds, reposition the ball to 
	   be in bounds. 
	 */
	private void setNewBallCoordinates (int x, int y) {
		int newx = x;
		int newy = y;

		if (newx < minX) newx = minX;
		if (newx > maxX) newx = maxX;	
		if (newy < minY) newy = minY;
		if (newy > maxY) newy = maxY;	

		Point2D newBallPosition = new Point2D(newx, newy);		
		ball.setCoordinates(newBallPosition); 
	}


 	/* Kick the ball using a goal keeper to a new position on the pitch. The choice 
	   of kick is always forward and may be short or long, narrow or wide, depending
	   on the x and y values returned by the random number generator.
	 */
    private void goalkeeperKick (int direction) {
    	assert (direction == 1 || direction == -1) : "Invalid direction.";

        Point2D  currentBallPosition = ball.getCoordinates();
        int x = currentBallPosition.getX();
        int y = currentBallPosition.getY();
        int newx = x + direction * r.nextInt(longHorizontalKick);
        int newy = y + direction * r.nextInt(longVerticalKick);

        setNewBallCoordinates (newx, newy);
	}


	/* Ball is kicked to a new position on the pitch by a player who is not a 
	   goal keeper. The choice of kick (short, long, forward, backward, left, right) 
	   depends on a specified distribution.					       
	*/
	private void kick (int direction) {
		assert (direction == 1 || direction == -1) : "Invalid direction.";
		
		Point2D  currentBallPosition = ball.getCoordinates();
		int x = currentBallPosition.getX();
		int y = currentBallPosition.getY();
		int newx = x;
		int newy = y;

		int kickType  = r.nextInt(100);
		assert (0 <= kickType && kickType < 100) : "Invalid probability " + kickType;

		if (0 <= kickType && kickType < 5) { 						/* long back pass, 5%*/
			newy = y - direction * r.nextInt(longVerticalKick);
		} else if (5 <= kickType && kickType < 10) {  				/* long left pass, 5% */
			newx = x - direction * r.nextInt(longHorizontalKick);
		} else if (10 <= kickType && kickType < 30) {				/* short left pass, 20% */
			newx = x - direction * r.nextInt(shortHorizontalKick);
		} else if (30 <= kickType && kickType < 50) {				/* short forward pass, 20% */
			newy = y + direction * r.nextInt(shortVerticalKick);
		} else if (50 <= kickType && kickType < 65) {				/* long forward pass, 15% */
			newy = y + direction * r.nextInt(longVerticalKick);
		} else if (65 <= kickType && kickType < 85) {				/* short right pass, 20% */
			newx = x + direction * r.nextInt(shortHorizontalKick);
		} else if (85 <= kickType && kickType < 90) {				/* long right cross pass, 5% */
			newx = x + direction * r.nextInt(longHorizontalKick);
		} else if (90 <= kickType && kickType < 100) {				/* short back pass, 10% */
			newy = y - direction * r.nextInt(shortVerticalKick);
		}

		setNewBallCoordinates (newx, newy);
	}


	/* The ball is spotted at the center of the pitch to start the game or
	   re-start the game after a goal is scored.     
	*/
	private void kickOff () {
		int x = pitch.getPitchWidth()/2;
		int y = pitch.getPitchLength()/2;
		Point2D center = new Point2D(x,y);
		
		ball.setCoordinates(center);
		movePlayers(team1);
		movePlayers(team2);
	}


	/* This is the main algorithm of the soccer match game. For each tick of the 
	   simulation, the ball goes to one of the closest players of each team. Then the player 
	   kicks the ball. If a goal is scored, the score is adjusted, and the players reset for
	   kick off. Otherwise, the players from the two teams move around their roaming
	   regions and play resumes.
	 */
	private void play () {
		kickOff();

		for(int i=0; i < duration; i++) {
			Player p1 = getNearestPlayerToBall(team1);
			Player p2 = getNearestPlayerToBall(team2);

			Player nearestPlayer;
			Point2D ballXY = ball.getCoordinates();
			
			if(getDistance(p1.getCoordinates(), ballXY) < getDistance(p2.getCoordinates(), ballXY)) {
				/* Player 1 is closer to the ball */
				nearestPlayer = p1;
			} else if (getDistance(p1.getCoordinates(), ballXY) > getDistance(p2.getCoordinates(), ballXY)) {
				/* Player 2 is closer to the ball */
				nearestPlayer = p2;
			} else {
				/* flip a coin to determine who wins the ball because players are equidistant to the ball.
				 */
				int whoGetsIt = r.nextInt(2);
				if (whoGetsIt == 0) 
					nearestPlayer = p1;
				else nearestPlayer = p2; 
			}
			
			/* Increment a variable to keep track of number of times a player 
			   touches the ball.
			 */
			nearestPlayer.incrementTouches();

			/* Player in possession keeps the ball before kicking it. 
			 */
			ball.setCoordinates(nearestPlayer.getCoordinates()); 

			int direction = 1;
			if (nearestPlayer.getTeam() == team2) 
				direction = -1;

			if (nearestPlayer.isGoalkeeper()){
				goalkeeperKick(direction);
			} else {
				kick(direction);
			}

			int goal = pitch.inGoal(ball);

			/* Has a goal been scored? 
			 */
			switch (goal) { 
				case 1: /* ball is in team1's goal */
					if (nearestPlayer.getTeam() == team1) 
						nearestPlayer.incrementOwnGoals();
					else
						nearestPlayer.incrementGoals();
					team2Score++;
					kickOff();
					break;
				case 2: /* ball is in team2's goal */
					if (nearestPlayer.getTeam() == team2) 
						nearestPlayer.incrementOwnGoals();
					else
						nearestPlayer.incrementGoals();
					team1Score++;
					kickOff();
					break;
				default: 
					/* Move the players within their roaming area. */
					movePlayers(team1);
					movePlayers(team2);
					break;
			}
		} 	
	}


	/* Construct and initialize a game.
	 */
	public Game (Team t1, Team t2, Ball b, Pitch p) {
		assert t1 != null && t2 != null : "Null team passed to Game().";
		assert b != null : "Null ball passed to Game().";
		assert p != null : "Null pitch passed to Game().";

		team1 = t1;
		team2 = t2;
		pitch = p;
		ball = b;
		r = new Random();

		/* Team 1 and Team 2 are assigned goals and roaming coordinates.
		   The roaming coordinates have to be set after the goals are 
		   determined, as it affects the position of the players.
		 */

		/* Team1 is the home team, so set it's goal to 1 and initialize its
		   player's roaming coordinates.
		 */
		team1.setGoal(1);
		team1.setPlayersRoamingCoordinates();

		/* Team2 is the away team, so set it's goal to 2 and initialize its
		   player's roaming coordinates.
		 */
		team2.setGoal(2);
		team2.setPlayersRoamingCoordinates();

		/* Initialize class variables to define what short and long 
		   vertical and horizontal kicks are in terms of the pitch size.   
		 */
		shortHorizontalKick = pitch.getPitchWidth()/10;
		longHorizontalKick = pitch.getPitchWidth()/5;
		shortVerticalKick = pitch.getPitchLength()/20;
		longVerticalKick = pitch.getPitchLength()/10;

		maxY = pitch.getPitchLength() - 1;
		maxX = pitch.getPitchWidth() - 1;
		minX = 0;
		minX = 0;

		/* set the initial score to 0-0 
		 */
		team1Score = 0;
		team2Score = 0;

		/* Position the players of each team on the pitch, based on 
		   their roaming area. Then start the game.				    
		 */
		kickOff();
		play();
	}

} /* Game */
