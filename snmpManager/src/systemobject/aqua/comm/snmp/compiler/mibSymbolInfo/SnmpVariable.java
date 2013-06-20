package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

import org.snmp4j.smi.BitString;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.SMIAddress;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariantVariable;

import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum SnmpVariable {

	Counter64(Counter64.class, null), Int32(Integer32.class, null), Null(
			Null.class, null), OctetString(OctetString.class, null), BitString(
			BitString.class, SnmpVariable.OctetString.var), Opaque(
			Opaque.class, SnmpVariable.OctetString.var), ObjectId(OID.class,
			null), SmiAddress(SMIAddress.class, null), GenericAddress(
			GenericAddress.class, SnmpVariable.SmiAddress.var), IpAddress(
			IpAddress.class, SnmpVariable.SmiAddress.var), TransportIpAddress(
			TransportIpAddress.class, SnmpVariable.IpAddress.var), TcpAddress(
			TcpAddress.class, SnmpVariable.TransportIpAddress.var), UdpAddress(
			UdpAddress.class, SnmpVariable.TransportIpAddress.var), UInt32(
			UnsignedInteger32.class, null), Counter32(Counter32.class,
			SnmpVariable.UInt32.var), Gauge32(Gauge32.class,
			SnmpVariable.UInt32.var), TimeTicks(TimeTicks.class,
			SnmpVariable.UInt32.var), VariantVariable(VariantVariable.class,
			null);

	SnmpVariable(Class<? extends Variable> var, Class<? extends Variable> father) {
		this.father = father;
		this.var = var;
	}

	private Class<? extends Variable> var;

	private Class<? extends Variable> father;

	public boolean isInstance(Variable syntax) {
		if (var.getClass().isInstance(syntax)) {
			if (father == null || !father.isInstance(syntax)) {
				return true;
			}
		}
		return false;
	}

	public static SnmpVariable get(Variable syntax) {
		SnmpVariable[] arr = values();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].isInstance(syntax)) {
				return arr[i];
			}
		}
		return null;
	}

	public Variable get() throws SnmpException {
		try {
			return var.newInstance();
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New Snmp Variable",
					e.getMessage(), e);
		}
	}

}
