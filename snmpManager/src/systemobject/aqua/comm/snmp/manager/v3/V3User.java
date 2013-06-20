package systemobject.aqua.comm.snmp.manager.v3;

import systemobject.aqua.comm.snmp.constant.v3.AuthenticationProtocol;
import systemobject.aqua.comm.snmp.constant.v3.PrivacyProtocol;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class V3User implements Cloneable {

	private String userName = null;

	private String securityName = null;

	private String authPassword = null;

	private String privacyPassword = null;

	private AuthenticationProtocol authProtocol = AuthenticationProtocol.MD5;

	private PrivacyProtocol privacyProtocol = PrivacyProtocol.DES;

	public V3User() {
		super();
	}

	public V3User(String user, String password) {
		this();

		setUser(user);
		setPassword(password);
	}

	public V3User(String userName, String securityName, String authPassword,
			String privacyPassword, AuthenticationProtocol authProtocol,
			PrivacyProtocol privacyProtocol) {
		this(userName, authPassword);
		setSecurityName(securityName);
		setPrivacyPassword(privacyPassword);
		setAuthProtocol(authProtocol);
		setPrivacyProtocol(privacyProtocol);

	}

	public V3User(V3User o) {
		this(o.getUserName(), o.getSecurityName(), o.getAuthPassword(), o
				.getPrivacyPassword(), o.getAuthProtocol(), o
				.getPrivacyProtocol());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) throws ClassCastException {
		if (obj != null && obj instanceof V3User) {
			if (this == obj) {
				return true;
			}
			V3User other = (V3User) obj;
			return (this.getUserName().equals(other.getUserName())
					&& this.getSecurityName().equals(other.getSecurityName())
					&& this.getAuthPassword().equals(other.getAuthPassword())
					&& this.getPrivacyPassword().equals(
							other.getPrivacyPassword())
					&& this.getAuthProtocol() == other.getAuthProtocol() && this
						.getPrivacyProtocol() == other.getPrivacyProtocol());

		}
		return false;
	}

	public void setUser(String user) {
		setUserName(user);
		setSecurityName(user);
	}

	public String getUser() {
		return (getUserName() != null
				&& getUserName().equals(getSecurityName()) ? getUserName()
				: null);
	}

	public void setPassword(String password) {
		setAuthPassword(password);
		setPrivacyPassword(password);
	}

	public String getPassword() {
		return (getAuthPassword() != null
				&& getAuthPassword().equals(getPrivacyPassword()) ? getAuthPassword()
				: null);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getPrivacyPassword() {
		return privacyPassword;
	}

	public void setPrivacyPassword(String privacyPassword) {
		this.privacyPassword = privacyPassword;
	}

	public AuthenticationProtocol getAuthProtocol() {
		return authProtocol;
	}

	public void setAuthProtocol(AuthenticationProtocol authProtocol) {
		this.authProtocol = authProtocol;
	}

	public PrivacyProtocol getPrivacyProtocol() {
		return privacyProtocol;
	}

	public void setPrivacyProtocol(PrivacyProtocol privacyProtocol) {
		this.privacyProtocol = privacyProtocol;
	}

}