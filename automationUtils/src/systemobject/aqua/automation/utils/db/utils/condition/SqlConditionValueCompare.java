package systemobject.aqua.automation.utils.db.utils.condition;

/**
 * @author Itzhak.Hovav
 */
public enum SqlConditionValueCompare {

	EQUAL("=") {
		public String value(Object value) {
			if (value == null) {
				return "IS NULL";
			}
			return super.value(value);
		}
	},
	LIKE("%") {
		public String value(Object value) {
			return ("LIKE '%" + value.toString() + "%'");
		}
	},
	REGEXP("REGEXP") {
		public String value(Object value) {
			return ("REGEXP '" + value.toString() + "'");
		}
	},
	NOT_LIKE("%") {
		public String value(Object value) {
			return ("NOT LIKE '%" + value.toString() + "%'");
		}
	},
	NOT_EQUAL("<>") {
		public String value(Object value) {
			if (value == null) {
				return "IS NOT NULL";
			}
			return super.value(value);
		}
	},
	GREATER_THAN(">"), LESS_THAN("<"), GREATER_EQUAL_TO(">="), LESS_EQUAL_TO(
			"<=");

	private String symbol = null;

	SqlConditionValueCompare(String symbol) {
		this.symbol = symbol;
	}

	public String value(Object value) {
		if (value != null && value instanceof SqlCondition) {
			return value.toString();
		}
		return (symbol + "'" + value.toString() + "'");
	}

}
