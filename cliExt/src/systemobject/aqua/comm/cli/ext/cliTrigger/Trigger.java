package systemobject.aqua.comm.cli.ext.cliTrigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.report.Reporter;
import systemobject.aqua.automation.utils.utils.html.HtmlTable.EnumHtmlColor;
import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.aqua.comm.cli.ext.cliTrigger.listener.TriggerListener;

/**
 * This class represents the basic CLI Trigger
 * 
 * @author Itzhak.Hovav
 */
public class Trigger extends ArrayList<Trigger> implements Comparable<Trigger>,
		Cloneable {

	/**
	 * ENUM represents all trigger possible actions regarding the test status
	 * and log notification
	 * 
	 * @author Itzhak.Hovav
	 */
	public enum TriggerAction {
		FAIL_AND_STOP_RUNNER(Reporter.FAIL, 5, EnumHtmlColor.DARK_RED), FAIL_AND_PAUSE_RUNNER(
				Reporter.FAIL, 4, EnumHtmlColor.DEEP_PINK), FAIL(Reporter.FAIL,
				3, EnumHtmlColor.RED), WARNING_AND_PAUSE_RUNNER(
				Reporter.WARNING, 2, EnumHtmlColor.DEEP_PINK), WARNING(
				Reporter.WARNING, 1, EnumHtmlColor.ORANGE), PASS(Reporter.PASS,
				0, EnumHtmlColor.BLUE), SILENT(-1, -1,
				EnumHtmlColor.GREEN_YELLOW);

		private int status;

		private int weight;

		private EnumHtmlColor reportColor;

		TriggerAction(int status, int weight, EnumHtmlColor reportColor) {
			this.status = status;
			this.weight = weight;
			this.reportColor = reportColor;
		}

		public int status() {
			return this.status;
		}

		public int weight() {
			return this.weight;
		}

		public EnumHtmlColor reportColor() {
			return this.reportColor;
		}
	}

	private static final long serialVersionUID = -3553217456815460676L;

	private HashMap<String, TriggerListener> listeners = null;

	private CliTelnet connection = null;

	/**
	 * String represents the trigger in the CLI
	 */
	private String trigger = null;

	/**
	 * read-write lock for the trigger object (enables effective access for both
	 * read and write while working with multiple threads)
	 */
	private ReadWriteLock triggerRwLock = new ReentrantReadWriteLock();

	/**
	 * current trigger action
	 */
	private TriggerAction action = TriggerAction.PASS;

	/**
	 * read-write lock for the action object (enables effective access for both
	 * read and write while working with multiple threads)
	 */
	private ReadWriteLock actionRwLock = new ReentrantReadWriteLock();

	/**
	 * true for case sensitive, false for non-case-sensitive
	 */
	private boolean caseSensitive = true;

	/**
	 * true for case sensitive, false for non-case-sensitive
	 */
	private boolean regEx = false;

	/**
	 * Pattern for regular expression
	 */
	private Pattern regExPattern = null;

	public Trigger(TriggerAction action, String trigger, boolean caseSensitive,
			boolean regEx, CliTelnet connection) {
		setTrigger(trigger);
		setAction(action);
		setCaseSensitive(caseSensitive);
		setRegEx(regEx);
		if (isRegEx()) {
			if (isCaseSensitive()) {
				setRegExPattern(Pattern.compile(getTrigger()));
			} else {
				setRegExPattern(Pattern.compile(getTrigger(),
						Pattern.CASE_INSENSITIVE));
			}
		} else {
			setRegExPattern(null);
		}
		setConnection(connection);
	}

	public Trigger(TriggerAction action, String trigger, boolean regEx) {
		this(action, trigger, regEx, null);
	}

	public Trigger(TriggerAction action, String trigger, boolean regEx,
			CliTelnet connection) {
		this(action, trigger, false, regEx, connection);
	}

	public Trigger(TriggerAction action, String trigger, boolean caseSensitive,
			boolean regEx) {
		this(action, trigger, caseSensitive, regEx, null);
	}

	public Trigger(Trigger o) {
		this(o.getAction(), o.getTrigger(), o.isCaseSensitive(), o.isRegEx(), o
				.getConnection());
	}

	public Trigger getTrigger(String trigger) {

		Trigger t = null;

		/**
		 * first check if the given trigger string is the exact one in this
		 * trigger
		 */
		if (this.isRegEx()) {
			Matcher m = this.getRegExPattern().matcher(trigger);
			if (m.find()) {
				t = this;
			}
		} else if (this.isCaseSensitive()) {
			if (this.getTrigger().equals(trigger)) {
				t = this;
			}
		} else {
			if (this.getTrigger().equalsIgnoreCase(trigger)) {
				t = this;
			}
		}

		if (t == null) {
			/**
			 * check if the given trigger string related to any of the children
			 * of this trigger
			 */
			for (Trigger o : this) {
				t = o.getTrigger(trigger);
				if (t != null) {
					break;
				}
			}
		}

		if (t == null) {
			/**
			 * check if the given trigger string related to this trigger
			 */
			if (testLine(trigger)) {
				t = this;
			}
		}

		return t;
	}

	private boolean testLine(String line) {
		boolean status = false;
		if (isRegEx() && getRegExPattern() != null) {
			status = getRegExPattern().matcher(line).find();
		} else {
			status = (isCaseSensitive() ? line.contains(getTrigger()) : line
					.toLowerCase().contains(getTrigger().toLowerCase()));
		}
		return status;
	}

	public Trigger test(String line) {
		/**
		 * default returned value: "null" (will have value only if the given
		 * line related to this trigger and not to any of its child).
		 */
		Trigger actTrigger = null;
		if (testLine(line)) { // the given line related to this trigger
			for (Trigger t : this) {
				Trigger temp = t.test(line);
				if (temp != null
						&& (actTrigger == null || actTrigger.getAction()
								.weight() < temp.getAction().weight())) {
					actTrigger = temp;
				}
			}
			if (actTrigger == null) {
				notifyCapture();
				actTrigger = this;
			}
		}
		return actTrigger;
	}

	public void addListener(TriggerListener listener) {
		if (listeners == null) {
			listeners = new HashMap<String, TriggerListener>();
		}
		listeners.put(listener.listenerName(), listener);
	}

	public TriggerListener getListener(TriggerListener listenerName) {
		return getListener(listenerName.listenerName());
	}

	public TriggerListener getListener(String listenerName) {
		return getListener(listenerName, false);
	}

	public TriggerListener removeListener(TriggerListener listenerName) {
		return getListener(listenerName.listenerName());
	}

	public TriggerListener removeListener(String listenerName) {
		return getListener(listenerName, true);
	}

	private TriggerListener getListener(String listenerName, boolean remove) {
		TriggerListener l = null;
		if (listeners != null) {
			if (remove) {
				l = listeners.remove(listenerName);
			} else {
				l = listeners.get(listenerName);
			}
		}
		return l;
	}

	/**
	 * this method will be called after each time the trigger will be captured
	 */
	public void notifyCapture() {
		if (listeners != null) {
			for (TriggerListener l : listeners.values()) {
				l.triggerNotify(this);
			}
		}
	}

	public Object clone() {
		return new Trigger(this);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getConnection() != null) {
			sb.append("Cli: ");
			sb.append(getConnection().toString());
			sb.append(": ");
		}
		sb.append("Trigger (");
		if (isRegEx()) {
			sb.append("Regular Expression");
			if (isCaseSensitive()) {
				sb.append(", ");
			}
		}
		if (isCaseSensitive()) {
			sb.append("Case Sensitive");
		} else {
			sb.append("Case Insensitive");
		}
		sb.append(") \"");
		sb.append(getTrigger());
		sb.append("\", Action ");
		sb.append(getAction().toString());
		return sb.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof Trigger) {
			Trigger t = (Trigger) o;
			return ((t.isCaseSensitive() == this.isCaseSensitive())
					&& (t.isRegEx() == this.isRegEx())
					&& (t.getAction() == this.getAction()) && (t.getTrigger()
					.equals(this.getTrigger())));
		}
		return false;
	}

	public String getTrigger() {
		try {
			triggerRwLock.readLock().lock();
			return trigger;
		} finally {
			triggerRwLock.readLock().unlock();
		}
	}

	public void setTrigger(String trigger) {
		try {
			triggerRwLock.writeLock().lock();
			this.trigger = trigger;
		} finally {
			triggerRwLock.writeLock().unlock();
		}
	}

	public TriggerAction getAction() {
		try {
			actionRwLock.readLock().lock();
			return action;
		} finally {
			actionRwLock.readLock().unlock();
		}
	}

	public void setAction(TriggerAction action) {
		try {
			actionRwLock.writeLock().lock();
			this.action = action;
		} finally {
			actionRwLock.writeLock().unlock();
		}
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isRegEx() {
		return regEx;
	}

	public void setRegEx(boolean regEx) {
		this.regEx = regEx;
	}

	public CliTelnet getConnection() {
		return connection;
	}

	public void setConnection(CliTelnet connection) {
		this.connection = connection;
	}

	public Pattern getRegExPattern() {
		return regExPattern;
	}

	public void setRegExPattern(Pattern regExPattern) {
		this.regExPattern = regExPattern;
	}

	@Override
	public int compareTo(Trigger o) {
		return getTrigger().compareTo(o.getTrigger());
	}
}
