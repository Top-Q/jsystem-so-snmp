package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndLessThanCondition extends SqlAndCondition {

	public SqlAndLessThanCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessThanCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessThanCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessThanCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LESS_THAN, field, value);
	}

	public SqlAndLessThanCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LESS_THAN, value);
	}

}
