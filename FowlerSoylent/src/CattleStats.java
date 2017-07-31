import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * Utility method used to generate cattle
 * @author Max Fowler
 *
 */
public class CattleStats {
	
	private static String[] nameList;

	/**
	 * Makes an ID for a cattle, between 0 and 5000
	 * Appends CN
	 * @return
	 */
	public static String generateID() {
		int temporaryInt = (int) (Math.random() * 5000);
		return "CN" + temporaryInt;
	}
	
	/**
	 * Makes an age for a cattle, between 18 and 28
	 * @return
	 */
	public static int generateAge(){
		int temporaryAge = (int) (Math.random() * 10 + 18);
		return temporaryAge;
	}

	/**
	 * Generate a random name by picking a name from the namelist
	 * @return
	 */
	public static String generateName() {
			int randomChoice = (int) (Math.random() * 68);
			return nameList[randomChoice];
	
	
	}
	
	/**
	 * Preload the namelist for cattle.
	 * 
	 */
	public static void intializeNameList(){
		BufferedReader s;
		try {
			URL url = CattleStats.class.getResource("resources/names");
			s = new BufferedReader(new InputStreamReader(url.openStream()));
			nameList = new String[68];
			for(int i = 0; i < nameList.length; i++){
				nameList[i] = s.readLine().trim();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The names failed", 
					"D:", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
	}

}
