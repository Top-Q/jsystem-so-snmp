package systemobject.aqua.comm.snmp.analyzers;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Variable;

import systemobject.aqua.comm.snmp.constant.SnmpError;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpValueAnalyzer extends AnalyzerParameterImpl {

	private Variable expected = null;

	public SnmpValueAnalyzer(Variable expected) {
		this.expected = expected;
	}

	public void setTestAgainst(Object o) {
		if (o != null) {
			if (o instanceof ResponseEvent) {
				o = ((ResponseEvent) o).getResponse();
			}
		}
		super.setTestAgainst(o);
	}

	public Class<?> getTestAgainstType() {
		return PDU.class;
	}

	public void analyze() {
		PDU actual = (PDU) testAgainst;
		title = "Snmp " + getAnalyzerTypeStr()
				+ "Value Analyzer: Value expected = " + expected.toString()
				+ ", actual = " + actual.get(0).getVariable().toString();

		if (actual.getErrorIndex() != SnmpError.NO_ERROR.value()
				|| actual.getErrorStatus() != SnmpError.NO_ERROR.value()) {
			title = title + " [ Error=" + actual.getErrorStatusText() + " ]";
			status = false;
		} else {
			status = this.expected.equals(actual.get(0).getVariable());
		}
	}

	protected String getAnalyzerTypeStr() {
		return "";
	}

	public Variable getExpected() {
		return expected;
	}

	public void setExpected(Variable expected) {
		this.expected = expected;
	}

	public PDU getActual() {
		return (PDU) testAgainst;
	}

}
