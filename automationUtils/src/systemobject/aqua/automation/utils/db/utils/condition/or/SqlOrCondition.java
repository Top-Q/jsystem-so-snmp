package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionType;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrCondition extends SqlCondition {

	public SqlOrCondition(SqlConditionValueCompare valCompare, Object field,
			Integer... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlOrCondition(SqlConditionValueCompare valCompare, Object field,
			Long... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlOrCondition(SqlConditionValueCompare valCompare, Object field,
			String... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlOrCondition(SqlConditionValueCompare valCompare, Object field,
			Object... value) {
		super(SqlConditionType.AND, SqlConditionType.OR, valCompare, field,
				value);
	}

	public SqlOrCondition(SqlConditionValueCompare valCompare,
			SqlCondition... value) {
		super(SqlConditionType.AND, SqlConditionType.OR, valCompare, value);
	}

}
