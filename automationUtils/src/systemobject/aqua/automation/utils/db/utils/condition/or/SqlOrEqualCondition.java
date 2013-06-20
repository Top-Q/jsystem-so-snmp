package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrEqualCondition extends SqlOrCondition {

	public SqlOrEqualCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrEqualCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrEqualCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrEqualCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.EQUAL, field, value);
	}

	public SqlOrEqualCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.EQUAL, value);
	}

}
