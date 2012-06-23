package info.radm.radscan.ds;

public class Domain {
	
	String did, comment = null;
	int from, to;
	double evalue = -1;
	
	public Domain (String did, int from, int to) {
		this.did = did;
		this.from = from;
		this.to = to;
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
}
