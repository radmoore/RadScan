package info.radm.radscan.ds;

import java.util.ArrayList;
import java.util.TreeMap;

public class Protein implements Comparable<Protein>{
	
	private String pid;
	private int length, domNo, RADSscore, RAMPAGEscore;
	private ArrayList<Domain> domains = new ArrayList<Domain>();
	private StringBuilder xdomOut;
	private final String NEW_LINE = System.getProperty("line.separator");
	private final int BEFORE = -1;
    private final int EQUAL = 0;
    private final int AFTER = 1;
	
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
	
	public String getID() {
		return this.pid;
	}
	
	public String getArrString() {
		StringBuilder arrStr = new StringBuilder();
		for (int i=0;i<domains.size();i++) {
			arrStr.append(domains.get(i).did);
			if ( !(i == domains.size()-1) )
				arrStr.append(";");
		}
		return arrStr.toString();
	}
	
	public void setRADSScore(int RADSscore) {
		this.RADSscore = RADSscore;
	}

	public void setRAMPAGEScore(int RAMPAGEscore) {
		this.RAMPAGEscore = RAMPAGEscore;
	}
	
	public int getRADSScore() {
		return this.RADSscore;
	}
	
	public int getRAMPAGEScore() {
		return this.RAMPAGEscore;
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

	public int compareTo(Protein aProtein) {
		if (this.RADSscore > aProtein.RADSscore)
			return BEFORE;
		else if (this.RADSscore < aProtein.RADSscore)
			return AFTER;
		else
			return EQUAL;
	}
	
		
}
