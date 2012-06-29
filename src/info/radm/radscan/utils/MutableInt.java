package info.radm.radscan.utils;

public class MutableInt extends Number implements Comparable<Integer> {

	private static final long serialVersionUID = 1L;
	final int BEFORE = -1;
	final int EQUAL = 0;
	final int AFTER = 1;
	int value = 1;

	public void inc() { value++; }
	public int  get() { return value; }
	public double doubleValue() { return value; }
	public float floatValue() { return value; }
	public int intValue() { return value; }
	public long longValue() { return value; }
	

	public int compareTo(Integer i) {
		if (i == this.intValue())
			return EQUAL;
		if (i < this.intValue())
			return BEFORE;
		
		return 1;
	}
  
}
