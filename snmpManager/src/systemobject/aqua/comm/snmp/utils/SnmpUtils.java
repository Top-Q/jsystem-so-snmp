package systemobject.aqua.comm.snmp.utils;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public abstract class SnmpUtils {

	public static long oidOctetToLong(int octet) {
		return (octet & 0xFFFFFFFFL);
	}
}
