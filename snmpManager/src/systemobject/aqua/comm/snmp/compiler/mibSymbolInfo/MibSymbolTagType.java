package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.type.BitSetType;
import net.percederberg.mibble.type.BooleanType;
import net.percederberg.mibble.type.IntegerType;
import net.percederberg.mibble.type.NullType;
import net.percederberg.mibble.type.ObjectIdentifierType;
import net.percederberg.mibble.type.RealType;
import net.percederberg.mibble.type.StringType;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum MibSymbolTagType {

	NOT_AVAILABLE, BIT_STRING, BOOLEAN, INTEGER, OBJECT_IDENTIFIER, NULL, OCTET_STRING, REAL, GAUGE32, INTEGER32, UNSIGNED32, COUNTER32, COUNTER64, TIME_TICKS, ROW_STATUS, MAC_ADDRESS, IP_ADDRESS;

	public static MibSymbolTagType get(MibType type) {
		if (type instanceof IntegerType) {
			return getInteger(type);
		}
		if (type instanceof StringType) {
			return getOctetString(type);
		}
		if (type instanceof BitSetType) {
			return getBits(type);
		}
		if (type instanceof ObjectIdentifierType) {
			return getObjectIdentifier(type);
		}
		if (type instanceof BooleanType) {
			return getBoolean(type);
		}
		if (type instanceof NullType) {
			return getNull(type);
		}
		if (type instanceof RealType) {
			return getReal(type);
		}
		return NOT_AVAILABLE;
	}

	private static MibSymbolTagType getInteger(MibType type) {

		if (type.hasReferenceTo("Gauge32")) {
			return GAUGE32;
		}
		if (type.hasReferenceTo("Integer32")) {
			return INTEGER32;
		}
		if (type.hasReferenceTo("Unsigned32")) {
			return UNSIGNED32;
		}
		if (type.hasReferenceTo("Counter32")) {
			return COUNTER32;
		}
		if (type.hasReferenceTo("Counter64")) {
			return COUNTER64;
		}
		if (type.hasReferenceTo("TimeTicks")) {
			return TIME_TICKS;
		}
		if (type.hasReferenceTo("RowStatus")) {
			return ROW_STATUS;
		}
		return INTEGER;
	}

	private static MibSymbolTagType getOctetString(MibType type) {
		if (type.hasReferenceTo("MacAddress")) {
			return MAC_ADDRESS;
		}
		if (type.hasReferenceTo("IpAddress")) {
			return IP_ADDRESS;
		}
		return OCTET_STRING;
	}

	private static MibSymbolTagType getBits(MibType type) {
		return BIT_STRING;
	}

	private static MibSymbolTagType getBoolean(MibType type) {
		return BOOLEAN;
	}

	private static MibSymbolTagType getObjectIdentifier(MibType type) {
		return OBJECT_IDENTIFIER;
	}

	private static MibSymbolTagType getNull(MibType type) {
		return NULL;
	}

	private static MibSymbolTagType getReal(MibType type) {
		return REAL;
	}
}
