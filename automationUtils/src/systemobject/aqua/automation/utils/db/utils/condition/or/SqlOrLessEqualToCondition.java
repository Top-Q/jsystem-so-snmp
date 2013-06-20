package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrLessEqualToCondition extends SqlOrCondition {

	public SqlOrLessEqualToCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessEqualToCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessEqualToCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessEqualToCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LESS_EQUAL_TO, field, value);
	}

	public SqlOrLessEqualToCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LESS_EQUAL_TO, value);
	}

}
