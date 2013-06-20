package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndLessEqualToCondition extends SqlAndCondition {

	public SqlAndLessEqualToCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessEqualToCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessEqualToCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLessEqualToCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LESS_EQUAL_TO, field, value);
	}

	public SqlAndLessEqualToCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LESS_EQUAL_TO, value);
	}

}
