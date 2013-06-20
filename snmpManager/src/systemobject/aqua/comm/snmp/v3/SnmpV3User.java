package systemobject.aqua.comm.snmp.v3;

import jsystem.framework.system.SystemObjectImpl;
import systemobject.aqua.comm.snmp.constant.v3.AuthenticationProtocol;
import systemobject.aqua.comm.snmp.constant.v3.PrivacyProtocol;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpV3User extends SystemObjectImpl implements Cloneable {

	private systemobject.aqua.comm.snmp.manager.v3.V3User v3User = null;

	public SnmpV3User() {
		super();
		this.v3User = new systemobject.aqua.comm.snmp.manager.v3.V3User();
	}

	public SnmpV3User(String user, String password) {
		this();

		this.v3User.setUser(user);
		this.v3User.setPassword(password);
	}

	public SnmpV3User(systemobject.aqua.comm.snmp.manager.v3.V3User v3User) {
		this();

		this.v3User = new systemobject.aqua.comm.snmp.manager.v3.V3User(v3User);
	}

	public SnmpV3User(SnmpV3User o) {
		this(o.v3User);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SnmpV3User o = (SnmpV3User) super.clone();
		o.v3User = (systemobject.aqua.comm.snmp.manager.v3.V3User) this.v3User
				.clone();
		return o;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SnmpV3User) {
			if (this == obj) {
				return true;
			}
			return (this.v3User.equals(((SnmpV3User) obj).v3User));

		}
		return false;
	}

	public void setUser(String user) {
		this.v3User.setUser(user);
	}

	public String getUser() {
		return this.v3User.getUser();
	}

	public void setPassword(String password) {
		this.v3User.setPassword(password);
	}

	public String getPassword() {
		return this.v3User.getPassword();
	}

	public String getUserName() {
		return this.v3User.getUserName();
	}

	public void setUserName(String userName) {
		this.v3User.setUserName(userName);
	}

	public String getSecurityName() {
		return this.v3User.getSecurityName();
	}

	public void setSecurityName(String securityName) {
		this.v3User.setSecurityName(securityName);
	}

	public String getAuthPassword() {
		return this.v3User.getAuthPassword();
	}

	public void setAuthPassword(String authPassword) {
		this.v3User.setAuthPassword(authPassword);
	}

	public String getPrivacyPassword() {
		return this.v3User.getPrivacyPassword();
	}

	public void setPrivacyPassword(String privacyPassword) {
		this.v3User.setPrivacyPassword(privacyPassword);
	}

	public AuthenticationProtocol getAuthProtocol() {
		return this.v3User.getAuthProtocol();
	}

	public void setAuthProtocol(AuthenticationProtocol authProtocol) {
		this.v3User.setAuthProtocol(authProtocol);
	}

	public PrivacyProtocol getPrivacyProtocol() {
		return this.v3User.getPrivacyProtocol();
	}

	public void setPrivacyProtocol(PrivacyProtocol privacyProtocol) {
		this.v3User.setPrivacyProtocol(privacyProtocol);
	}

	public systemobject.aqua.comm.snmp.manager.v3.V3User getSnmpV3User() {
		return v3User;
	}

}