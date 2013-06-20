package systemobject.aqua.misc.juniper;

import systemobject.aqua.misc.juniper.BrasDiscoveryTableEntry.EnumEntryType;
import systemobject.aqua.misc.juniper.BrasDiscoveryTableEntry.EnumUserState;

/**
 * This class is used to process and hold an extended Bras Discovery table entry
 * with all its parameters
 * 
 * @author Itzhak.Hovav
 * 
 */
public class BrasDiscoveryTableEntryFullDetails {

	/**
	 * This enum represents the possible dsl types as presented in the bras for
	 * each entry
	 * 
	 * @author Itzhak.Hovav
	 * 
	 */
	public enum EnumDslType {
		UNKNOWN(0), ADSL1(1), ADSL2(2), ADSL2_PLUS(3), VDSL(4), VDSL2(5), SDSL(
				6), INVALID_TRANSMISSION_TYPE(7);
		private int value;

		EnumDslType(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static EnumDslType getByValue(int value) {
			EnumDslType[] arr = EnumDslType.values();
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].value() == value) {
					return arr[i];
				}
			}
			return EnumDslType.UNKNOWN;
		}
	}

	/**
	 * This enum represents the possible line status as presented in the bras
	 * for each entry
	 * 
	 * @author Itzhak.Hovav
	 * 
	 */
	public enum EnumLineState {
		UNKNOWN(0), SHOWTIME(1), IDLE(2), SILENT(3);
		private int value;

		EnumLineState(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static EnumLineState getByValue(int value) {
			EnumLineState[] arr = EnumLineState.values();
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].value() == value) {
					return arr[i];
				}
			}
			return EnumLineState.UNKNOWN;
		}
	}

	private String neighbor = "";

	private EnumUserState state = EnumUserState.UNKNOWN;

	private int slot = 0;

	private int port = 0;

	private int vlan = 0;

	private int vp = 0;

	private int vc = 0;

	private String masterIp = "";

	private String slaveIp = "";

	private EnumEntryType entryType = EnumEntryType.UNKNOWN;

	private long actualDataRateUpstream = 0;

	private long actualDataRateDownstream = 0;

	private long minDataRateUpstream = 0;

	private long minDataRateDownstream = 0;

	private long attainableDataRateUpstream = 0;

	private long attainableDataRateDownstream = 0;

	private long maxDataRateUpstream = 0;

	private long maxDataRateDownstream = 0;

	private long minLowPowerDataRateUpstream = 0;

	private long minLowPowerDataRateDownstream = 0;

	private long maxInterleavingDelayUpstream = 0;

	private long actualInterleavingDelayUpstream = 0;

	private long maxInterleavingDelayDownstream = 0;

	private long actualInterleavingDelayDownstream = 0;

	private EnumLineState lineState = EnumLineState.UNKNOWN;

	private EnumDslType dslType = EnumDslType.UNKNOWN;

	protected BrasDiscoveryTableEntryFullDetails(String[] entry) {
		if (entry != null && entry.length > 0) {
			for (int i = 0; i < entry.length; i++) {
				if (entry[i] != null && entry[i].length() != 0
						&& !(entry[i] = entry[i].trim()).equals("")) {
					if (entry[i].contains("Access-Loop-Id")) {
						processAccessLoopId(entry[i]);
					} else if (entry[i].contains("Neighbor")) {
						processNeighbor(entry[i]);
					} else if (entry[i].contains("Actual-Data-Rate-Upstream")) {
						processActualDataRateUS(entry[i]);
					} else if (entry[i].contains("Actual-Data-Rate-Downstream")) {
						processActualDataRateDS(entry[i]);
					} else if (entry[i].contains("Min-Data-Rate-Upstream")) {
						processMinDataRateUS(entry[i]);
					} else if (entry[i].contains("Min-Data-Rate-Downstream")) {
						processMinDataRateDS(entry[i]);
					} else if (entry[i]
							.contains("Attainable-Data-Rate-Upstream")) {
						processAttainableDataRateUS(entry[i]);
					} else if (entry[i]
							.contains("Attainable-Data-Rate-Downstream")) {
						processAttainableDataRateDS(entry[i]);
					} else if (entry[i].contains("Max-Data-Rate-Upstream")) {
						processMaxDataRateUS(entry[i]);
					} else if (entry[i].contains("Max-Data-Rate-Downstream")) {
						processMaxDataRateDS(entry[i]);
					} else if (entry[i]
							.contains("Min-Low-Power-Data-Rate-Upstream")) {
						processMinLowPowerDataRateUS(entry[i]);
					} else if (entry[i]
							.contains("Min-Low-Power-Data-Rate-Downstream")) {
						processMinLowPowerDataRateDS(entry[i]);
					} else if (entry[i]
							.contains("Max-Interleaving-Delay-Upstream")) {
						processMaxInterleavingDelayUS(entry[i]);
					} else if (entry[i]
							.contains("Actual-Interleaving-Delay-Upstream")) {
						processActualInterleavingDelayUS(entry[i]);
					} else if (entry[i]
							.contains("Max-Interleaving-Delay-Downstream")) {
						processMaxInterleavingDelayDS(entry[i]);
					} else if (entry[i]
							.contains("Actual-Interleaving-Delay-Downstream")) {
						processActualInterleavingDelayDS(entry[i]);
					} else if (entry[i].contains("Line-State")) {
						processLineState(entry[i]);
					} else if (entry[i].contains("Dsl-Type")) {
						processDslType(entry[i]);
					}
				}
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Access-Loop-Id: " + masterIp
				+ (slaveIp == null || slaveIp.equals("") ? "" : "/" + slaveIp)
				+ " " + entryType.name() + " " + slot + "/" + port + ":"
				+ (vp != 0 && vc != 0 && vlan == 0 ? vp + "." + vc : vlan)
				+ " " + state.name() + '\n');
		sb.append("Neighbor: " + neighbor + '\n');
		sb.append("Actual-Data-Rate-Upstream: " + actualDataRateUpstream
				+ "(kbps)" + '\n');
		sb.append("Actual-Data-Rate-Downstream: " + actualDataRateDownstream
				+ "(kbps)" + '\n');
		sb.append("Min-Data-Rate-Upstream: " + minDataRateUpstream + "(kbps)"
				+ '\n');
		sb.append("Min-Data-Rate-Downstream: " + minDataRateDownstream
				+ "(kbps)" + '\n');
		sb.append("Attainable-Data-Rate-Upstream: "
				+ attainableDataRateUpstream + "(kbps)" + '\n');
		sb.append("Attainable-Data-Rate-Downstream: "
				+ attainableDataRateDownstream + "(kbps)" + '\n');
		sb.append("Max-Data-Rate-Upstream: " + maxDataRateUpstream + "(kbps)"
				+ '\n');
		sb.append("Max-Data-Rate-Downstream: " + maxDataRateDownstream
				+ "(kbps)" + '\n');
		sb.append("Min-Low-Power-Data-Rate-Upstream: "
				+ minLowPowerDataRateUpstream + "(kbps)" + '\n');
		sb.append("Min-Low-Power-Data-Rate-Downstream: "
				+ minLowPowerDataRateDownstream + "(kbps)" + '\n');
		sb.append("Max-Interleaving-Delay-Upstream: "
				+ maxInterleavingDelayUpstream + "(ms)" + '\n');
		sb.append("Actual-Interleaving-Delay-Upstream: "
				+ actualInterleavingDelayUpstream + "(ms)" + '\n');
		sb.append("Max-Interleaving-Delay-Downstream: "
				+ maxInterleavingDelayDownstream + "(ms)" + '\n');
		sb.append("Actual-Interleaving-Delay-Downstream: "
				+ actualInterleavingDelayDownstream + "(ms)" + '\n');
		sb.append("Line-State: " + lineState.value() + "(" + lineState.name()
				+ ")" + '\n');
		sb.append("Dsl-Type: " + dslType.value() + "(" + dslType.name() + ")"
				+ '\n');
		return sb.toString();
	}

	protected void processAccessLoopId(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Access-Loop-Id")) {
			line = line.replace("Access-Loop-Id:", "").trim();
			while (line.contains("  ")) {
				line = line.replace("  ", " ");
			}
			String[] arr = line.split(" ");
			if (arr != null && arr.length == 4) {
				if (arr[0].contains("/")) {
					masterIp = arr[0].substring(0, arr[0].indexOf("/")).trim();
					slaveIp = arr[0].substring(arr[0].indexOf("/") + 1).trim();
				} else {
					masterIp = arr[0].trim();
				}

				entryType = (arr[1].trim().toUpperCase().contains("ETH") ? EnumEntryType.ETH
						: EnumEntryType.ATM);

				slot = Integer.parseInt(arr[2]
						.substring(0, arr[2].indexOf("/")).trim());
				port = Integer.parseInt(arr[2].substring(
						arr[2].indexOf("/") + 1, arr[2].indexOf(":")).trim());
				arr[2] = arr[2].substring(arr[2].indexOf(":") + 1).trim();
				if (arr[2].contains(".")) {
					vp = Integer.parseInt(arr[2].substring(0,
							arr[2].indexOf('.')).trim());
					vc = Integer.parseInt(arr[2].substring(
							arr[2].indexOf('.') + 1).trim());
				} else {
					vlan = Integer.parseInt(arr[2].trim());
				}

				state = (arr[3].trim().toUpperCase().contains("UP") ? EnumUserState.UP
						: EnumUserState.DOWN);
			}
		}
	}

	protected void processNeighbor(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Neighbor")) {
			line = line.replace("Neighbor:", "");
			neighbor = line.trim();
		}
	}

	protected void processActualDataRateUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Actual-Data-Rate-Upstream")) {
			line = line.replace("Actual-Data-Rate-Upstream:", "");
			line = line.replace("(kbps)", "");
			actualDataRateUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processActualDataRateDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Actual-Data-Rate-Downstream")) {
			line = line.replace("Actual-Data-Rate-Downstream:", "");
			line = line.replace("(kbps)", "");
			actualDataRateDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processMinDataRateUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Min-Data-Rate-Upstream")) {
			line = line.replace("Min-Data-Rate-Upstream:", "");
			line = line.replace("(kbps)", "");
			minDataRateUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processMinDataRateDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Min-Data-Rate-Downstream")) {
			line = line.replace("Min-Data-Rate-Downstream:", "");
			line = line.replace("(kbps)", "");
			minDataRateDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processAttainableDataRateUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Attainable-Data-Rate-Upstream")) {
			line = line.replace("Attainable-Data-Rate-Upstream:", "");
			line = line.replace("(kbps)", "");
			attainableDataRateUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processAttainableDataRateDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Attainable-Data-Rate-Downstream")) {
			line = line.replace("Attainable-Data-Rate-Downstream:", "");
			line = line.replace("(kbps)", "");
			attainableDataRateDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processMaxDataRateUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Max-Data-Rate-Upstream")) {
			line = line.replace("Max-Data-Rate-Upstream:", "");
			line = line.replace("(kbps)", "");
			maxDataRateUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processMaxDataRateDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Max-Data-Rate-Downstream")) {
			line = line.replace("Max-Data-Rate-Downstream:", "");
			line = line.replace("(kbps)", "");
			maxDataRateDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processMinLowPowerDataRateUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Min-Low-Power-Data-Rate-Upstream")) {
			line = line.replace("Min-Low-Power-Data-Rate-Upstream:", "");
			line = line.replace("(kbps)", "");
			minLowPowerDataRateUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processMinLowPowerDataRateDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Min-Low-Power-Data-Rate-Downstream")) {
			line = line.replace("Min-Low-Power-Data-Rate-Downstream:", "");
			line = line.replace("(kbps)", "");
			minLowPowerDataRateDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processMaxInterleavingDelayUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Max-Interleaving-Delay-Upstream")) {
			line = line.replace("Max-Interleaving-Delay-Upstream:", "");
			line = line.replace("(ms)", "");
			maxInterleavingDelayUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processMaxInterleavingDelayDS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Max-Interleaving-Delay-Downstream")) {
			line = line.replace("Max-Interleaving-Delay-Downstream:", "");
			line = line.replace("(ms)", "");
			maxInterleavingDelayDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processActualInterleavingDelayUS(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Actual-Interleaving-Delay-Upstream")) {
			line = line.replace("Actual-Interleaving-Delay-Upstream:", "");
			line = line.replace("(ms)", "");
			actualInterleavingDelayUpstream = Long.parseLong(line.trim());
		}
	}

	protected void processActualInterleavingDelayDS(String line) {
		if (line != null
				&& line.length() != 0
				&& line.trim().startsWith(
						"Actual-Interleaving-Delay-Downstream")) {
			line = line.replace("Actual-Interleaving-Delay-Downstream:", "");
			line = line.replace("(ms)", "");
			actualInterleavingDelayDownstream = Long.parseLong(line.trim());
		}
	}

	protected void processLineState(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Line-State")) {
			line = line.replace("Line-State:", "").trim();
			line = line.substring(0, line.indexOf('(')).trim();
			lineState = EnumLineState.getByValue(Integer.parseInt(line.trim()));
		}
	}

	protected void processDslType(String line) {
		if (line != null && line.length() != 0
				&& line.trim().startsWith("Dsl-Type")) {
			line = line.replace("Dsl-Type:", "").trim();
			line = line.substring(0, line.indexOf('(')).trim().trim();
			dslType = EnumDslType.getByValue(Integer.parseInt(line.trim()));
		}
	}

	public String getNeighbor() {
		return neighbor;
	}

	protected void setNeighbor(String neighbor) {
		this.neighbor = neighbor;
	}

	public EnumUserState getState() {
		return state;
	}

	protected void setState(EnumUserState state) {
		this.state = state;
	}

	public int getVlan() {
		return vlan;
	}

	protected void setVlan(int vlan) {
		this.vlan = vlan;
	}

	public int getVp() {
		return vp;
	}

	protected void setVp(int vp) {
		this.vp = vp;
	}

	public int getVc() {
		return vc;
	}

	protected void setVc(int vc) {
		this.vc = vc;
	}

	public String getMasterIp() {
		return masterIp;
	}

	protected void setMasterIp(String masterIp) {
		this.masterIp = masterIp;
	}

	public String getSlaveIp() {
		return slaveIp;
	}

	protected void setSlaveIp(String slaveIp) {
		this.slaveIp = slaveIp;
	}

	public EnumEntryType getEntryType() {
		return entryType;
	}

	protected void setEntryType(EnumEntryType entryType) {
		this.entryType = entryType;
	}

	public long getActualDataRateUpstream() {
		return actualDataRateUpstream;
	}

	protected void setActualDataRateUpstream(long actualDataRateUpstream) {
		this.actualDataRateUpstream = actualDataRateUpstream;
	}

	public long getActualDataRateDownstream() {
		return actualDataRateDownstream;
	}

	protected void setActualDataRateDownstream(long actualDataRateDownstream) {
		this.actualDataRateDownstream = actualDataRateDownstream;
	}

	public long getMinDataRateUpstream() {
		return minDataRateUpstream;
	}

	protected void setMinDataRateUpstream(long minDataRateUpstream) {
		this.minDataRateUpstream = minDataRateUpstream;
	}

	public long getMinDataRateDownstream() {
		return minDataRateDownstream;
	}

	protected void setMinDataRateDownstream(long minDataRateDownstream) {
		this.minDataRateDownstream = minDataRateDownstream;
	}

	public long getAttainableDataRateUpstream() {
		return attainableDataRateUpstream;
	}

	protected void setAttainableDataRateUpstream(long attainableDataRateUpstream) {
		this.attainableDataRateUpstream = attainableDataRateUpstream;
	}

	public long getAttainableDataRateDownstream() {
		return attainableDataRateDownstream;
	}

	protected void setAttainableDataRateDownstream(
			long attainableDataRateDownstream) {
		this.attainableDataRateDownstream = attainableDataRateDownstream;
	}

	public long getMaxDataRateUpstream() {
		return maxDataRateUpstream;
	}

	protected void setMaxDataRateUpstream(long maxDataRateUpstream) {
		this.maxDataRateUpstream = maxDataRateUpstream;
	}

	public long getMaxDataRateDownstream() {
		return maxDataRateDownstream;
	}

	protected void setMaxDataRateDownstream(long maxDataRateDownstream) {
		this.maxDataRateDownstream = maxDataRateDownstream;
	}

	public long getMinLowPowerDataRateUpstream() {
		return minLowPowerDataRateUpstream;
	}

	protected void setMinLowPowerDataRateUpstream(
			long minLowPowerDataRateUpstream) {
		this.minLowPowerDataRateUpstream = minLowPowerDataRateUpstream;
	}

	public long getMinLowPowerDataRateDownstream() {
		return minLowPowerDataRateDownstream;
	}

	protected void setMinLowPowerDataRateDownstream(
			long minLowPowerDataRateDownstream) {
		this.minLowPowerDataRateDownstream = minLowPowerDataRateDownstream;
	}

	public long getMaxInterleavingDelayUpstream() {
		return maxInterleavingDelayUpstream;
	}

	protected void setMaxInterleavingDelayUpstream(
			long maxInterleavingDelayUpstream) {
		this.maxInterleavingDelayUpstream = maxInterleavingDelayUpstream;
	}

	public long getActualInterleavingDelayUpstream() {
		return actualInterleavingDelayUpstream;
	}

	protected void setActualInterleavingDelayUpstream(
			long actualInterleavingDelayUpstream) {
		this.actualInterleavingDelayUpstream = actualInterleavingDelayUpstream;
	}

	public long getMaxInterleavingDelayDownstream() {
		return maxInterleavingDelayDownstream;
	}

	protected void setMaxInterleavingDelayDownstream(
			long maxInterleavingDelayDownstream) {
		this.maxInterleavingDelayDownstream = maxInterleavingDelayDownstream;
	}

	public long getActualInterleavingDelayDownstream() {
		return actualInterleavingDelayDownstream;
	}

	protected void setActualInterleavingDelayDownstream(
			long actualInterleavingDelayDownstream) {
		this.actualInterleavingDelayDownstream = actualInterleavingDelayDownstream;
	}

	public EnumLineState getLineState() {
		return lineState;
	}

	protected void setLineState(EnumLineState lineState) {
		this.lineState = lineState;
	}

	public EnumDslType getDslType() {
		return dslType;
	}

	protected void setDslType(EnumDslType dslType) {
		this.dslType = dslType;
	}

	public int getSlot() {
		return slot;
	}

	protected void setSlot(int slot) {
		this.slot = slot;
	}

	public int getPort() {
		return port;
	}

	protected void setPort(int port) {
		this.port = port;
	}

}
