package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;
import org.snmp4j.smi.Variable;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpValidatingGetFailed extends SnmpOperationFailed {

	private static final long serialVersionUID = 8392238657035633320L;

	private Variable[] expected = null;

	private Variable actual = null;

	public SnmpValidatingGetFailed(MibReader reader, Variable[] expected,
			Variable actual) {
		this(reader, expected, actual, new StringBuilder(""));
	}

	public SnmpValidatingGetFailed(MibReader reader, Variable[] expected,
			Variable actual, StringBuilder addInfo) {
		this(null, reader, expected, actual, addInfo);
	}

	public SnmpValidatingGetFailed(MibReader reader, Variable[] expected,
			Variable actual, StringBuilder addInfo, Throwable cause) {
		this(null, reader, expected, actual, addInfo, cause);
	}

	public SnmpValidatingGetFailed(PDU sentPdu, MibReader reader,
			Variable[] expected, Variable actual, Throwable cause) {
		super(null, sentPdu, reader, cause);
		this.expected = expected;
		this.actual = actual;
	}

	public SnmpValidatingGetFailed(PDU sentPdu, MibReader reader,
			Variable[] expected, Variable actual, StringBuilder addInfo) {
		super("Validating Get After Set"
				+ (addInfo == null ? "" : addInfo.toString()), sentPdu, reader);
		this.expected = expected;
		this.actual = actual;
	}

	public SnmpValidatingGetFailed(PDU sentPdu, MibReader reader,
			Variable[] expected, Variable actual, StringBuilder addInfo,
			Throwable cause) {
		super("Validating Get After Set"
				+ (addInfo == null ? "" : addInfo.toString()), sentPdu, reader,
				cause);
		this.expected = expected;
		this.actual = actual;
	}

	public Variable[] getExpected() {
		return expected;
	}

	public void setExpected(Variable[] expected) {
		this.expected = expected;
	}

	public Variable getActual() {
		return actual;
	}

	public void setActual(Variable actual) {
		this.actual = actual;
	}

}
