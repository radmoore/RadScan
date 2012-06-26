package info.radm.radscan;

import info.radm.radscan.utils.ProgressBar;
import info.radm.radscan.utils.RadsMessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class RADSRunner{

	RADSQuery query;
	public static String RADSBaseUrl = "http://rads-dev.uni-muenster.de/rads3.pl?";
	private static int INTERVAL = 5000;
	private boolean running = false;
	private ProgressBar pBar;
	private int state = 1;
	private String jobId;
	private boolean quiet = false, benchmarkMode = false;
	
	
	public RADSRunner(RADSQuery query) {
		this.query = query;
		this.quiet = query.isQuiet();
		this.pBar = query.getProgressBar();
		pBar.setQuietMode(quiet);
	}
	
	public void setBenchmarkMode(boolean mode) {
		this.benchmarkMode = mode;
	}
	
	public RADSResults submit() {
		
		String xdomURL = null; 
		BufferedReader reader = null;
		RADSResults results = new RADSResults(query);
		boolean has_error = false;
		
		if (!quiet)
			pBar.setMessage("Establishing RADS connection");
		
		try {
			if (!quiet)
				pBar.start();
			
			reader = read( this.query.getQueryString() );
			String line = null;
			line = reader.readLine();
			running = true;
			while (line != null) {
				
				line = reader.readLine();
				
		        if (line.contains("error"))
		            has_error = true;
		      
		        if (line.contains("message") && has_error) {
		        	String[] fields = line.split(": ");
		            String msg = fields[1].replace("\"", "");
		            pBar.finish(true);
		            System.err.println("ERROR: "+msg);
		            System.exit(-1);
		        }

				if (line.contains("preparing")) {
					if (!quiet)
						pBar.setMessage("RADS: submitting job");
				}

				if (line.contains("\"jobid\":")) {
					String[] fields = line.split("\\s+");
					jobId = fields[2].replace("\"", "");
					jobId = jobId.replace(",", "");
				}
				if (line.contains("full_url")) {
					String[] fields = line.split("\\s+");
					String jobUrl = fields[2].replace("\"", "");
					if (!quiet)
						RadsMessenger.writeTable("JOB ID", jobId);
					results = intervalCheck(jobUrl, results);
					break;
				}
			}
			reader.close();
		}
		catch (MalformedURLException me) {
			me.printStackTrace();
			semiGracefulExit();
		}
		catch (IOException e) {
			e.printStackTrace();
			semiGracefulExit();
		}
		
		return results;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	//TODO
//	public RADSResults lookupJob(String jobid) {
//
//	}
	
	
	/**
	 * 
	 * @param jobURL
	 * @return
	 */
	private RADSResults intervalCheck(String jobURL, RADSResults results) {
	
		BufferedReader reader = null;
		String rawRunTime;
		boolean has_error = false;

		if (!quiet)
			pBar.setMessage("RADS: search running");
		int hits = 0;
		try {
			Thread.sleep(INTERVAL);	
			reader = read( jobURL );
			String line = null;
			running = true;
			while ( (line = reader.readLine()) != null) {
				
				if (line.contains("running")) {
					reader.close();
					intervalCheck(jobURL, results);
					break;
				}
				
				if (line.contains("runtime") && query.isBenchmarking()) {
					String[] fields = line.split(": ");
		            String runtime = fields[1].replace("\"", "");
		            runtime = runtime.replace(",", "");
					System.out.println("Runtime: "+runtime+"s");
					System.exit(0);
				}
				
		        if (line.contains("error"))
		            has_error = true;
		      
		        if (line.contains("message") && has_error) {
		        	String[] fields = line.split(": ");
		            String msg = fields[1].replace("\"", "");
		            pBar.finish(true);
		            System.err.println("ERROR: "+msg);
		            System.exit(-1);
		        }
				
				if (line.contains("runtime")) {
					String[] fields = line.split("\\s+");
					String runtime = fields[2].replace("\"", "");
					runtime = runtime.replace(",", "");
					results.setRawRuntime(runtime+"s");
					if (benchmarkMode) {
						System.err.println("Runtime: "+runtime);
						System.exit(0);
					}
				}
				
				if (line.contains("hits_n")) {
					String[] fields = line.split("\\s+");
					String hitsString = fields[2].replace("\"", "");
					hitsString = hitsString.replace(",", "");
					hits = Integer.valueOf(hitsString);
					results.setNumHits(hits);
					
					if (!quiet)
						pBar.setMessage("RADS: "+hits+" found");
				}
				if (line.contains("xdom_url")) {
					String[] fields = line.split("\\s+");
					String xdomURL = fields[2].replace("\"", "");
					xdomURL = xdomURL.replace(",", "");
					results.setXdomUrl(xdomURL);
					break;
				}
			}
			reader.close();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	private BufferedReader read(String url) throws IOException, MalformedURLException{
		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));}


	private void semiGracefulExit() {
		running = false;
		System.err.println("Fatal error. Exiting");
		System.exit(-1);
	}

}
