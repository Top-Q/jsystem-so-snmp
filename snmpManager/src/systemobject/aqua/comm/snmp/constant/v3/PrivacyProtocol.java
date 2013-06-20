package systemobject.aqua.comm.snmp.constant.v3;

import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.OID;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum PrivacyProtocol {

	DES(PrivDES.ID), DES3(Priv3DES.ID), AES128(PrivAES128.ID), AES192(
			PrivAES192.ID), AES256(PrivAES256.ID);

	private OID oid;

	PrivacyProtocol(OID oid) {
		this.oid = oid;
	}

	public OID oid() {
		return oid;
	}
}
