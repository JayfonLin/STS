/* A class that has some useful debugging methods. You should add your own methods too. 

   Author: Spiros Mancoridis (c) 2011
 */

public class Printer {
	private int pitchWidth;
	private int pitchLength;

	/* Print the pitch along with  the players.
	 */
   	public void pitchWithPlayers (Ball ball, Pitch pitch) {
       	int ballX = ball.getCoordinates().getX();
        int ballY = ball.getCoordinates().getY();
		int pitchLength = pitch.getPitchLength();
		int pitchWidth = pitch.getPitchWidth();

        for (int i=0; i < pitchLength; i++) {
        	for (int j=0; j < pitchWidth; j++) {
        		Player p = (Player) pitch.getObject(new Point2D(j,i));

                if (j == ballX && i == ballY)
                	System.out.print("*");
                else if (p == null) {
                	System.out.print("-");
                } else {
                	char playerId = p.getTeam().getId();
                    System.out.print(playerId);
                }
        	} /* inner for */
            System.out.println();
        } /* outer for */
        System.out.println("\n");
   	} /* pitchWithPlayers */


   	/* Print the pitch along with a single player's roaming region.
   	 */
   	public void pitchWithSinglePlayerRoamingRegion (Player player) {
   		Pitch pitch = new Pitch(pitchWidth, pitchLength);
        Point2D [] roaming = player.getRoamingCoordinates();
        int x1 = roaming[0].getX();
        int y1 = roaming[0].getY();
        int x4 = roaming[3].getX();
        int y4 = roaming[3].getY();
        
        System.out.println("Roaming region area for play = " + (x4-x1)*(y4-y1));

        for (int x=x1; x <= x4; x++) {
        	for (int y=y1; y <= y4; y++) {
				Point2D xy = new Point2D(x,y);
                pitch.setCoordinates(xy, player);
        	}
        }

		Ball dummyBall = new Ball(new Point2D(-1,-1));
		pitchWithPlayers(dummyBall, pitch);
   	} /* pitchWithSinglePlayerRoamingRegion */


    /* Print the pitch to indicate the team's total roaming region,
       total covered area, and total uncovered area.                      
     */
    public void pitchWithRoamingRegions (Team team) {
		Pitch pitch = new Pitch(pitchWidth, pitchLength);
		int numPlayers = team.getSize();

        for (int i=0; i < numPlayers; i++) {
			Player p = team.getPlayer(i);
            Point2D [] roaming = p.getRoamingCoordinates();
            int x1 = roaming[0].getX();
            int y1 = roaming[0].getY();
            int x4 = roaming[3].getX();
            int y4 = roaming[3].getY();

            for (int x=x1; x <= x4; x++)
            	for (int y=y1; y <= y4; y++) {
					Point2D xy = new Point2D(x,y);
                    pitch.setCoordinates(xy, p);
				}
        }

		Ball dummyBall = new Ball(new Point2D(-1,-1));
		pitchWithPlayers(dummyBall, pitch);

        int teamArea = 0;
		for (int i=0; i < pitchWidth; i++) {
			for (int j=0; j < pitchLength; j++) {
				if (pitch.getObject(new Point2D(i,j)) != null) 
					teamArea++;
			}
		}

        System.out.println("Total team covered area = " + teamArea);
        System.out.println("Total team uncovered area = " + ((pitchWidth * pitchLength) - teamArea));
        System.out.println();
    } /* pitchWithRoamingRegions */


    public void pitchWithPlayerBallTouches (Player player) {
    	int numPoints = player.getBallPositions().size();
		Pitch pitch = new Pitch(pitchWidth, pitchLength);

        for (int i=0; i < numPoints; i++) {
        	Point2D point = (Point2D) player.getBallPossessionPosition(i);
            if (point != null) 
				pitch.setCoordinates(point, player);
        }
		Ball dummyBall = new Ball(new Point2D(-1,-1));
		pitchWithPlayers(dummyBall, pitch);
    } /* pitchWithPlayerBallTouches */


	public void teamStatistics(Team team) {
		int totalTeamTouches = 0;

        System.out.println(team.getName() + " player statistics:");
		int teamSize = team.getSize();

        for (int i=0; i < teamSize; i++) {
        	Player p = team.getPlayer(i);

            System.out.println("    Player " + p.getId() +
                                " Touches: " + p.getTouches() +
                                ", Goals: " + p.getGoals() +
                                ", Own Goals: " + p.getOwnGoals()
            );

           totalTeamTouches += p.getTouches();
           //pitchWithPlayerBallTouches(p);
        }

        System.out.println("Total team touches: " + totalTeamTouches);
        System.out.println();
    } /* teamStatistics */


    /* Output the score board to standard output.
     */
    public void printScoreBoard(Game g) {
        System.out.println(g.getTeam1Name() + " : " + g.getTeam1Score());
        System.out.println(g.getTeam2Name() + " : " + g.getTeam2Score());
    } /* printScoreBoard */


    /* Construct a pitch of a specified size.
     */
	public Printer (int newPitchWidth, int newPitchLength) {
		pitchWidth = newPitchWidth;
		pitchLength = newPitchLength;
	} /* Printer */
	
} /* Printer */

