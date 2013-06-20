package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndEqualCondition extends SqlAndCondition {

	public SqlAndEqualCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndEqualCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndEqualCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndEqualCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.EQUAL, field, value);
	}

	public SqlAndEqualCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.EQUAL, value);
	}

}
