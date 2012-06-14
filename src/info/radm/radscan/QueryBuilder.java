package info.radm.radscan;

import info.radm.radscan.utils.RadsMessenger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class QueryBuilder implements RADSQuery{

	private String queryString = null, queryID = null, querySequence = null;
	private int format = -1;
	private File queryFile;
	private boolean verbose = false, quiet = false;
	private long seqChecksum;
	
	
	/**
	 * 
	 * @param queryFile
	 */
	public QueryBuilder(String queryFilePath) {
		this.queryFile = new File(queryFilePath);
		setFormat();
	}
	
	/**
	 * 
	 * 
	 */
	public QueryBuilder() {	}
	
	
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
	 * @param verbose
	 */
	public void setVerboseMode(boolean verbose) {
		this.verbose = verbose;
	}
	
	/**
	 * 
	 * @param quiet
	 */
	public void setQuietMode(boolean quiet) {
		this.quiet = quiet;
		this.verbose = false;
	}
	
	
	/**
	 * 
	 */
	public RADSQuery build() {
		StringBuilder qString = new StringBuilder();
		qString.append(RADSRunner.RADSBaseUrl);
		try {
			qString.append("apicall=1&algorithm=rads&gp_rampage_M=150&gp_rampage_m=-100" +
				"&gp_rampage_G=-50&gp_rampage_g=-25&gp_rampage_T=-100&gp_rampage_t=-50" +
				"&matrix=BLOSUM62&gp_rampage_I=-10&gp_rampage_i=-1&gp_rampage_E=0"+
				"&gp_rampage_e=0&query=" + URLEncoder.encode(queryString, "utf8") +
				"&db=simap26&Submit.x=28&Submit.y=28&json=1&.cgifields=algorithm");
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
	public String getFileName() {
		return queryFile.getName();
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
	public String getQueryString() {
		return queryString;
	}
	
	
	/**
	 * 
	 */
	private void setFormat() {
		
		StringBuilder qString = new StringBuilder();
		StringBuilder querySeq = new StringBuilder();
		
		if (verbose) {
			RadsMessenger.writeMessage("RADS");
			RadsMessenger.writeMessage("Rapid Alignment Domain Search - find proteins with similar architectures");
			RadsMessenger.printHR();
			
		}
		
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
					if (verbose) {
						RadsMessenger.writeTable("INPUT FILE", queryFile.getName());
						RadsMessenger.writeTable("QUERY PROTEIN", queryID);
					}
					qString.append(line+"\n");
					continue;
				}
				
				if (this.format == -1) {
					fields = line.split("\\s+");
					
					if (fields.length == 1) {
						this.format = RADSQuery.FASTA;
						if (verbose)
							RadsMessenger.writeTable("FORMAT", "FASTA");
					}
					else {
						this.format = RADSQuery.XDOM;
						if (verbose)
							RadsMessenger.writeTable("FORMAT", "XDOM");
					}
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
				RadsMessenger.writeTable("SEQUENCE CHECKSUM", ""+this.seqChecksum);
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

	public String getQueryID() {
		return this.queryID;
	}
	
	
	private void generateSequenceChecksum(String sequence) {
		try {
			Checksum checksum = new CRC32();
			byte[] msgByte = sequence.getBytes("UTF-8");
			checksum.update(msgByte,0,msgByte.length);
			this.seqChecksum = checksum.getValue();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
