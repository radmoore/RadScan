package info.radm.radscan.utils;


public class ProgressBar {

	public static int INTERMEDIATE_MODE = 0;
	public static int PROGRESSABLE_MODE = 1;
	public static char SIGN_1 = '=';
	public static char SIGN_2 = '|';
	public static char SIGN_3 = '\\';
	public static char SIGN_4 = '#';

	private int current, max;
	private long start, lastUpdate;
	private String message;
	private boolean intermediate = false, indicate = false;
	private char indChar = SIGN_1;
	private int mode = PROGRESSABLE_MODE;
	

	
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
	 * @throws Exception 
	 */
	public ProgressBar(int max, String message, int mode) {
		this.start = System.currentTimeMillis();
		this.message = message;
		this.max = max;
		this.indicate = true;
		setProgressMode(mode);
	}
	
	
	public void setIndicatorCharater(char indicatorChar) {
		this.indChar = indicatorChar;
	}
	
	/**
	 * 
	 * @param mode
	 */
	public void setProgressMode(int mode) {
		if ( (mode == INTERMEDIATE_MODE) || (mode == PROGRESSABLE_MODE) )
			this.mode = mode;
//		else
//			throw new Exception("IllegalProgressMode");
	}
	
	
	public void start() {
		startThread();
	}
	
	
	public void stop() {
		// todo
	}
	
	
	public void changeMessage(String message) {
		this.message = message;
	}
	
	
	public void setVal(int i) {
	  this.current = i;
	  if ( (System.currentTimeMillis() - this.lastUpdate) > 1000 ){
		this.lastUpdate = System.currentTimeMillis();
	    //this.printBar(false);
	  }
	}
	
	
	public void finish() {
		if (intermediate) {
			indicate = false;
			return;
		}	
	    this.current = this.max;
	    //this.printBar(true);
	}
	
	
	private void printInterBar() {
		int end = 75;
		int indSize = 10;
		int pos = 0;
		int start = 0;
		  
		StringBuilder iPbar = new StringBuilder();
		  
		while (indicate) {

			if (pos >= end) {
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
			if (pos + indSize > end)
				indSize -= 1;
			  
			for (int i = start; i < pos; i++) {
				 iPbar.append(" ");
			}
			for (int i = pos; i < pos+indSize; i++) {
				 iPbar.append(indChar);
			}
			for (int i = pos+indSize; i < end; i++) {
				 iPbar.append(" ");
			}

			System.out.print(message+":  |"+iPbar+"| [ETA: --:--]");
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
	
	
	private void printProgressBar() {
	    
		double numbar= Math.floor(75*(double)current/(double)max);
	    String strbar = "";
	    int ii = 0;
	    
	    for(ii = 0; ii < numbar; ii++){
	    	strbar += "=";
	    }
	    
	    for(ii = (int)numbar; ii < 75; ii++){
	    	strbar += " ";
	    }
	    
	    long elapsed = (System.currentTimeMillis() - this.start);
	    int seconds = (int)(elapsed / 1000)%60;
	    int minutes = (int)(elapsed / 1000)/60;
	    String strend = String.format("%02d",minutes)+":"+String.format("%02d",seconds);
	
	    String strETA = "";
	    if (elapsed < 2000){
	    	strETA = "--:--";
	    }
	    else{
	    	long timeETA = elapsed * (long)((double)max/(double)current);
	    	int ETAseconds = (int)(timeETA /1000)%60;
	    	int ETAminutes = (int)(timeETA /1000)/60;
	    	strETA = String.format("%02d",ETAminutes)+":"+String.format("%02d",ETAseconds);
	    }
	    
//	    if(finished)
//	    	strend = "Finished: "+strend+"               ";
//	    else
//	    	strend = "Elapsed: "+strend+" ETA: "+strETA+"   ";
	    
	    System.out.print("|"+strbar+"| "+strend);
	    
//	    if(finished)
//	    	System.out.print("\n");
//	    else
//	    	System.out.print("\r");
		    
	}

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
