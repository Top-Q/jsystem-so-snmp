package systemobject.aqua.automation.utils.utils.publish;

import jsystem.framework.report.Summary;

/**
 * @author Itzhak.Hovav
 */
public enum PublishProperties {

	DUT_VERSION("summary.Version"), DUT_BUILD("summary.Build"), SUT_NAME(
			"summary.Sut");

	private String prop;

	PublishProperties(String prop) {
		this.prop = prop;
	}

	public String getValue() {
		Object o = Summary.getInstance().getProperty(this.prop);
		if (o != null) {
			o = o.toString();
		} else {
			o = ((String) null);
		}
		return (String) o;
	}

	public String setValue(String value) throws Exception {
		String prev = getValue();
		Summary.getInstance().setProperty(this.prop, value);
		return prev;
	}

}
