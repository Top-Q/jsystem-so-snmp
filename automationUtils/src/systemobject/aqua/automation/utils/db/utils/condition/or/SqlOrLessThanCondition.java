package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrLessThanCondition extends SqlOrCondition {

	public SqlOrLessThanCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessThanCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessThanCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLessThanCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LESS_THAN, field, value);
	}

	public SqlOrLessThanCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LESS_THAN, value);
	}

}
