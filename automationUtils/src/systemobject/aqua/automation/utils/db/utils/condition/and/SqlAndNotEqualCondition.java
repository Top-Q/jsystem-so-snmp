package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndNotEqualCondition extends SqlAndCondition {

	public SqlAndNotEqualCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotEqualCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotEqualCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotEqualCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.NOT_EQUAL, field, value);
	}

	public SqlAndNotEqualCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.NOT_EQUAL, value);
	}

}
