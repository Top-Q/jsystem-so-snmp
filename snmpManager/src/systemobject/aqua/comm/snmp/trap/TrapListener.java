package systemobject.aqua.comm.snmp.trap;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public interface TrapListener {

	public void trapReceived(TrapReceived tr);

}
