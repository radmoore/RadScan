package info.radm.radscan.ds;

import info.radm.radscan.utils.MapUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class Protein implements Comparable<Protein>{
	
	private String pid;
	private int length, domNo, RADSscore, RAMPAGEscore;
	private ArrayList<Domain> domains = new ArrayList<Domain>();
	private final String NEW_LINE = System.getProperty("line.separator");
	private final int BEFORE = -1;
    private final int EQUAL = 0;
    private final int AFTER = 1;
    
	public Protein (String pid, int length) {
		this.pid = pid;
		this.length = length;
	}
	
	public void addDomain(Domain d) {
		domains.add(d);
		domNo ++;
	}
	
	public void setRADSScore(int RADSscore) {
		this.RADSscore = RADSscore;
	}

	public void setRAMPAGEScore(int RAMPAGEscore) {
		this.RAMPAGEscore = RAMPAGEscore;
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

	public int getRADSScore() {
		return this.RADSscore;
	}
	
	public int getRAMPAGEScore() {
		return this.RAMPAGEscore;
	}
	
	public int getDomainNo() {
		return domNo;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public ArrayList<Domain> getDomains() {
		return this.domains;
	}
	
	public String toString() {
		StringBuilder xdom = new StringBuilder();
		xdom.append(">"+pid+"\t"+length+NEW_LINE);
		for (Domain d : domains)
			xdom.append(d.toString()+NEW_LINE);
		return xdom.toString();
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
	
	
	public void collapse(int repno) {
		ArrayList<Domain> collpasedDomains = new ArrayList<Domain>();
		
		Domain lastDom = null;
		int repLength = 1;
		for (Domain cDom: domains) {
			
			if (lastDom == null) {
				lastDom = cDom;
				continue;
			}
			
			if (lastDom.did.equals(cDom.did))
				repLength += 1;
			
			else {
				if (repLength >= repno) {
					Domain firstRepDom = domains.get(domains.indexOf(cDom)-repLength);
					Domain d = new Domain(lastDom.did, firstRepDom.from, lastDom.to);
					d.addComment("collapsed "+repLength+" instances");
					d.addEvalue(-1);
					collpasedDomains.add(d);
				}
				else {
					collpasedDomains.add(lastDom);
				}
				repLength = 1;
			}
			lastDom = cDom;
		}
		collpasedDomains.add(lastDom);
		this.domains = collpasedDomains;
	}
	
	
	
	public static List<Entry<String, Integer>> getUniqueArchitectures(TreeSet<Protein> proteins) {
		Map<String, Integer> uniqueArrs = new HashMap<String, Integer>();
		for (Protein p : proteins) {
			Integer freq = uniqueArrs.get(p.getArrString());
			if (freq == null)
				uniqueArrs.put(p.getArrString(), 1);
			else
				uniqueArrs.put(p.getArrString(), freq+1);
		}
		return MapUtilities.sortByValue(uniqueArrs);	
		
	}
		
}
