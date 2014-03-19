/* A class that implements an input reader for file or standard input. The input is stored
   in a single string.

   Author: Spiros Mancoridis (c) 2011
 */

import java.io.*;

class Input {
	private BufferedReader is;	/* The buffer reader variable. 		*/ 


 	 /* Read the entire input and store it into a single string.
	  */
 	 public String input2String () {
    	String input = new String("");

    	try {
      		while (true) {
        		String line = is.readLine();

       			if (line == null) 
					break;

       			input = input.concat(line + "\n");
   			}
   		} catch (Exception e) {
       		e.printStackTrace();
       		System.exit(1);
   		}

   		return input;
  	}


  	/* Construct an input reader that reads input from a specified file. 
	 */
  	Input (String fileName) {
		assert fileName != null : "Null file name passed to Input().";

    	try {
      		is = new BufferedReader(new FileReader(fileName));
    	} catch (Exception e) {
      		e.printStackTrace();
      		System.exit(1);
    	}
  	}


  	/*  Construct an input reader that reads input from standard input. 
	 */
  	Input () {
    		is = new BufferedReader(new InputStreamReader(System.in));
  	}

} /* Input */
