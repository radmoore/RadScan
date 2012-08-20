package info.radm.radscan.ds;

import info.radm.radscan.utils.MapUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class RADSProtein implements Comparable<RADSProtein>{
	
	private String pid;
	private int length, domNo, RADSscore, RAMPAGEscore;
	private ArrayList<RADSDomain> domains = new ArrayList<RADSDomain>();
	private final String NEW_LINE = System.getProperty("line.separator");
	private final int BEFORE = -1;
    private final int EQUAL = 0;
    private final int AFTER = 1;
    
	public RADSProtein (String pid, int length) {
		this.pid = pid;
		this.length = length;
	}
	
	public void addDomain(RADSDomain d) {
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
	
	public ArrayList<RADSDomain> getDomains() {
		return this.domains;
	}
	
	public String toString() {
		StringBuilder xdom = new StringBuilder();
		xdom.append(">"+pid+"\t"+length+NEW_LINE);
		for (RADSDomain d : domains)
			xdom.append(d.toString()+NEW_LINE);
		return xdom.toString();
	}
	
	public String architecture() {
		int pos = 0;
		StringBuilder arch = new StringBuilder();
		for (RADSDomain d: domains) {
			if ( pos+1 == domains.size() ) {
				arch.append(d.did);
				break;
			}
			arch.append(d.did+";");
			pos ++;
		}
		return arch.toString();
	}

	public int compareTo(RADSProtein aProtein) {
		if (this.RADSscore > aProtein.RADSscore)
			return BEFORE;
		else if (this.RADSscore < aProtein.RADSscore)
			return AFTER;
		else
			return EQUAL;
	}
	
	
	public void collapse(int repNo) {
		ArrayList<RADSDomain> collpasedDomains = new ArrayList<RADSDomain>();
		ArrayList<RADSDomain> domainHolding = new ArrayList<RADSDomain>();
		
		RADSDomain lastDom = null;

		for (RADSDomain cDom: domains) {
			if (lastDom == null) {
				domainHolding.add(cDom);
				lastDom = cDom;
				continue;
			}
			
			if (lastDom.did.equals(cDom.did))
				domainHolding.add(cDom);
			
			else {
				if ( domainHolding.size() >= repNo ) {
					RADSDomain firstRepDom = domainHolding.get(0);
					RADSDomain d = new RADSDomain(firstRepDom.did, firstRepDom.from, lastDom.to);
					d.addComment("collapsed "+domainHolding.size()+" instances");
					d.addEvalue(-1);
					collpasedDomains.add(d);
				}
				else {
					for (RADSDomain d : domainHolding)
						collpasedDomains.add(d);
				}
				domainHolding.clear();
				domainHolding.add(cDom);
			}
			lastDom = cDom;

		}
		if ( domainHolding.size() >= repNo ) {
			RADSDomain firstRepDom = domainHolding.get(0);
			RADSDomain d = new RADSDomain(lastDom.did, firstRepDom.from, lastDom.to);
			d.addComment("collapsed "+domainHolding.size()+" instances");
			d.addEvalue(-1);
			collpasedDomains.add(d);
		}
		else {
			for (RADSDomain d : domainHolding)
				collpasedDomains.add(d);
		}
		this.domains = collpasedDomains;
	}
	
	
	
	public static List<Entry<String, Integer>> getUniqueArchitectures(TreeSet<RADSProtein> proteins) {
		Map<String, Integer> uniqueArrs = new HashMap<String, Integer>();
		for (RADSProtein p : proteins) {
			Integer freq = uniqueArrs.get(p.getArrString());
			if (freq == null)
				uniqueArrs.put(p.getArrString(), 1);
			else
				uniqueArrs.put(p.getArrString(), freq+1);
		}
		return MapUtilities.sortByValue(uniqueArrs);	
		
	}
		
}
