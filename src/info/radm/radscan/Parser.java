package info.radm.radscan;

import info.radm.radscan.ds.Domain;
import info.radm.radscan.ds.Protein;
import info.radm.radscan.utils.MapUtilities;
import info.radm.radscan.utils.ProgressBar;
import info.radm.radscan.utils.RadsWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private ArrayList<Protein> proteins = null;
	private RADSResults results;
	private List<Map.Entry<String, Integer>> scoreTable;
	
	
	/**
	 * 
	 * @param results
	 */
	public Parser(RADSResults results) {
		this.results = results;
		this.query = results.getQuery();
		this.pbar = query.getProgressBar();
		this.maxHits = results.getHitsNumber();
		buildScoreTable();
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
	
	
	/**
	 * 
	 * @param writer
	 */
	public void parse(RadsWriter writer) {
		pbar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true, true);
		String msg = "Writing hits to file";
		if (IDonly)
			msg = "Writing IDs to file"; 
		pbar.setMessage(msg);
		pbar.setMaxVal(maxHits);
		if ( writer.isStdOutMode() )
			pbar.setQuietMode(true);
		
		BufferedReader reader = null;
		int val = 0;
		
		try {
			reader = read(results.getXdomUrl());
			String line = null;	
			while ( (line = reader.readLine()) != null) {
				
				if (line.substring(0, 1).equals(">")) {
					if ( (maxHits != -1) && (val >= maxHits) ) {
						System.err.println("MAX REACHED: "+val);
						break;
					}
					
					String[] fields = line.split("\\t");
					String pid = fields[0].replace(">", "");
					
					if (IDonly)
						writer.writeln(pid);
					else
						writer.writeln(line);
					val++;
					pbar.setVal(val);
				}
				else {
					if (IDonly)
						continue;
					writer.writeln(line);
				}
			}
			val++;
			pbar.setVal(val);
			writer.destroy();
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
	 * @return
	 */
	public ArrayList<Protein> getProteins() {
		if (proteins == null)
			buildProteinDS();
		return proteins;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public void buildProteinDS() {
		proteins = new ArrayList<Protein>();
		pbar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true, true);
		pbar.setMessage("Post-processing: Building DS");
		pbar.setMaxVal(maxHits);
		BufferedReader reader = null;
		int val = 0;
		Protein p = null;
		
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
					val++;
					pbar.setVal(val);
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
			pbar.setVal(val);
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
	private void buildScoreTable() {
		
		BufferedReader reader = null;
		String line = null;
		StringBuilder scoreLine = new StringBuilder();
		int score = 0;
		Map<String, Integer> tmpScores = new HashMap<String, Integer>();
		pbar.setMessage("Constructing score table");
		try {
			reader = read(results.getCrampageOut());	
			while ( (line = reader.readLine()) != null) {
				if (line.contains("QUERY")) {
					if (scoreLine.length() != 0)
						tmpScores.put(scoreLine.toString(), score);
					
					scoreLine = new StringBuilder();
					String[] fields = line.split("\\s+");
					scoreLine.append(fields[1]);
					scoreLine.append("\t");
				}
				else if (line.contains("SUBJECT")) {
					String[] fields = line.split("\\s+");
					scoreLine.append(fields[1]);
					scoreLine.append("\t");
				}
				else if (line.contains("RADS SCORE")) {
					String[] fields = line.split("\\s+");
					score = Integer.valueOf(fields[2]);
				}
			}
		}
		catch (IOException ioe) {
			
		}
		scoreTable = MapUtilities.sortByValue(tmpScores);
	}
	
}

	