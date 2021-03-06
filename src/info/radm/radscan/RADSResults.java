package info.radm.radscan;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResults {

	private static String outputBaseUrl = "http://rads.uni-muenster.de/output";
	private String xdomURL, crampageOut, jobURL, jobID = null, runtime;
	private int hits = 0;
	private RADSQuery query;
	private boolean rampageRun = false; 
	
	/**
	 * 
	 * @param query
	 */
	public RADSResults(RADSQuery query) {
		this.query = query;
	}
	
	
	/**
	 * 
	 * @param runtime
	 */
	public void setRawRuntime(String runtime) {
		this.runtime = runtime;
	}
	
	
	/**
	 * 
	 * @param hits_n
	 */
	public void setNumHits(int hits_n) {
		this.hits = hits_n;
	}
	
	
	/**
	 * 
	 */
	public void setRampageRun() {
		this.rampageRun = true;
	}
	
	
	/**
	 * 
	 * @param url
	 */
	public void setXdomUrl(String url) {
		this.xdomURL = url;
	}
	
	
	/**
	 * 
	 * @param url
	 */
	public void setCrampageOut(String url) {
		this.crampageOut = url;
	}
	
	
	/**
	 * 
	 * @param jobID
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	
	

	/**
	 * 
	 * @return
	 */
	public RADSQuery getQuery() {
		return this.query;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getJobID() {
		return this.jobID;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCrampageOutUrl() {
		if (jobID == null)
			return null;
		return outputBaseUrl+"/"+jobID+".crampage.out";
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isRampageRun() {
		return rampageRun;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getRawRuntime() {
		return this.runtime;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getHitsNumber() {
		return this.hits;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getXdomUrl() {
		return this.xdomURL;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getJobUrl() {
		return RADSRunner.RADSQueryUrl+"jobid="+jobID;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCrampageOut() {
		return RADSRunner.RADSBaseUrl+"/output/"+jobID+".crampage.out";
	}
	
	
}
