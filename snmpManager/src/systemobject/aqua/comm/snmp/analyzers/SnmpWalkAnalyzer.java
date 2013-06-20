package systemobject.aqua.comm.snmp.analyzers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.analyzers.text.AnalyzeTextParameter;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpWalkAnalyzer extends AnalyzeTextParameter {

	private String expected = null;

	private String actual = null;

	private String oidExt = null;

	private boolean caseSensitive = false;

	public SnmpWalkAnalyzer(String oid, Object... oidExt) {
		this(null, oid, oidExt);
	}

	public SnmpWalkAnalyzer(String expected, String oid, Object... oidExt) {
		this(false, null, oid, oidExt);
	}

	public SnmpWalkAnalyzer(boolean caseSensitive, String expected, String oid,
			Object... oidExt) {
		super(null);
		StringBuilder sb = new StringBuilder(oid);
		if (oidExt != null) {
			for (Object o : oidExt) {
				sb.append('.');
				sb.append(o.toString());
			}
		}
		this.toFind = sb.toString();
		this.expected = expected;
	}

	public void analyze() {
		Pattern p = null;
		if (this.caseSensitive) {
			p = Pattern.compile(String.format("%s%s", this.toFind,
					"[\\.0-9]+\\s*([\\=\\:])[ \\t\\x0B\\f]*(.*)"));
		} else {
			p = Pattern.compile(String.format("%s%s", this.toFind,
					"[\\.0-9]+\\s*([\\=\\:])[ \\t\\x0B\\f]*(.*)"),
					Pattern.CASE_INSENSITIVE);
		}
		Matcher m = p.matcher(this.testText);
		this.message = this.testText;
		if (!m.find()) {
			this.status = false;
			this.title = "Oid Wasn't Found In Walk: " + this.toFind;
			return;
		}
		String seperator = m.group(1);
		this.oidExt = m.group();
		this.oidExt = this.oidExt.replace(")", "");
		if (this.oidExt.length() >= this.toFind.length()) {
			this.oidExt = this.oidExt.substring(this.toFind.length());
		}
		int index = this.oidExt.indexOf(seperator);
		if (index >= 0 && this.oidExt.length() > index) {
			this.actual = this.oidExt.substring(index + 1).trim();
		}
		if (index >= 0 && this.oidExt.length() >= index) {
			this.oidExt = this.oidExt.substring(0,
					this.oidExt.indexOf(seperator));
		}
		while (this.oidExt.length() > 0 && this.oidExt.charAt(0) == '.') {
			this.oidExt = this.oidExt.substring(1);
		}
		while (this.oidExt.length() > 0
				&& (this.oidExt.charAt(this.oidExt.length() - 1) < '0' || this.oidExt
						.charAt(this.oidExt.length() - 1) > '9')) {
			this.oidExt = this.oidExt.substring(0, this.oidExt.length() - 1);
		}
		StringBuilder sb = new StringBuilder("Get Value From Walk: ");
		sb.append(getFullOid());
		sb.append(": ");
		if (expected != null) {
			sb.append("Expected ");
			sb.append(this.expected);
			sb.append(", Actual ");
		}
		sb.append(this.actual);
		this.title = sb.toString();
		this.status = (compareCounters() == 0);
	}

	protected int compareCounters() {
		return (this.expected == null ? 0 : this.actual
				.compareTo(this.expected));
	}

	public String getCounter() {
		return this.actual;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getFullOid() {
		String oid = String.format("%s%c%s", this.toFind, '.', this.oidExt);
		while (oid.length() > 0
				&& (oid.charAt(oid.length() - 1) < '0' || oid.charAt(oid
						.length() - 1) > '9')) {
			oid = oid.substring(0, oid.length() - 1);
		}
		return oid;
	}

	public String getOidExt() {
		return oidExt;
	}

	public void setOidExt(String oidExt) {
		this.oidExt = oidExt;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}
