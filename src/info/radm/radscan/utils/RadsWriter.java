package info.radm.radscan.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RadsWriter {

	private File outFile = null;
	private BufferedWriter bw = null;
	private FileWriter fw = null;
	private final String NEW_LINE = System.getProperty("line.separator");
	private String desc = null;
	
	public RadsWriter(String outFile, String desc) throws IOException  {
		this.outFile = new File(outFile);
		this.fw = new FileWriter(outFile);
		this.bw = new BufferedWriter(fw);
		this.desc = desc;
	}
	
	public RadsWriter() { };
	
	public boolean isStdOutMode() {
		return (outFile == null);
	}
	
	
	public void writeln(String outString) {
		if (outFile != null) {
			try {
				bw.write(outString+NEW_LINE);
			} 
			catch (IOException e) {
				System.err.println("ERROR: Could not print to "+outFile.getAbsolutePath());
				System.exit(-1);
			}
		}
		else 
			System.out.println(outString);
	}
	
	public void destroy() {
		if (outFile != null) {
			try {
				this.bw.close();
				this.fw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getFileDescription() {
		return this.desc;
	}
	
	public boolean isToFile() {
		return (outFile != null);
	}
	
	public String getOutFilePath() {
		String filePath = null;
		if (outFile != null) {
			filePath = outFile.getAbsolutePath();
		}
		return filePath;
	}
	
}
