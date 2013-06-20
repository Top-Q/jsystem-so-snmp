package systemobject.aqua.comm.snmp.constant;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.mp.SnmpConstants;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum SnmpVersion {
	UNDEFINED(Integer.MIN_VALUE), SNMPV1(SnmpConstants.version1), SNMPV2(
			SnmpConstants.version2c), SNMPV3(SnmpConstants.version3) {
		public PDU build(int type) {
			PDU p = new ScopedPDU();
			p.setType(type);
			return p;
		}
	};

	SnmpVersion(int version) {
		this.version = version;
	}

	public static SnmpVersion get(int version) {
		SnmpVersion[] arr = values();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].value() == version) {
				return arr[i];
			}
		}
		return SnmpVersion.UNDEFINED;
	}

	public PDU build(int type) {
		PDU p = new PDU();
		p.setType(type);
		return p;
	}

	private int version;

	public int value() {
		return version;
	}
}
