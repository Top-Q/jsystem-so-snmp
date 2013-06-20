package systemobject.aqua.comm.snmp.analyzers;

import java.math.BigInteger;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpValueFromWalkAnalyzer extends SnmpWalkAnalyzer {

	private double tolerance = 0.0D;

	private boolean isRange = false;

	public SnmpValueFromWalkAnalyzer(String oid, Object... oidExt) {
		this(null, oid, oidExt);
	}

	public SnmpValueFromWalkAnalyzer(Long expected, String oid,
			Object... oidExt) {
		super(expected == null ? null : expected.toString(), oid, oidExt);
	}

	protected int compareCounters() {
		if (getExpected() == null) {
			return 0;
		}
		BigInteger exp = new BigInteger(getExpected());
		BigInteger act = new BigInteger(getActual());
		BigInteger dist = null;
		if (isRange) {
			dist = new BigInteger(Long.toString((long) tolerance));
		} else {
			dist = new BigInteger(Long.toString((new Double(tolerance
					* exp.doubleValue())).longValue()));
		}
		return act.compareTo(exp.add(dist));
	}

	public long getLongCounter() {
		return Long.parseLong(getActual());
	}

	public BigInteger getBigIntegerCounter() {
		return new BigInteger(getActual());
	}

	public int getIntCounter() {
		return Integer.parseInt(getActual());
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public boolean isRange() {
		return isRange;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}

}
