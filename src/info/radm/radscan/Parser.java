package info.radm.radscan;

import info.radm.radscan.ds.Domain;
import info.radm.radscan.ds.Protein;
import info.radm.radscan.utils.ProgressBar;
import info.radm.radscan.utils.RadsWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */

public class Parser {
	
	private String xdomURL;
	private ProgressBar pbar;
	private RADSQuery query;
	private int maxHits = -1;
	private boolean IDonly = false;
	private ArrayList<Protein> proteins = null;
		
	
	/**
	 * 
	 * @param results
	 */
	public Parser(RADSResults results) {
		this.xdomURL = results.getXdomUrl();
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
	 * @param writer
	 */
	public void parse(RadsWriter writer) {
		pbar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true, true);
		pbar.setMessage("Writing hits to file");
		pbar.setMaxVal(maxHits);
		if ( writer.isStdOutMode() )
			pbar.setQuietMode(true);
		
		BufferedReader reader = null;
		int val = 0;
		
		try {
			reader = read(xdomURL);
			String line = null;	
			while ( (line = reader.readLine()) != null) {
				
				if (line.substring(0, 1).equals(">")) {
					if ( (maxHits != -1) && (val >= maxHits) )
						break;
					
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
			reader = read(xdomURL);
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
	
	
}

	