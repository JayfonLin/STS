/* A class that implements a soccer tournament. Every team plays the other team twice, once as
   the home team and once as the away team. At the end of the tournament a standings table
   is produced showing the statistics of each team.

   Author: Spiros Mancoridis (c) 2011
 */

import java.util.*;

class Tournament {
	private int numTeams;		    					/* Number of teams in the tournament. 	  	    			*/
	private Hashtable<Team,TeamRecord> teamStats;		/* Statistics of each team are stored in a hash table.  	*/
	private TeamRecord [] teamRecords;  				/* Array used to sort the teams based on points earned. 	*/
	private int pitchWidth;		    					/* Standard width of the pitch for the tournament.      	*/
	private int pitchLength;	    					/* Standard length of the pitch for the tournament.     	*/
	private Vector<Team> teams;		    				/* Vector of teams participating in the tournament.	    	*/
	private boolean outputScores;						/* Keeps track of whether scores should be output.			*/
	private boolean outputStandings;					/* Keeps track of whether standings should be output 		*/


	/* Output a scores table of the teams in the tournament, complete with the home (rows)
	   and away (columns) results between every pair of teams in the tournament.
	 */
	void outputScoresTable (TeamRecord [] teamRecords) {
		if (!outputScores) return;
		System.out.printf("%20s", " ");

		for (int i=0; i < numTeams; i++) {
			Team team = teams.elementAt(i);
			int end = team.getName().length();
			if (end > 13) end = 12;	
			System.out.printf("%s %2s", team.getName().substring(0,end), " ");
		}

		System.out.printf("\n");

		for (int i=0; i < 118; i++)
			System.out.printf ("_"); 

		System.out.printf ("\n"); 

		for (int i=0; i < numTeams; i++) {
			Team homeTeam = teams.elementAt(i);
			int end = homeTeam.getName().length();
			end = (end > 13) ? 12 : end;	// cut the name of the team to 12 characters maximum
			
			System.out.printf("%-16s", homeTeam.getName().substring(0,end));
			TeamRecord homeTeamRecord = teamStats.get(homeTeam);

			for (int j=0; j < numTeams; j++) {
				if (i != j) {
					Team awayTeam = teams.elementAt(j);
					TeamResult tres = homeTeamRecord.getScore(awayTeam);
					System.out.printf("%7s %d-%d ", " ", tres.getHomeScore(), tres.getAwayScore());
				} else System.out.printf("%7s %3s ", " ", "---"); 
			}
			System.out.printf("\n");
		}
		System.out.printf("\n");
	}


	/* Output a standings table of the teams in the tournament, complete with a header
	   and the individual teams statistics.
	 */
	void outputStandingsTable (TeamRecord [] teamRecords) {
		if (!outputStandings)
			return;

		/* Output the standings table header. 
		 */
		System.out.printf("%20s %15s %27s %30s\n", " ", "HOME", "AWAY", "OVERALL");
		System.out.printf("%-20s", "TEAM");
		System.out.printf ("%4s %4s %4s %4s %4s    ", "W", "D", "L", "GS", "GA"); 
		System.out.printf ("%4s %4s %4s %4s %4s    ", "W", "D", "L", "GS", "GA"); 
		System.out.printf ("%4s %4s %4s %4s %4s %4s    ", "P", "W", "D", "L", "GS", "GA");
		System.out.printf ("%4s %4s\n", "GD", "PTS"); 

		for (int i=0; i < 118; i++)
			System.out.printf ("_"); 
		
		System.out.printf ("\n"); 

		/* Output the rest of the standings table. 
		 */
		for (int i=0; i < teamRecords.length; i++) {
			teamRecords[i].outputRecord();
		}
	} 


	/* Sort the team records according to the points they earned during the tournament. The
	   sorting algorithm is Insertion Sort. The list of teamRecords is sorted in non-ascending 
	   order.
	 */
	void sortTeamRecords (TeamRecord [] teamRecords) {
		assert teamRecords != null : "No team records to sort.";
		
    		int firstOutOfOrder, location;
		int numTeamRecords = teamRecords.length;
    
    		for (firstOutOfOrder = 1; firstOutOfOrder < numTeamRecords; firstOutOfOrder++) { 
    			if(teamRecords[firstOutOfOrder].getPoints()  > teamRecords[firstOutOfOrder - 1].getPoints()) { 
    				TeamRecord temp = teamRecords[firstOutOfOrder];
    				location = firstOutOfOrder;
            
    				do {
    					teamRecords[location] = teamRecords[location-1];
    					location--;
    				} while (location > 0 && teamRecords[location-1].getPoints() < temp.getPoints());
               	
    				teamRecords[location] = temp;
    			}
    		}
	}


	/* Play a tournament where each team plays every other team twice. Once as a home 
	   team and once as a visiting (away) team. At the end of the tournament, output 
	   the final standings table.
	 */
	public void play () {
		for (int i=0; i < numTeams; i++) {
			for (int j=0; j < numTeams; j++) {
				Team team1 = teams.elementAt(i);
				Team team2 = teams.elementAt(j);
	
				/* Teams do not play themselves. 
				 */
				if (i == j) continue; 

				/* Every game is played on a new pitch and with a new ball.
				 */
				Pitch pitch = new Pitch(pitchWidth, pitchLength);
		        Ball ball = new Ball(new Point2D(pitchWidth/2, pitchLength/2));

				/* Play the game ...
				 */
				Game g = new Game(team1, team2, ball, pitch);
				
				assert team1.roamingRegionsOK() : "Team " + team1.getName() + " has an invalid roaming region.";
				assert team2.roamingRegionsOK() : "Team " + team2.getName() + " has an invalid roaming region.";
				assert team1.getSize() == team2.getSize() : "Teams have an unequal number of players.";

				/* Get the final score, and record it.
				 */
				int team1Score = g.getTeam1Score();
				int team2Score = g.getTeam2Score();
				
				boolean home = true;
				TeamRecord team1Record = teamStats.get(team1);
				team1Record.updateRecord(team1Score, team2Score, home);
				team1Record.setScore(team2, team1Score, team2Score);

				home = false;
				TeamRecord team2Record = teamStats.get(team2);
				team2Record.updateRecord(team2Score, team1Score, home);
				// No need to set the score for the away team, only the home team records are kept for each team	
			}
	
		}

		
		/* Sort the team records in non-ascending order and the output the final 
		   standings table.
		 */
		Printer p = new Printer (pitchWidth, pitchLength);
		Team bestTeam = getBestTeam();
		System.out.println("Best Team Strategy: " + bestTeam.getName());
		p.pitchWithRoamingRegions(bestTeam);
		
		outputScoresTable(teamRecords);
		sortTeamRecords(teamRecords);
		outputStandingsTable(teamRecords);
	}



	/* Returns the best team. 
	 */
	Team getBestTeam () {
		int maxPoints = 0;
		Team maxTeam = null;

		for (int i=0; i < numTeams; i++) {
			Team team = teams.get(i);
			TeamRecord tr = teamStats.get(team);
			int points = tr.getPoints();
			if (points > maxPoints) { 
				maxPoints = points;
				maxTeam = team;
			}
		}
		return maxTeam;
	}


	/* Construct and initialize a tournament. 
	 */
	public Tournament (Vector<Team> newTeams, int newPitchWidth, int newPitchLength, String outputSpec) {
		numTeams = newTeams.size();

		assert numTeams > 1 : "At least 2 teams are needed for a tournament.";
		assert newPitchWidth > 0 && newPitchLength > 0 : 
			"Negative pitch dimensions passed to Tournament().";

		pitchWidth = newPitchWidth;
		pitchLength = newPitchLength;
		teams = newTeams;

		/* Create a team record for every team and insert it into a hash table
		   indexed by the team object reference as well as a team records array, 
		   which is used for sorting and outputting the tournament standings table.
		 */
		teamStats = new Hashtable<Team,TeamRecord>();
		teamRecords = new TeamRecord[numTeams];

		for (int i=0; i < numTeams; i++) {
			Team team = (Team) teams.elementAt(i);
			TeamRecord tr = new TeamRecord(team.getName());
			teamStats.put(team, tr); 
			teamRecords[i] = tr;
		}

		if (outputSpec == null) {
			outputStandings = false;
			outputScores = false;
		} else if (outputSpec.compareTo("st") == 0 || outputSpec.compareTo("ts") == 0) {
			outputStandings = true;
			outputScores = true;
		} else if (outputSpec.compareTo("s") == 0) {
			outputStandings = false;
			outputScores = true;
		} else if (outputSpec.compareTo("t") == 0) {
			outputStandings = true;
			outputScores = false;
		} else
			assert false : "Usage: java -ea STS attributes.txt [s|st|ts|t]";
	}
} /* Tournament */



/* A class that maintains statistics about a team's performance in a tournament.

   Author: Spiros Mancoridis (c) 2011
 */
class TeamRecord {
	private String name;
	private int points;
	private int gamesPlayed;
	private int homeWins;
	private int homeDraws;
	private int homeLosses;
	private int homeGoalsAllowed;
	private int homeGoalsScored;
	private int awayWins;
	private int awayDraws;
	private int awayLosses;
	private int awayGoalsAllowed;
	private int awayGoalsScored;
	private Hashtable<Team,TeamResult> results; 	/* Keep track of only your home games */


	public void outputRecord () {
		int goalsScored = homeGoalsScored + awayGoalsScored;
		int goalsAllowed = homeGoalsAllowed + awayGoalsAllowed;

		System.out.printf ("%-20s", name);
		System.out.printf ("%4d %4d %4d %4d %4d    ", 
				homeWins, 
				homeDraws, 
				homeLosses, 
				homeGoalsScored, 
				homeGoalsAllowed);

		System.out.printf ("%4d %4d %4d %4d %4d    ", 
				awayWins, 
				awayDraws, 
				awayLosses, 
				awayGoalsScored, 
				awayGoalsAllowed);

		System.out.printf ("%4d %4d %4d %4d %4d %4d    ", 
				gamesPlayed, 
				homeWins + awayWins,
				homeDraws + awayDraws, 
				homeLosses + awayLosses, 
				homeGoalsScored + awayGoalsScored, 
				homeGoalsAllowed + awayGoalsAllowed);

		System.out.printf ("%4d %4d\n", goalsScored-goalsAllowed, points); 
	}


	/* Updates the record of a team after a result. This method should be called by both the home and
	   away team after a result. 
	 */
	public void updateRecord (int ourScore, int theirScore, boolean ourHome) {
		assert ourScore >= 0 && theirScore >= 0 : "Negative scores not allowed.";
		
		boolean weWon = (ourScore > theirScore);
		boolean weDrew = (ourScore == theirScore);

		gamesPlayed++;

		if (weWon) 
			points +=3;
		else if (weDrew) 
			points += 1;
		
		/* The rest of this code is for statistical purposes only.
		 */
		if (ourHome) {
			if (weWon)
				homeWins++;
			else if (weDrew)
				homeDraws++;
			else homeLosses++;

			homeGoalsAllowed += theirScore;
			homeGoalsScored += ourScore;
		} else {
			if (weWon)
				awayWins++;
			else if (weDrew)
				awayDraws++;
			else awayLosses++;

			awayGoalsAllowed += theirScore;
			awayGoalsScored += ourScore;
		}
	}


	public int getPoints () {
		return points;
	}


	public TeamResult getScore (Team team) {
		TeamResult teamResult = results.get(team);
		return teamResult;
	}


	public void setScore (Team awayTeam, int homeScore, int awayScore) {
		assert (homeScore >= 0 && awayScore >= 0) : "Scores cannot be negative.";
		assert awayTeam != null : "No team specified.";
		
		TeamResult tr = new TeamResult(homeScore, awayScore);
		results.put(awayTeam, tr); 
	}


	TeamRecord (String newName) {
		name = newName;
		points = 0;
		gamesPlayed = 0;
		homeWins = 0;
		homeDraws = 0;
		homeLosses = 0;
		homeGoalsAllowed = 0;
		homeGoalsScored = 0;
		awayWins = 0;
		awayDraws = 0;
		awayLosses = 0;
		awayGoalsAllowed = 0;
		awayGoalsScored = 0;
		
		results = new Hashtable<Team,TeamResult>();
	}
}


/* A class that maintains a team's score for a single game of a tournament.

   Author: Spiros Mancoridis (c) 2011
 */
class TeamResult {
	private int homeScore;			/* Home score. */
	private int awayScore;			/* Away score. */

	/* Return the home score.
	 */
	public int getHomeScore () {
		return homeScore;
	}

	/* Return the away score.
	 */
	public int getAwayScore () {
		return awayScore;
	}

	/* Construct a single game result and initialize the game's score.
	 */
	TeamResult (int newHomeScore, int newAwayScore) {
		assert (newHomeScore >= 0 && newAwayScore >= 0) : "Scores cannot be negative.";
			
		homeScore = newHomeScore;
		awayScore = newAwayScore;
	}
} /* TeamResult */

