package info.radm.radscan;

import info.radm.pbar.ProgressBar;
import info.radm.radscan.ds.Domain;
import info.radm.radscan.ds.Protein;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */

public class Parser {
	
	private ProgressBar pbar;
	private RADSQuery query;
	private int maxHits = -1;
	private boolean IDonly = false;
	//private TreeMap<String, Protein> proteins = null;
	private RADSResults results;
	private List<Map.Entry<String, Integer>> scoreTable;
	private HashMap<String, Integer> radsScores;
	private HashMap<String, Integer> rampageScores;
	private TreeSet<Protein> proteins = null;

	
	/**
	 * 
	 * @param results
	 */
	public Parser(RADSResults results) {
		this.results = results;
		this.query = results.getQuery();
		this.pbar = query.getProgressBar();
		this.maxHits = results.getHitsNumber();
	}
	
	
	/**
	 * 
	 * @param maxHits
	 */
	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}
	
	
	/**
	 * 
	 * @param IDonly
	 */
	public void setIDonlyMode(boolean IDonly) {
		this.IDonly = IDonly;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<Map.Entry<String, Integer>> getScoreTable() {
		return this.scoreTable;
	}
	
	
	public TreeSet<Protein> parse() {
		readScoreTable();
		readHits();
		return proteins;
	}

	/**
	 * 
	 * @return
	 */
	private void readHits() {
		
		pbar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true);
		pbar.setMessage("Extracting hits");
		pbar.setMaxVal(maxHits);
		BufferedReader reader = null;
		int val = 0;
		Protein p = null;
		proteins = new TreeSet<Protein>();
		
		try {
			reader = read(results.getXdomUrl());
			String line = null;
			
			while ( (line = reader.readLine()) != null) {
				if (line.substring(0, 1).equals(">")) {
					
					if ( (maxHits != -1) && (val >= maxHits) )
						break;
					
					String[] fields = line.split("\\t");
					String pid = fields[0].replace(">", "");
					
					if (p != null)
						proteins.add(p);
					
					p = new Protein( pid, Integer.valueOf(fields[1]) );
					p.setRADSScore(radsScores.get(pid));
					//TODO: implement STATIC varaible for Algorithm
					if (query.getAlgorithm() == "RAMPAGE")
						p.setRAMPAGEScore(rampageScores.get(pid));
					val++;
					pbar.setCurrentVal(val);
				}
				else {
					String[] fields = line.split("\\t");
					Domain d = new Domain(fields[2], 
							Integer.valueOf(fields[0]), 
							Integer.valueOf(fields[1]));
					if (fields.length == 4)
						d.addEvalue(Double.valueOf( fields[3]) );
					p.addDomain(d);
				}
			}
			proteins.add(p);
			val++;
			pbar.setCurrentVal(val);
			pbar.finish(true);
				
		}
		catch(MalformedURLException mue) {
			mue.printStackTrace();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private BufferedReader read(String url) throws IOException, MalformedURLException{
		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));}
	
	
	
	/**
	 * 
	 */
	private void readScoreTable() {
		
		BufferedReader reader = null;
		String subject = null;
		String line = null;
		int radsScore = 0, rampageScore = 0;
		radsScores = new HashMap<String, Integer>();
		rampageScores = new HashMap<String, Integer>();
		
		
		pbar.setMessage("Reading score table");
		
		try {
			reader = read(results.getCrampageOut());
			
			while ( (line = reader.readLine()) != null) {
			
				if (line.contains("QUERY")) {
					if (subject != null) {
						radsScores.put(subject, radsScore);
						rampageScores.put(subject, rampageScore);
					}
				}
				else if (line.contains("SUBJECT")) {
					String[] fields = line.split("\\s+");
					subject = fields[1];
				}
				else if (line.contains("RADS SCORE")) {
					String[] fields = line.split("\\s+");
					radsScore = Integer.valueOf(fields[2]);
				}
				else if (line.contains("RAMPAGE SCORE")) {
					String[] fields = line.split("\\s+");
					rampageScore = Integer.valueOf(fields[2]);
				}
			}
			if (subject != null) {
				radsScores.put(subject, radsScore);
				rampageScores.put(subject, rampageScore);
			}
		}
		catch (IOException ioe) {
			
		}
	}
	
}

	