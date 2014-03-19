/* Main class that reads an attributes file whose name is in args[0] and, based on the 
   contents of that file, creates a set of teams and plays a soccer tournament.
 */
import java.util.*;

public class STS {

	public static void main (String args[]) {
		if (args == null || args.length > 2) {
			Quit.now("Usage: java STS attributes.txt [s|st|ts|t]");
		}
		
		int pitchWidth = 0;
		int pitchLength = 0;
		String outputSpec = null;

		if (args.length == 2)
			outputSpec = args[1];

		/* Read the input file and store its contents into a single string.
		 */
		Input in = new Input(args[0]);
		String text = in.input2String();

		/* Parse the string to get pitch and team attributes.
		 */
		Parser parser = new Parser(text);
		
		/* Get the pitch and team attributes from the Parser.
		 */
		Vector<Team> teams = new Vector<Team>();
		pitchLength = parser.getPitchLength();
		pitchWidth = parser.getPitchWidth();
		final int numTeams = parser.getNumberOfTeams();
		
		for (int i=0; i < numTeams; i++) {
			int numPlayers = parser.getNumberOfPlayers(i);
			String name = parser.getName(i);
			Vector<Point2D[]> customRoaming = parser.getCustomRoaming(i);
			teams.add(new Team(name, Integer.toString(i).charAt(0), numPlayers, pitchWidth, pitchLength, customRoaming));
		}

		/* Create a tournament and and play it.
		 */
		Tournament tournament = new Tournament(teams, pitchWidth, pitchLength, outputSpec);
	 	tournament.play();
	} 
} /* STS */
