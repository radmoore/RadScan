package info.radm.radscan;

public interface RADSQuery {

	public static int FASTA = 0;
	public static int XDOM = 1;
	
	public RADSQuery build();
	
	public String getQueryString();
	
	public String getQueryID();

	// TODO:
	// getQueryFile
	// getSequence ?
	// setStartTime ?
	
}
