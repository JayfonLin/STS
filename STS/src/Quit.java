/* A class that quits the program when the program enters a non-recoverable state.

   Author: Spiros Mancoridis (c) 2011
 */
public class Quit {
	
	static void now(String error) {
		System.out.println(error);
		System.exit(1);
	}
}
