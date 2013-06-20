package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpOperationFailed extends SnmpException {

	private static final long serialVersionUID = -7114745585176614459L;

	private PDU sentPdu = null;

	private PDU receivedPdu = null;

	public SnmpOperationFailed(String operationName, PDU sentPdu,
			PDU receivedPdu, MibReader reader) {
		super("Snmp " + operationName + " Operation Failed", buildMsg(reader,
				sentPdu, receivedPdu));
		setSentPdu(sentPdu);
		setReceivedPdu(receivedPdu);
	}

	public SnmpOperationFailed(String operationName, PDU sentPdu,
			PDU receivedPdu, MibReader reader, Throwable cause) {
		super("Snmp " + operationName + " Operation Failed", buildMsg(reader,
				sentPdu, receivedPdu), cause);
		setSentPdu(sentPdu);
		setReceivedPdu(receivedPdu);
	}

	public SnmpOperationFailed(String operationName, PDU sentPdu,
			MibReader reader) {
		this(operationName, sentPdu, null, reader);
	}

	public SnmpOperationFailed(String operationName, PDU sentPdu,
			MibReader reader, Throwable cause) {
		this(operationName, sentPdu, null, reader, cause);
	}

	protected static String buildMsg(MibReader reader, PDU sent) {
		return buildMsg(reader, sent, null);
	}

	protected static String buildMsg(MibReader reader, PDU sent, PDU received) {
		boolean readerExist = (reader != null);
		boolean receiverExist = (received != null);
		boolean sentExist = (sent != null);
		StringBuilder sb = new StringBuilder();
		if (sentExist) {
			VariableBinding vb = sent.get(0);
			OID o = vb.getOid();
			Variable v = vb.getVariable();
			sb.append("\nSent ");
			sb.append(v.getClass().getSimpleName());
			sb.append(": ");
			if (readerExist) {
				sb.append(reader.getMibActualName(o.toString()));
				sb.append("[");
			}
			sb.append(o.toString());
			if (readerExist) {
				sb.append("]");
			}
			sb.append("=");
			sb.append(v.toString());
		}
		if (receiverExist) {
			if (sentExist) {
				sb.append(", ");
			}
			VariableBinding vb = received.get(0);
			OID o = vb.getOid();
			Variable v = vb.getVariable();
			sb.append("\nResponce ");
			sb.append(v.getClass().getSimpleName());
			sb.append(": ");
			if (readerExist) {
				sb.append(reader.getMibActualName(o.toString()));
				sb.append("[");
			}
			sb.append(o.toString());
			if (readerExist) {
				sb.append("]");
			}
			sb.append("=");
			sb.append(v.toString());
			sb.append(" (");
			sb.append(received.getErrorStatusText());
			sb.append(")");
		}
		return sb.toString();
	}

	public PDU getSentPdu() {
		return sentPdu;
	}

	public void setSentPdu(PDU sentPdu) {
		this.sentPdu = sentPdu;
	}

	public PDU getReceivedPdu() {
		return receivedPdu;
	}

	public void setReceivedPdu(PDU receivedPdu) {
		this.receivedPdu = receivedPdu;
	}
}
