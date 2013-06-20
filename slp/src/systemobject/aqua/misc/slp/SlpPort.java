package systemobject.aqua.misc.slp;

import jsystem.framework.system.SystemObjectImpl;

/**
 * @author Uri.Koaz
 */
public class SlpPort extends SystemObjectImpl {

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void on() throws Exception {
		((Slp) this.getParent()).on(this);
	}

	public void off() throws Exception {
		((Slp) this.getParent()).off(this);
	}

	public boolean isOn() throws Exception {
		return ((Slp) this.getParent()).isOn(this);
	}

	public boolean isOff() throws Exception {
		return ((Slp) this.getParent()).isOff(this);
	}
}
