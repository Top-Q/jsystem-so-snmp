/**
 * Represents a Single interface in the Juniper Bras 
 * A single interface contains Slot, Port and Vlan
 */
package systemobject.aqua.misc.juniper;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.sysobj.apcon.ApconConnection;

/**
 * @author Itzhak.Hovav
 * 
 */
public class BrasInterface extends SystemObjectImpl implements ApconConnection {

	private int apconPortIndex = -1;

	private int slot;

	private int port;

	private int vlan;

	private int bridgeGroup;

	private String portMac;

	private String neighbor;

	private String endUserId;

	private String ip;

	private String mask;

	@Override
	public int getApconPortIndex() {
		return apconPortIndex;
	}

	@Override
	public boolean isConnectedToApcon() {
		return apconPortIndex >= 0;
	}

	@Override
	public void setApconPortIndex(int apconPortIndex) {
		this.apconPortIndex = apconPortIndex;
	}

	/**
	 * returns the interface in a "S/P.V" pattern (S=slot, P=port and V=vlan)
	 */
	public String toString() {
		return (slot + "/" + port + "." + vlan);
	}

	/**
	 * returns the slot number of the interface
	 * 
	 * @return slot number as int
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * sets the slot number of the interface
	 * 
	 * @param slot
	 *            slot number as int
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

	/**
	 * returns the port number of the interface
	 * 
	 * @return port number as int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * sets the port number of the interface
	 * 
	 * @param port
	 *            port number as int
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * returns the vlan number of the interface
	 * 
	 * @return vlan number as int
	 */
	public int getVlan() {
		return vlan;
	}

	/**
	 * sets the vlan number of the interface
	 * 
	 * @param vlan
	 *            vlan number as int
	 */
	public void setVlan(int vlan) {
		this.vlan = vlan;
	}

	/**
	 * returns the Bridge Group number of the interface
	 * 
	 * @return Bridge Group number as int
	 */
	public int getBridgeGroup() {
		return bridgeGroup;
	}

	/**
	 * sets the Bridge Group number of the interface
	 * 
	 * @param bridgeGroup
	 *            Bridge Group number as int
	 */
	public void setBridgeGroup(int bridgeGroup) {
		this.bridgeGroup = bridgeGroup;
	}

	/**
	 * returns the neighbor name of the interface
	 * 
	 * @return neighbor name as String
	 */
	public String getNeighbor() {
		return neighbor;
	}

	/**
	 * sets the neighbor name of the interface
	 * 
	 * @param neighbor
	 *            neighbor name as String
	 */
	public void setNeighbor(String neighbor) {
		this.neighbor = neighbor;
	}

	/**
	 * returns the end-user-id of the interface
	 * 
	 * @return end-user-id as String
	 */
	public String getEndUserId() {
		return endUserId;
	}

	/**
	 * sets the end-user-id of the interface
	 * 
	 * @param endUserId
	 *            end-user-id as String
	 */
	public void setEndUserId(String endUserId) {
		this.endUserId = endUserId;
	}

	/**
	 * returns the ip of the interface
	 * 
	 * @return interface ip as String ("0.0.0.0" format)
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * sets the ip of the interface
	 * 
	 * @param ip
	 *            interface ip as String ("0.0.0.0" format)
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * returns the mask of the interface (mostly will be class C -
	 * "255.255.255.0")
	 * 
	 * @return interface mask as String ("0.0.0.0" format)
	 */
	public String getMask() {
		return mask;
	}

	/**
	 * sets the mask of the interface (mostly will be class C - "255.255.255.0")
	 * 
	 * @return mask interface mask as String ("0.0.0.0" format)
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getPortMac() {
		return portMac;
	}

	public void setPortMac(String portMac) {
		this.portMac = portMac;
	}
}
