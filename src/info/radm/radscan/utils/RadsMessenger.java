package info.radm.radscan.utils;

public class RadsMessenger {

	public static void writeTable(String rowName, String rowValue) {
		rowName = String.format("%1$-" + 35 + "s", rowName);
		System.err.println(rowName+": "+rowValue);
	}
	
	public static void writeMessage(String message) {
		System.err.println(message);
	}

	public static void printHR() {
		int length = 100;
		StringBuilder hr = new StringBuilder();
		for (int i = 0; i < length; i++)
			hr.append("_");
		
		System.err.println(hr.toString());
	}
	
}
