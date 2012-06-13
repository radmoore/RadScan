package info.radm.radscan;

import info.radm.radscan.utils.ProgressBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Parser {
	
	private File xdomOutFile;
	private String xdomURL;
	private ProgressBar pbar;
	
	public Parser(String xdomURL, ProgressBar pbar) {
		this.xdomURL = xdomURL;
		this.pbar = pbar;
	}
	
	public void parse(int max) {
		pbar.setProgressMode(ProgressBar.PROGRESSABLE_MODE);
		pbar.setMaxVal(max);
		BufferedReader reader = null;
		File outXdom = new File("/home/radmoore/Desktop/rads_search.xdom");
		
		int val = 0;
		try {
			FileWriter fw = new FileWriter(outXdom);
			BufferedWriter outFile = new BufferedWriter(fw);
			reader = read(xdomURL);
			String line = null;
			
			while ( (line = reader.readLine()) != null) {
				if (line.substring(0, 1).equals(">")) {
					val++;
					pbar.setVal(val);
				}
				outFile.write(line+"\n");
				
			}
			pbar.finish();
			outFile.close();
			reader.close();
		}
		catch(MalformedURLException mue) {
			mue.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}

		
	}
	
	
	private BufferedReader read(String url) throws IOException, MalformedURLException{
		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));}
	

	
}
