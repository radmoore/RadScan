package info.radm.radscan.utils;

import java.lang.Math.*;

public class ProgressBar {


	private int current, max;
	private long start, lastUpdate;
	private String name;
	private boolean intermediate = false, indicate = false;
	private char indChar = SIGN_1;
	
	public static char SIGN_1 = '=';
	public static char SIGN_2 = '|';
	public static char SIGN_3 = '\\';
	public static char SIGN_4 = '#';
	
	public ProgressBar(int max, String name) {
	  this.start = System.currentTimeMillis();
	  this.name = name;
	  this.max = max;
	  System.out.println(this.name+":");
	  this.printBar(false);
	}
	  
	public ProgressBar(int max, String name, boolean intermediate) {
	  this.start = System.currentTimeMillis();
	  this.name = name;
	  this.max = max;
	  //System.out.println(this.name+":");
	  this.indicate = true;
	}
	
	public void setIndicatorCharater(char indicatorChar) {
		this.indChar = indicatorChar;
	}
	
	public void startIntermediate() {
		printInterBar();
	}
	
	public void stopIntermediate() {
		indicate = false;
	}
	
	public void setVal(int i) {
	  this.current = i;
	  if ( (System.currentTimeMillis() - this.lastUpdate) > 1000 ){
		this.lastUpdate = System.currentTimeMillis();
	    this.printBar(false);
	  }
	}
	
	  public void finish() {
		if (intermediate) {
			indicate = false;
			return;
		}	
	    this.current = this.max;
	    this.printBar(true);
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
			  
			  if (pos + indSize > end)  {
				  indSize -= 1;
			  }
			  
			  for (int i = start; i < pos; i++) {
				  iPbar.append(" ");
			  }
			  
			  for (int i = pos; i < pos+indSize; i++) {
				  iPbar.append(indChar);
			  }
			  
			  for (int i = pos+indSize; i < end; i++) {
				  iPbar.append(" ");
			  }

			  System.out.print(name+":  |"+iPbar+"| [ETA: --:--]");
			  System.out.print("\r");
			  iPbar = new StringBuilder();
			  pos += 1;
			  try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		  }
		  
		  
		  
	  }
	
	  private void printBar(boolean finished) {
	    
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
	    
	    if(finished)
	      strend = "Finished: "+strend+"               ";
	    else
	      strend = "Elapsed: "+strend+" ETA: "+strETA+"   ";
	    
	    System.out.print("|"+strbar+"| "+strend);
	    
	    if(finished)
	      System.out.print("\n");
	    else
	      System.out.print("\r");
		    
		  }
}
