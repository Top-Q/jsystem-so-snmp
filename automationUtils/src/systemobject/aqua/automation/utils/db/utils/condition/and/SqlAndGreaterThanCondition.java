package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndGreaterThanCondition extends SqlAndCondition {

	public SqlAndGreaterThanCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterThanCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterThanCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterThanCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.GREATER_THAN, field, value);
	}

	public SqlAndGreaterThanCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.GREATER_THAN, value);
	}

}
