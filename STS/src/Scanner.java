/* A class that implements a simple text scanner that can be used by a parser.

   Author: Spiros Mancordis (c) 2011
 */

class Scanner {
	private String str;		/* Contains the text to be scanned.      	*/
  	private int  pos;		/* Current character of the text being scanned. */
  	private int lastPos;	/* Last character of the text to be scanned. 	*/


	/* Skip over all of the next consecutive whitespace characters. 
	 */
	public void scanWhitespace () {
    	while (!endOfText () && Character.isWhitespace(currentChar()))
    		pos++;
  	}

	
	/* Check if the end of the text has been reached.
	 */
	public boolean endOfText () {
		return pos == lastPos;
	}

	
	/* Scan text and return a string that contains a natural number.
	 */
  	public String scanNumber () {
    	int oldpos = pos;

    	while (!endOfText() && Character.isDigit(currentChar()))
      		pos++;

		return str.substring(oldpos, pos);
  	}


	/* Scan text and return a string that contains a word consisting of only
	   alphabetic letters.
	 */
  	public String scanWord () {
    	int oldpos = pos;

    	while (!endOfText() && Character.isLetter(currentChar()))
      		pos++;

		return str.substring(oldpos, pos);
  	}


	/* Return the current character being scanned.
	 */
	public char currentChar () {
    	return str.charAt(pos);
  	}


	/* Move the scanner ahead by one character, unless the end of text is reached.
	 */
  	public boolean next () {
    	if (endOfText()) 
    		return false;
		else 
			pos++;

		return true; 
  	}


	/* Construct and initialize a scanner.
	 */
  	public Scanner (String text) {
    	str = new String(text);
    	lastPos = str.length() - 1;
  	}

} /* Scanner */

