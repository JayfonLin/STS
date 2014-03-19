/* A class that implements a soccer team.

   Author: Spiros Mancoridis (c) 2011
 */

import java.util.*;

public class Team {
	private String name;					/* Name of the team. 										*/
	private char teamId;					/* Identifier of the team. 									*/
	private int teamSize;					/* Number of players on the team. 							*/
	private Player [] players;				/* Players on the team.										*/
	private int goal;						/* Team's goal identifier, value is 1 (home) or 2 (away). 	*/
	private int pitchWidth, pitchLength;	/* Length and width of the pitch.							*/


	/* Return the identifier for the goal of the team.
	 */
	public int getGoal () {
		return goal;
	}


	/* Set the goal of the team, either 1 or 2. 
	 */
	public void setGoal (int newGoal) {
		assert (newGoal == 1 || newGoal == 2) : "Goal must be either 1 (home) or 2 (away).";
		
		goal = newGoal;
	}


	/* Return the size of the team. I.e., the number of players on the team.
	 */
	public int getSize () {
		return teamSize;
	}


	/* Set the size of the team. I.e., the number of players on the team.
	 */
	public void setSize (int newSize) {
		assert newSize > 0 : "Team must have at least one player.";
		
		teamSize = newSize;
	}


	/* Set the ith player to be the player passed to the function.
	 */
	public void setPlayer(Player player, int i) {
		assert (0 <= i && i < teamSize) : "Cannot assign player to team.";
		assert players != null : "Cannot assign a player to a null team.";
		assert player != null : "Null player cannot be assigned to a team.";
		
		players[i] = player;	
		player.setTeam(this);
	}


	/* Return the ith player of a team.
	 */
	public Player getPlayer (int i) {
		assert (0 <= i && i < teamSize) : "Player out of range: " + i;

		if (i < teamSize)
			return players[i];
		else
			return null;
	}


	/* Return the team name.
	 */
	public String getName () {
		return name;
	}


	/* Return the identifier of the team (used for printing). 
	 */
	public char getId () {
		return teamId;
	}


	/* Sanity check to ensure that the area of the assigned roaming region is less than the maximum area.
	 */
 	public boolean roamingRegionsOK () {
		int maxPlayerArea = (pitchWidth * pitchLength) / teamSize;
		int maxGoalkeeperArea = (pitchWidth*4/6) * (pitchLength/6);

        for (int i=0; i < teamSize; i++) {
            Point2D [] points = players[i].getRoamingCoordinates();
			int x1 = points[0].getX();
			int y1 = points[0].getY();
			int x4 = points[3].getX();
			int y4 = points[3].getY();

			if (i == 0) {
				if (Math.abs(x4-x1) * Math.abs(y1-y4) > maxGoalkeeperArea) {
					return false;
				}
			} else {
				if (Math.abs(x4-x1) * Math.abs(y4-y1) > maxPlayerArea) {
					return false;
				}
			}
        }	
		
        return true;
    }

 	
	/* Set the roaming coordinates of each player on a team to either home or away
	   coordinates. This is necessary because the home team moves in one direction 
	   and the away team moves in the opposite direction on the pitch. Both sets of 
	   roaming coordinates are stored by each player, at player construction time. 
	   Depending on whether the team is home or away, the appropriate set of roaming 
	   coordinates is selected.
	 */
	public void setPlayersRoamingCoordinates () {
		boolean isHome = (goal == 1);

		for (int i=0; i < teamSize; i++)
			if (isHome)
				players[i].setRoamingToHome();
			else
				players[i].setRoamingToAway();
	}


	/* Construct and initialize a team.
	 */
	public Team (String newName, char newTeamId, int numPlayers, int newPitchWidth, int newPitchLength, Vector<Point2D[]> customRoaming) {
		pitchWidth = newPitchWidth;
		pitchLength = newPitchLength;
		/* Sanity check the input parameters */
		assert 0 < pitchWidth && 0 < pitchLength : "Illegal pitch size";
		assert  numPlayers > 0 : "Illegal number of players.";
		assert newTeamId != ' ' && newName != null && newName.length() > 0 : "Illegal team name or identifier.";

		name = newName;
		teamId = newTeamId;
		teamSize = numPlayers;

		/* allocate the array of players */
		players = new Player[numPlayers];

		boolean isCustomRoaming = (customRoaming != null);

		/* create player objects and load the player array */
		for (int i=0; i < numPlayers; i++) {
			Point2D [] customRoamingCoordinates = null;

			if (isCustomRoaming) 
				customRoamingCoordinates = customRoaming.get(i);

			players[i] = new Player(this, i+1, pitchWidth, pitchLength, customRoamingCoordinates);
		}
	}

} /* Team */ 

