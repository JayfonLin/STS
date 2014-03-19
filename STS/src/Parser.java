/* A class that implements an XML-style parser for reading in attributes that
   are used in the soccer game.
 
  The grammar of this parser is:
  	RECORDS ::= {PITCH_RECORD | TEAM_RECORD}*
  	PITCH_RECORD ::= '<begin' 'pitch' '>' {PITCH_ATTRIBUTES}* '<\end' 'pitch' '>'
  	PITCH_ATTRIBUTES ::= PITCH_LENGTH | PITCH_WIDTH
  	PITCH_LENGTH ::= '<begin' 'length' '>' NUMBER '<\end' 'length' '>'
  	PITCH_WIDTH  ::= '<begin' 'width' '>' NUMBER '<\end' 'width' '>'

  	TEAM_RECORD ::= '<begin' 'team' '>' {TEAM_ATTRIBUTES}* '<\end' 'team' '>'
  	TEAM_ATTRIBUTES ::= TEAM_NAME | TEAM_NUMBER_OF_PLAYERS | TEAM_STRATEGY 
    	TEAM_NAME ::= WORD {' ' WORD}
	TEAM_NUMBER_OF_PLAYERS ::= NUMBER
	TEAM STRATEGY ::= 'random' | 'custom' TEAM_REGIONS 
	TEAM_REGIONS ::= {'<region> POINT POINT '<\region>'}* 
	POINT ::= '(' NUMBER ',' NUMBER ')'

  	NUMBER ::= DIGIT NUMBER | DIGIT
  	DIGIT ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
  	WORD ::= LETTER WORD | LETTER
  	LETTER ::= 'a' | 'b' | ... | 'z' | 'A' | 'B' | ... | 'Z'

   Author: Spiros Mancoridis (c) 2011
 */

import java.util.*;

class Parser {
	private Scanner s;								/* Scanner used by the parser. 					*/
	private LinkedList<TeamAttributes> teams;		/* LinkedList of all of the attribute records. 	*/
	private PitchAttributes pitch; 					/* Object contains the attributes of the pitch. */

	
	/* Returns the length of the pitch.
	 */
	public int getPitchLength () {
		assert pitch != null : "Pitch attributes have not been initialized by Parser class.";
		
		return pitch.getLength();
	}
	
	
	/* Returns the length of the pitch.
	 */
	public int getPitchWidth () {
		assert pitch != null : "Pitch attributes have not been initialized by Parser class.";
		
		return pitch.getWidth();
	}

	
	/* Returns the number of teams.
	 */
	public int getNumberOfTeams () {
		return teams.size();
	}
	
	
	/* Returns the number of players of the ith team.
	 */
	public int getNumberOfPlayers (int i) {
		assert teams != null : "No teams exist.";
		assert i < teams.size() : "There are fewer than " + i + " teams.";
		
		return teams.get(i).getNumberOfPlayers();
	}

	
	/* Returns the strategy of the ith team (random or custom).
	 */
	public  String getStrategy (int i) {
		assert teams != null : "No teams exist.";
		assert i < teams.size() : "There are fewer than " + i + " teams.";
		
		return teams.get(i).getStrategy();
	}
	
	
	/* Returns the name of the ith team.
	 */
	public  String getName (int i) {
		assert teams != null : "No teams exist.";
		assert i < teams.size() : "There are fewer than " + i + " teams.";
		
		return teams.get(i).getName();
	}
	
	
	/* Returns the custom roaming strategy of the ith team as a Vector of 2D coordinates.
	 */
	public Vector<Point2D[]> getCustomRoaming (int i) {
		assert teams != null : "No teams exist.";
		assert i < teams.size() : "There are fewer than " + i + " teams.";
		
		return teams.get(i).getCustomRoaming();
	}
	
	
	/* Check if a String contains only alphabetic characters and spaces.
     */
    private boolean isWords (String words) {
    	assert words != null : "Null string words passed to isWords().";

        int wordsLen = words.length();

        for (int i=0; i < wordsLen; i++) {
        	char chr = words.charAt(i);
            if (!Character.isLetter(chr) && !Character.isWhitespace(chr))
            	return false;
		}

        return true;
    }


    /* Check if a String contains only alphabetic characters.
     */
    private boolean isWord (String word) {
    	assert word != null : "Null string word passed to isWord().";

        int wordLen = word.length();

        for (int i=0; i < wordLen; i++)
        	if (!Character.isLetter(word.charAt(i)))
        		return false;

        return true;
    }


    /* Check if a String contains only digits.
     */
    private boolean isNumber (String number) {
    	assert number != null : "Null string number passed to isNumber().";

        int numLen = number.length();

        for (int i=0; i < numLen; i++)
        	if (!Character.isDigit(number.charAt(i)))
        		return false;

        return true;
    }


	/* Skip whitespaces, then scan and (hopefully) match the string passed to this routine,
	   then skip whitespaces again to prepare for the rest of the parsing process. If the
	   match is perfect, the function returns, else it quits the program.
	 */	
	private void match (String str) {
		assert str != null : "Null string passed to match().";

      	s.scanWhitespace();
		String currStr = "";

		for (int i=0; i < str.length(); i++) {
			currStr += Character.toString(s.currentChar());
			
			if (!s.next())
				Quit.now("Unexpected end of file.");
		}

      	s.scanWhitespace();
        
      	if (str.compareTo(currStr) != 0)
      		Quit.now("Parser error in input file, " + str + " expected.");
	}


	/* The PITCH_LENGTH production of the grammar.
	 */
	private void PITCH_LENGTH (PitchAttributes pitchAttributes) {
		assert pitchAttributes != null : "Null pitch attributes passed to PITCH_LENGTH().";

		match(">");
		String number = s.scanNumber();
		pitchAttributes.setLength(Integer.parseInt(number));
		match("<\\length>");
	}


	/* The PITCH_WIDTH production of the grammar.
	 */
	private void PITCH_WIDTH (PitchAttributes pitchAttributes) {
		assert pitchAttributes != null : "Null pitch attributes passed to PITCH_WIDTH().";

		match(">");
		String number = s.scanNumber();
		pitchAttributes.setWidth(Integer.parseInt(number));
		match("<\\width>");
	}


	/* The PITCH_ATTRIBUTES production of the grammar.
	 */
	private void PITCH_ATTRIBUTES () {
		String word;
		pitch = new PitchAttributes();

		match("<");

		while (s.currentChar() != '\\') {
			word = s.scanWord();

			if (word.compareTo("length") == 0) 
				PITCH_LENGTH(pitch);
			else if (word.compareTo("width") == 0) 
				PITCH_WIDTH(pitch);
			else assert false : "Syntax error: unsupported tag.";

			match("<");
		}

		match("\\end pitch>");
	}


	/* The TEAM_NAME production of the grammar.
	 */
	private void TEAM_NAME (TeamAttributes teamAttributes) {
		assert teamAttributes != null : "Null team attributes passed to TEAM_NAME().";

		match(">");

		String word = s.scanWord();
		String words = word;

		while (word.length() > 0) {
			s.scanWhitespace();
			word = s.scanWord();
			if (word.length() > 0)
				words = words + " " + word;
		};
 
		teamAttributes.setName(words);
        assert isWords(words) : "Words expected but '" + words + "' found.";
		
        match("<\\name>");
	}


	/* The TEAM_NUMBER_OF_PLAYERS production of the grammar.
	 */
	private void TEAM_NUMBER_OF_PLAYERS (TeamAttributes teamAttributes) {
		assert teamAttributes != null : 
			"Null team attributes passed to TEAM_NUMBER_OF_PLAYERS().";

		match(">");
		
		String number = s.scanNumber();
        assert isNumber(number) : "Number expected but '" + number + "' found.";
		
        teamAttributes.setNumberOfPlayers(Integer.parseInt(number));
		match("<\\numberOfPlayers>");
	}


	private void TEAM_REGIONS (TeamAttributes teamAttributes) {
		assert teamAttributes != null : "Null team attributes passed to TEAM_STRATEGY().";

		match("<");

		while (s.currentChar() != '\\') {
			match("region>");

			/* Read in (x1,y1) 
		 	*/
			match("(");
			int x1 = Integer.parseInt(s.scanNumber());
			
			match(",");
			int y1 = Integer.parseInt(s.scanNumber());
			
			match(")");

			/* Read in (x4,y4) 
		 	*/
			match("(");
			int x4 = Integer.parseInt(s.scanNumber());
			
			match(",");
			int y4 = Integer.parseInt(s.scanNumber());
			
			match(")");

			/* Allocate memory for the 2 points
		 	*/
			Point2D [] points = new Point2D[2];
			points[0] = new Point2D(x1,y1);
			points[1] = new Point2D(x4,y4); 

			teamAttributes.addCustomRoaming(points);

			match("<\\region>");

			match("<");
		}
	}


	/* The TEAM_STRATEGY production of the grammar.
	 */
	private void TEAM_STRATEGY (TeamAttributes teamAttributes) {
		assert teamAttributes != null : "Null team attributes passed to TEAM_STRATEGY().";

		match(">");
		
		String word = s.scanWord();
        assert isWord(word) : "Word expected but '" + word + "' found.";

		teamAttributes.setStrategy(word);

		if (word.equals("custom")) {
			TEAM_REGIONS(teamAttributes);
			match("\\strategy>");
		} else
			match("<\\strategy>");
	}


	/* The TEAM_ATTRIBUTES production of the grammar.
	 */
	private void TEAM_ATTRIBUTES () {
		match("<");
		TeamAttributes teamAttributes = new TeamAttributes();
		teams.addLast(teamAttributes);

		String word;
		while (s.currentChar() != '\\') {
			word = s.scanWord();

			if (word.compareTo("name") == 0) 
				TEAM_NAME(teamAttributes);
			else if (word.compareTo("numberOfPlayers") == 0)  
				TEAM_NUMBER_OF_PLAYERS(teamAttributes);
			else if (word.compareTo("strategy") == 0)  
				TEAM_STRATEGY(teamAttributes);
			else assert false : "Syntax error: unsupported tag.";

			match ("<");
		}
		match("\\end team>");
	} 


	/* The RECORDS production of the grammar.
	 */
	private void RECORDS () {
    	while (!s.endOfText()) {
			match("<begin");
      			
			String word = s.scanWord();
            assert isWord(word) : "Word expected but '" + word + "' found.";

			if (word.compareTo("pitch") == 0) {
				match(">"); 
				PITCH_ATTRIBUTES();
			} else if (word.compareTo("team") == 0) {
				match(">"); 
				TEAM_ATTRIBUTES ();
			} else 
				assert false : "Syntax error: unsupported tag.";
      	}
	} 


	/* Construct a parser object. Instantiate a scanner object and then start parsing.
	 */
	public Parser (String text) {
		assert text != null : "Null text string passed to Parser().";

		s = new Scanner(text);
		teams = new LinkedList<TeamAttributes>();

		RECORDS();
	}
} /* Parser */




/* A class that stores attributes of a soccer team in an object. Objects of this class 
   are stored in the attributes LinkedList of the Parser class.

   Author: Spiros Mancoridis (c) 2011
 */

class TeamAttributes {
	private String name;						/*  Name of the team. 				*/
	private int numberOfPlayers;				/*  Number of players on a team. 	*/
	private String strategy;					/*  Team strategy. 					*/
	private Vector<Point2D[]> customRoaming;   	/*  If the player's roaming is prescribed (not assigned 
					   					   			at random) via a custom roaming specification such
					   					   			as a file input, store the custom roaming coordinates
					   					   			in this vector.  Each element of the Vector should be
					   					   			an array with exactly 2 elements (points) for the 
					   					   			(x1,y1) and (x4,y4) points that define a player's
					   					   			roaming region.
					 							*/

	/* Return the name of the team. 
	 */
	public String getName () {
		return name;
	}


	/* Set the name of the team. 
	 */
	public void setName (String newName) {
		assert newName != null : "Null string passed to setName().";

		name = newName;
	}


	/* Return the number of players on team. 
	 */
	public int getNumberOfPlayers () {
		return numberOfPlayers;
	}


	/* Set the number of players on team. 
	 */
	public void setNumberOfPlayers (int newNumberOfPlayers) {
		numberOfPlayers = newNumberOfPlayers;
	}


	/* Return the strategy of the team.
	 */
	public String getStrategy () {
		return strategy;
	}


	/* Set the strategy of the team.
	 */
	public void setStrategy (String newStrategy) {
		assert newStrategy != null : "Null string passed to setStrategy().";

		strategy = newStrategy;
	}


	/* Add an element to the custom roaming Vector corresponding to a team. The element is 
    	   an array with exactly 2 elements (points) for the (x1,y1) and (x4,y4) points that 
	   define a player's roaming region.
	 */
	public void addCustomRoaming (Point2D [] customRoamingCoordinates) {
		if (customRoaming == null) {
			customRoaming = new Vector<Point2D[]>();
		}

		customRoaming.add(customRoamingCoordinates);
	}


	/* Return a Vector of custom roaming points or null if there are none.
	 */
	public Vector<Point2D[]> getCustomRoaming () {
		return customRoaming;
	}


	/* Construct and initialize a team attribute.
	 */
	TeamAttributes (String newName, int newNumberOfPlayers, String newStrategy) {
		assert newName != null : "Null new name string passed to TeamAttributes().";
		assert newNumberOfPlayers >= 0 : "Negative number of players passed to TeamAttributes().";
		assert newName != null : "Null new strategy string passed to TeamAttributes().";

		name = newName;
		numberOfPlayers = newNumberOfPlayers;
		strategy = newStrategy;
		customRoaming = null;
	}


	/* Construct a team attribute. 
	 */
	TeamAttributes () {
		name = "";
		numberOfPlayers = 0;
		strategy = "";
		customRoaming = null;
	}
} /* TeamAttributes */



/* A class that stores attributes of the soccer pitch in an object. Objects of this class
   are stored in the attributes LinkedList of the Parser class.

   Author: Spiros Mancoridis (c) 2011
 */
class PitchAttributes {
    private int width;		/* pitch width  */
	private int length;  	/* pitch length */ 


	/* Return the length of the pitch. 
	 */
	public int getLength () {
		return length;
	}


	/* Set the length of the pitch. 
	 */
    public void setLength (int newLength) {
		assert newLength >= 0 : "Negative pitch length passed to setLength().";
        
		length = newLength;
    }


	/* Return the width of the pitch. 
	 */
    public int getWidth () {
    	return width;
    }


	/* Set the width of the pitch. 
	 */
    public void setWidth (int newWidth) {
    	assert newWidth >= 0 : "Negative pitch width passed to setWidth().";
        
    	width = newWidth;
    }



	/* Construct a pitch attribute. 
	 */
    PitchAttributes () {
    	length = 0;
        width = 0;
    }
} /* PitchAttributes */

