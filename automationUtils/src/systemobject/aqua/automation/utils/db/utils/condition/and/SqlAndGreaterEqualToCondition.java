package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndGreaterEqualToCondition extends SqlAndCondition {

	public SqlAndGreaterEqualToCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterEqualToCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterEqualToCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndGreaterEqualToCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.GREATER_EQUAL_TO, field, value);
	}

	public SqlAndGreaterEqualToCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.GREATER_EQUAL_TO, value);
	}

}
