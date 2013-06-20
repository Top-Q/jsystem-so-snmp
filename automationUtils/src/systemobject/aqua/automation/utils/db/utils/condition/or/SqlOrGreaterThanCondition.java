package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrGreaterThanCondition extends SqlOrCondition {

	public SqlOrGreaterThanCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterThanCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterThanCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterThanCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.GREATER_THAN, field, value);
	}

	public SqlOrGreaterThanCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.GREATER_THAN, value);
	}

}
