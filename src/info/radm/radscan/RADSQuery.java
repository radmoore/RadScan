package info.radm.radscan;

import info.radm.radscan.utils.ProgressBar;

public interface RADSQuery {

	public static int FASTA = 0;
	public static int XDOM = 1;
	public static int RAWSEQ = 2;
	
	public RADSQuery build();
	
	public String getQueryString();
	
	public String getQueryID();

	public boolean getQuietMode();
	
	public ProgressBar getProgressBar();
	
	public String getDatabase();
	
	
	// TODO:
	// getQueryFile
	// getSequence ?
	// setStartTime ?
	
}
