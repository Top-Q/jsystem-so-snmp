package systemobject.aqua.comm.snmp.analyzers;

import org.snmp4j.smi.Gauge32;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpGauge32ValueAnalyzer extends SnmpValueAnalyzer {

	public SnmpGauge32ValueAnalyzer(int expected) {
		this(new Gauge32(expected));
	}

	public SnmpGauge32ValueAnalyzer(Gauge32 expected) {
		super(expected);
	}

	protected String getAnalyzerTypeStr() {
		return "Gauge32 ";
	}

}
