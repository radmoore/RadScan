package info.radm.radscan.ds;

import java.util.ArrayList;
import java.util.TreeMap;

public class Protein {
	
	String pid;
	int length, domNo;
	ArrayList<Domain> domains = new ArrayList<Domain>();
	StringBuilder xdomOut;
	private final String NEW_LINE = System.getProperty("line.separator");
	
	public Protein (String pid, int length) {
		this.pid = pid;
		this.length = length;
		this.xdomOut = new StringBuilder();
		xdomOut.append(">"+pid+"\t"+length+NEW_LINE);
	}
	
	public void addDomain(Domain d) {
		domains.add(d);
		xdomOut.append(d.toString()+NEW_LINE);
		domNo ++;
	}
	
	public int getDomainNo() {
		return domNo;
	}
	
	public ArrayList<Domain> getDomains() {
		return this.domains;
	}
	
	public String toString() {
		return xdomOut.toString();
	}
	
	public String architecture() {
		int pos = 0;
		StringBuilder arch = new StringBuilder();
		for (Domain d: domains) {
			if ( pos+1 == domains.size() ) {
				arch.append(d.did);
				break;
			}
			arch.append(d.did+";");
			pos ++;
		}
		return arch.toString();
	}
	
		
}
