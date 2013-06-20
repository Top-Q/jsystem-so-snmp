package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrGreaterEqualToCondition extends SqlOrCondition {

	public SqlOrGreaterEqualToCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterEqualToCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterEqualToCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrGreaterEqualToCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.GREATER_EQUAL_TO, field, value);
	}

	public SqlOrGreaterEqualToCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.GREATER_EQUAL_TO, value);
	}

}
