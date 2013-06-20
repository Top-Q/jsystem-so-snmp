package systemobject.aqua.comm.snmp.compiler.db;

import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum EnumMibDbFields {

	OID {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getOid();
		}
	},
	NAME {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getMibName();
		}
	},
	TYPE {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getTagType().ordinal();
		}
	},
	MAX_ACCESS {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getAccess().ordinal();
		}
	},
	STATUS {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getStatus().ordinal();
		}
	},
	DESCRIPTION {
		public Object getValue(MibSymbolInfo symbol) {
			return symbol.getDescription().replace("'", "''")
					.replace("\\", "\\\\").replace(";", "\\;")
					.replace(":", "\\:");
		}
	};

	public Object getValue(MibSymbolInfo symbol) {
		return null;
	}

}
