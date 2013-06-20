package systemobject.aqua.comm.snmp.analyzers;

import org.snmp4j.smi.Integer32;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpInt32ValueAnalyzer extends SnmpValueAnalyzer {

	public SnmpInt32ValueAnalyzer(int expected) {
		this(new Integer32(expected));
	}

	public SnmpInt32ValueAnalyzer(Integer32 expected) {
		super(expected);
	}

	protected String getAnalyzerTypeStr() {
		return "Integer32 ";
	}

}
