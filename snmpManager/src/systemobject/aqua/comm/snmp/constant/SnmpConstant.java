package systemobject.aqua.comm.snmp.constant;

import org.snmp4j.mp.SnmpConstants;

import systemobject.aqua.comm.snmp.constant.v3.SecurityLevel;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public interface SnmpConstant {

	public static final int DEFAULT_PORT = SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT;

	public static final int DEFAULT_TRAP_PORT = SnmpConstants.DEFAULT_NOTIFICATION_RECEIVER_PORT;

	public static final String DEFAULT_COMUNITY = "private";

	public static final SnmpVersion DEFAULT_VERSION = SnmpVersion.SNMPV2;;

	public static final int DEFAULT_RETRIES = 5;

	public static final int DEFAULT_TIMEOUT = 10000;

	public static final SecurityLevel DEFAULT_SNMP_V3_SECURITY_LEVEL = SecurityLevel.AUTH_PRIV;

}
