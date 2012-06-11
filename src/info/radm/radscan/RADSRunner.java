package info.radm.radscan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class RADSRunner {

	RADSQuery query;
	public static String RADSBaseUrl = "http://localhost/rads3.pl?";
	private static int INTERVAL = 10000;
	private boolean running = false;
	
	
	public RADSRunner(RADSQuery query) {
		this.query = query;
	}
	
	public void submit() {
		
		BufferedReader reader = null;
		
		try {
		//	Thread.sleep(5000);
			reader = read( this.query.getQueryString() );
			String line = null;
			line = reader.readLine();
			running = true;
			while (line != null) {
				//System.out.println(line);
				line = reader.readLine();
				if (line.contains("preparing")) {
					System.out.print("RADS is preparing input query... ");
				}
				if (line.contains("running")) {
					System.out.println("done.");
				}
				if (line.contains("jobid: ")) {
					String[] fields = line.split("\\s+");
					String jid = fields[2].replace("\"", "");
					System.out.println("RADS job is sibmitted [JOBID:" +jid +"]");
				}
				if (line.contains("full_url")) {
					System.out.println(line);
					String[] fields = line.split("\\s+");
					String jobUrl = fields[2].replace("\"", "");
					System.out.println("Checking in intervals with this url: "+jobUrl);
					intervalCheck(jobUrl);
				}
			}
		}
		catch (MalformedURLException me) {
			me.printStackTrace();
			semiGracefulExit();
		}
		catch (IOException e) {
			e.printStackTrace();
			semiGracefulExit();
		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 

	}
	
	public boolean isRunning() {
		return running;
	}
	
	
	private void intervalCheck(String jobURL) {
		
		
		BufferedReader reader = null;
		
		try {
			Thread.sleep(INTERVAL);	
			reader = read( jobURL );
			String line = null;
			line = reader.readLine();
			running = true;
			while (line != null) {
				//System.out.println(line);
				line = reader.readLine();
				if (line.contains("running")) {
					System.out.println("RADS job is still running. Will check again in 5.");
					reader.close();
					intervalCheck(jobURL);
				}
				if (line.contains("complete")) {
					System.out.println("JOB IS COMPLETE!");
					
				}
				if (line.contains("hits_n")) {
					String[] fields = line.split("\\s+");
					String hits = fields[2].replace("\"", "");
					System.out.println("Number of hits: "+hits);
				}
				if (line.contains("xdom_url")) {
					String[] fields = line.split("\\s+");
					String xdomfile = fields[2].replace("\"", "");
					System.out.println("Results file present under: "+xdomfile);
					System.exit(0);
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private BufferedReader read(String url) throws IOException, MalformedURLException{
		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));}


	private void semiGracefulExit() {
		running = false;
		System.err.println("Fatal error. Exiting");
		System.exit(-1);
	}



	
	
}
