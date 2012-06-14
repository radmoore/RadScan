package info.radm.radscan.utils;

/**
 * 
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class ProgressBar {

	public static int INTERMEDIATE_MODE = 0;
	public static int PROGRESSABLE_MODE = 1;
	public static char SIGN_1 = '|';
	public static char SIGN_2 = '=';
	public static char SIGN_3 = '\\';
	public static char SIGN_4 = '#';

	private int current, max, seconds, minutes, ETAsec, ETAmin;
	private long start, lastUpdate, elapsed, ETAtime;
	private String runningTime, ETAstring = "--:--", message = "";
	private boolean intermediate = false, indicate = false;
	private char indChar = SIGN_1;
	private int mode;
	private int barWidth = 75;
	private int indWidth = 5;

	
	/**
	 * 
	 * @param max
	 * @param message
	 */
	public ProgressBar(int max, String message) {
		this.message = message;
		this.max = max;
	}
	
	/**
	 * 
	 * @param max
	 * @param message
	 * @param mode
	 */
	public ProgressBar(int max, String message, int mode) {
		this.start = System.currentTimeMillis();
		setMessage(message);
		this.max = max;
		this.indicate = true;
		setProgressMode(mode);
	}
	
	public void setMaxVal(int max) {
		this.max = max;
	}
	
	/**
	 * 
	 * @param indicatorChar
	 */
	public void setIndicatorCharater(char indicatorChar) {
		this.indChar = indicatorChar;
	}
	
	/**
	 * 
	 * @param mode
	 */
	public void setProgressMode(int mode) {
		if (mode == PROGRESSABLE_MODE) { 
			this.mode = mode;
			indicate = false;
		}
		else if (mode == INTERMEDIATE_MODE)
			this.mode = mode;
		
		else
			new Exception("IllegalProgressMode");
	}
	
	/**
	 * 
	 */
	public void start() {
		startThread();
	}
	
	/**
	 * 
	 */
	public void stop() {
		// todo
	}
	
	/**
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		if (message.length() <= 35)
			this.message = String.format("%1$-" + 35 + "s", message);
		else if ( message.length() == 25 )
			this.message = message;
		else 
			new Exception("MessageOverflow");
	}
	
	/**
	 * 
	 * @param val
	 */
	public void setVal(int val) {
	  this.current = val;
      this.printProgressBar();
	}
	
	/**
	 * 
	 */
	public void finish() {
		if (intermediate) {
			indicate = false;
			return;
		}
	    this.current = this.max;
	    takeTime();
	    StringBuilder finalBar = new StringBuilder();
	    finalBar.append("|");
	    for (int i= 0; i < barWidth; i++)
	    	finalBar.append(indChar);
	    finalBar.append("|");
	    if (mode == PROGRESSABLE_MODE) {
	    	finalBar.append(" 100% [Total: "+runningTime+"]     ");
	    	System.out.println(message+": "+finalBar);
	    }
	}
	
	
	/**
	 * 
	 */
	private void printInterBar() {
		int indSize = 10;
		int pos = 0;
		int start = 0;
		StringBuilder iPbar = new StringBuilder();
	  
		while (indicate) {
			takeTime();
			if (pos >= barWidth) {
				start = 0;
				pos = 0;
				indSize = 10;
			} 
			if (indSize < 10) {
				start = 10 - indSize;
				for (int i = 0; i < start; i++) {
					iPbar.append(indChar);
				 }
			}
			if (pos + indSize > barWidth)
				indSize -= 1;
			  
			for (int i = start; i < pos; i++) {
				 iPbar.append(" ");
			}
			for (int i = pos; i < pos+indSize; i++) {
				 iPbar.append(indChar);
			}
			for (int i = pos+indSize; i < barWidth; i++) {
				 iPbar.append(" ");
			}

			System.out.print(message+": |"+iPbar+"| ["+runningTime+"]");
			System.out.print("\r");
			iPbar = new StringBuilder();
			pos += 1;
			try {
				 Thread.sleep(25);
			} 
			catch (InterruptedException e) {
				 e.printStackTrace();
			}
		}	
	}
	
	
	/**
	 * 
	 *
	 */
	private void printProgressBar() {
	    
		double numbar = Math.floor(75*(double)current/(double)max);
		double progress = Math.floor(100*((double)current/(double)max));
	    StringBuilder strbar = new StringBuilder();
	    int i = 0;
	    
	    for(i = 0; i < numbar; i++)
	    	strbar.append(indChar);
	    
	    for(i = (int)numbar; i < 75; i++)
	    	strbar.append(" ");
	    
	    takeTime(); 
	    System.out.print(message+": |"+strbar+"| "+(int)progress+"% complete     "); 
		System.out.print("\r");
	}

	/**
	 * 
	 */
	private void takeTime() {
	    elapsed = (System.currentTimeMillis() - this.start);
	    seconds = (int)(elapsed / 1000)%60;
	    minutes = (int)(elapsed / 1000)/60;
    	ETAtime = elapsed * (long)((double)max/(double)current);
    	ETAsec = (int)(ETAtime /1000)%60;
    	ETAmin = (int)(ETAtime /1000)/60;
    	ETAstring = String.format("%02d",ETAmin)+":"+String.format("%02d",ETAsec);
    	runningTime = String.format("%02d",minutes)+":"+String.format("%02d",seconds);
	}
	
	
	/**
	 * 
	 */
	private void startThread() {
		new Thread() {
			public void run() {
				try {
					if (mode == INTERMEDIATE_MODE)
						printInterBar();
					else
						printProgressBar();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
