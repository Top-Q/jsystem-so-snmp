package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionType;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndCondition extends SqlCondition {

	public SqlAndCondition(SqlConditionValueCompare valCompare, Object field,
			Integer... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlAndCondition(SqlConditionValueCompare valCompare, Object field,
			Long... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlAndCondition(SqlConditionValueCompare valCompare, Object field,
			String... value) {
		this(valCompare, field, (Object[]) value);
	}

	public SqlAndCondition(SqlConditionValueCompare valCompare, Object field,
			Object... value) {
		super(SqlConditionType.AND, SqlConditionType.AND, valCompare, field,
				value);
	}

	public SqlAndCondition(SqlConditionValueCompare valCompare,
			SqlCondition... value) {
		super(SqlConditionType.AND, SqlConditionType.AND, valCompare, value);
	}

}
