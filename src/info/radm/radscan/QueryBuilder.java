package info.radm.radscan;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryBuilder implements RADSQuery{

	private String queryString = null, queryID = null, querySequence = null;
	private int format = -1;
	private File queryFile;
	
	
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
			// TODO Auto-generated catch block
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
					if (fields.length == 1) {
						this.format = RADSQuery.FASTA;
					}
					else 
						this.format = RADSQuery.XDOM;
				}
				qString.append(line+"\n");
				
			}
			br.close();
			dis.close();
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.queryString = qString.toString();
	}

	public String getQueryID() {
		return this.queryID;
	}
	

	
	
	
}
