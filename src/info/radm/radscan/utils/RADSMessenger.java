package info.radm.radscan.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class RADSMessenger {

	private static String radsTextLogo = "resources/rads_text_logo.txt";
	
	/**
	 * 
	 * @param rowName
	 * @param rowValue
	 */
	public static void writeTable(String rowName, String rowValue) {
		rowName = String.format("%1$-" + 35 + "s", rowName);
		rowValue = String.format("%1$-" + 50 + "s", rowValue);
		System.err.println(rowName+": "+rowValue);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public static void writeMessage(String message) {
		System.err.println(message);
	}

	
	/**
	 * 
	 */
	public static void printHR() {
		int length = 75;
		StringBuilder hr = new StringBuilder();
		for (int i = 0; i < length; i++)
			hr.append("-");
		
		System.err.println(hr.toString());
	}
	
	
	/**
	 * 
	 */
	public static void printBanner() {
		InputStream is = RADSMessenger.class.getResourceAsStream(radsTextLogo);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		String line;
		
		try {
			while ((line = in.readLine()) != null)
				System.err.println(line);
			
			is.close();
			in.close();
			
		} 
		catch (IOException e) {
			System.err.println("NOT FOUND!");
			e.printStackTrace();
		}
	}
	

}
