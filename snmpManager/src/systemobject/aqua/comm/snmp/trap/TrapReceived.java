package systemobject.aqua.comm.snmp.trap;

import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.Address;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class TrapReceived {

	private long time = 0L;

	private PDU pdu = null;

	private Address address = null;

	public TrapReceived(CommandResponderEvent event) {
		this(event.getPDU(), event.getPeerAddress());
	}

	public TrapReceived(PDU pdu, Address address) {
		setTime(System.currentTimeMillis());
		setPdu(pdu);
		setAddress(address);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Trap Received: time=");
		sb.append(getTime());
		sb.append(" millis\n");
		sb.append("Address=");
		sb.append(getAddress().toString());
		sb.append("\nPDU=");
		sb.append(pdu.toString());
		return sb.toString();
	}

	public PDU getPdu() {
		return pdu;
	}

	public void setPdu(PDU pdu) {
		this.pdu = pdu;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getHost() {
		Address a = getAddress();
		String val = null;
		if (a != null) {
			val = a.toString();
			val = val.substring(0, val.indexOf('/')).trim();
		}
		return val;
	}

	public Integer getPort() {
		Address a = getAddress();
		Integer val = null;
		if (a != null) {
			String str = a.toString();
			val = Integer.parseInt(str.substring(str.indexOf('/') + 1).trim());
		}
		return val;
	}

}
