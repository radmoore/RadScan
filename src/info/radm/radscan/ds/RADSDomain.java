package info.radm.radscan.ds;

public class RADSDomain implements Comparable<RADSDomain>{
	
	protected String did, comment = null;
	protected int from, to;
	protected double evalue = -1;
	private final int BEFORE = -1;
    private final int EQUAL = 0;
    private final int AFTER = 1;
	
	public RADSDomain (String did, int from, int to) {
		this.did = did;
		this.from = from;
		this.to = to;
	}
	
	public String getID() {
		return this.did;
	}
	
	public void addComment(String comment) {
		this.comment = comment;
	}
	
	public void addEvalue(double evalue) {
		this.evalue = evalue;
	}
	
	public String toString() {
		StringBuilder outString = new StringBuilder();
		outString.append(this.from+"\t");
		outString.append(this.to+"\t");
		outString.append(this.did);
		
		if (evalue != -1)
			outString.append("\t"+evalue);
		
		if (comment != null)
			outString.append("\t;"+comment);
		
		return outString.toString();
		
	}
	
	public boolean overlaps(RADSDomain nextDom) {
		if (this.to >= nextDom.from)
			return true;
		return false;
	}
	
	public int compareTo(RADSDomain other) {
		if (this.from < other.from)
			return BEFORE;
		else if (this.from > other.from)
			return AFTER;
		else
			return EQUAL;
		
	}
	
	public int getFrom() {
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	public double getEvalue() {
		return this.evalue;
	}
}
