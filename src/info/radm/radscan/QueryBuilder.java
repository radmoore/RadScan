package info.radm.radscan;

import info.radm.radscan.utils.ProgressBar;
import info.radm.radscan.utils.RadsMessenger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class QueryBuilder implements RADSQuery{

	private String queryString = null, queryID = "rawseq", querySequence = null;
	private int format = -1;
	private File queryFile;
	private boolean quiet = false, benchmarkMode = false;
	private String seqChecksum;
	private ProgressBar pBar;
	private String database = "uniprot", algo = "rads", matrix = "BLOSUM62";
	
	private int gp_rampage_M = 150, gp_rampage_m= -100,
			gp_rampage_G =-50, gp_rampage_g = -25, gp_rampage_T= -100,
			gp_rampage_t =-50, gp_rampage_I = -10,
			gp_rampage_i = -1, gp_rampage_E = 0, gp_rampage_e = 0;
	
	/**
	 * 
	 * 
	 */
	public QueryBuilder() {	
		this.pBar = new ProgressBar(500, "Inititating", ProgressBar.INTERMEDIATE_MODE);
	}
	
	
	/**
	 * 
	 */
	public void setQueryProtein(String queryFilePath) {
		try {
			this.queryFile = new File(queryFilePath);
			setFormat();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param quiet
	 */
	public void setQuietMode(boolean quiet) {
		this.quiet = quiet;
		pBar.setQuietMode(quiet);
	}

	
	
	public void setBenchmarkMode(boolean mode) {
		this.benchmarkMode = mode;
	}
	
	
	/**
	 * 
	 * @param algo
	 */
	public void setAlgorithm(String algo) {
		this.algo = algo;
	}
	
	
	/**
	 * 
	 */
	public void setMatrix(String maxtrix) {
		this.matrix = maxtrix;
	}

	
	/**
	 * 
	 * @param database
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isQuiet() {
		return this.quiet;
	}
	
	
	
	public boolean isBenchmarking() {
		return this.benchmarkMode;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getAlgorithm() {
		return this.algo;
	}
	
	/**
	 * 
	 */
	public String getDatabase() {
		return this.database;
	}

	
	/**
	 * 
	 * @return
	 */
	public String getFileName() {
		return queryFile.getName();
	}
	
	
	public String getMatrix() {
		return this.matrix;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getFormat() {
		return this.format;
	}

	/**
	 * 
	 */
	public boolean getQuietMode() {
		return this.quiet;
	}
	
	/**
	 * 
	 */
	public String getQueryString() {
		return queryString;
	}
	
	
	/**
	 * 
	 */
	public ProgressBar getProgressBar() {
		return this.pBar;
	}
	
	
	/**
	 * 
	 */
	public String getQueryID() {
		return this.queryID;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getSeqChecksum() {
		return this.seqChecksum;
	}
	
	
	/**
	 * RADS: Match score
	 * @param G
	 */
	public void setRads_M(int M) {
		this.gp_rampage_M = M;
	}

	
	/**
	 * RADS: Mismatch penalty
	 * @param G
	 */
	public void setRads_m(int m) {
		this.gp_rampage_m = m;
	}
	
	

	/**
	 * RADS: Internal opening GAP penalty
	 * @param G
	 */
	public void setRads_G(int G) {
		this.gp_rampage_G = G;
	}
	
	
	/**
	 * RADS: Internal extension GAP penalty
	 * @param g
	 */
	public void setRads_g(int g) {
		this.gp_rampage_g = g;
	}
	
	
	/**
	 * RADS: Terminal opening GAP penalty
	 * @param T
	 */
	public void setRads_T(int T) {
		this.gp_rampage_T = T;
	}
	
	
	/**
	 * RADS: Terminal extension GAP penalty
	 * @param t
	 */
	public void setRads_t(int t) {
		this.gp_rampage_t = t;
	}
	
	
	/**
	 * RAMPAGE: Internal opening GAP penalty
	 * @param I
	 */
	public void setRampage_I(int I) {
		this.gp_rampage_I = I;
	}
	
	
	/**
	 * RAMPAGE: Internal extension GAP penalty
	 * @param i
	 */
	public void setRampage_i(int i) {
		this.gp_rampage_i = i;
	}
	
	
	/**
	 * RAMPAGE: Terminal opening GAP penalty
	 * @param E
	 */
	public void setRampage_E(int E) {
		this.gp_rampage_E = E;
	}
	
	
	/**
	 * RAMPAGE: Terminal opening GAP penalty
	 * @param e
	 */
	public void setRampage_e(int e) {
		this.gp_rampage_e = e;
	}
	
	
	
	/**
	 * 
	 */
	// TODO: use StringBuilder more efficiently
	public RADSQuery build() {
		StringBuilder qString = new StringBuilder();
		qString.append(RADSRunner.RADSQueryUrl);
		String urlAlgo = "algorithm=rads";
		if (algo.equals("rampage"))
			urlAlgo = "algorithm=rads&algorithm=rampage";
			
		try {
			qString.append("apicall=1"+
					"&"+urlAlgo+
					"&dbname="+database+
					"&gp_rampage_M="+gp_rampage_M+
					"&gp_rampage_m="+gp_rampage_m+
					"&gp_rampage_G="+gp_rampage_G+
					"&gp_rampage_g="+gp_rampage_g+
					"&gp_rampage_T="+gp_rampage_T+
					"&gp_rampage_t="+gp_rampage_t+
					"&matrix="+matrix+
					"&gp_rampage_I="+gp_rampage_I+
					"&gp_rampage_i="+gp_rampage_i+
					"&gp_rampage_E="+gp_rampage_E+
					"&gp_rampage_e="+gp_rampage_e+
					"&query=" + URLEncoder.encode(queryString, "utf8")+
					"&db="+database+
					"&Submit.x=28&Submit.y=28&json=1&.cgifields=algorithm");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.queryString = qString.toString();
		return this;
	}


	/**
	 * 
	 * @return
	 */
	public boolean isFasta() {
		if (this.format == RADSQuery.FASTA)
			return true;
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isXdom() {
		if (this.format == RADSQuery.XDOM)
			return true;
		
		return false;
	}


	/**
	 * 
	 */
	private void setFormat() {
		
		StringBuilder qString = new StringBuilder();
		StringBuilder querySeq = new StringBuilder();
		
		try {
			
			FileInputStream fis = new FileInputStream(queryFile);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			String[] fields;

			while((line = br.readLine())!= null) {	
				if (line.substring(0, 1).equals(">")) {
					fields = line.split("\\s+");
					queryID = fields[0].replace(">", "");
					qString.append(line+"\n");
					continue;
				}
				
				if (this.format == -1) {
					fields = line.split("\\s+");
					
					if (fields.length == 0)
						this.format = RADSQuery.RAWSEQ;
					else if (fields.length == 1)
						this.format = RADSQuery.FASTA;
					else 
						this.format = RADSQuery.XDOM;
					
				}
				qString.append(line+"\n");
				if (this.format == RADSQuery.FASTA) 
					querySeq.append(line);
				
			}
			br.close();
			dis.close();
			fis.close();
			
			if (isFasta()) {
				generateSequenceChecksum(querySeq.toString());
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		this.queryString = qString.toString();
	}


	
	/**
	 * 
	 * @param sequence
	 */
	private void generateSequenceChecksum(String sequence) {
		try {
            MessageDigest msg = MessageDigest.getInstance("MD5");
            msg.update(sequence.getBytes(), 0, sequence.length());
            String digest = new BigInteger(1, msg.digest()).toString(16);
            this.seqChecksum = digest;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}
