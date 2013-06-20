package systemobject.aqua.comm.snmp.constant.v3;

import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.smi.OID;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum AuthenticationProtocol {

	MD5(AuthMD5.ID), SHA(AuthSHA.ID);

	private OID oid;

	AuthenticationProtocol(OID oid) {
		this.oid = oid;
	}

	public OID oid() {
		return oid;
	}
}
