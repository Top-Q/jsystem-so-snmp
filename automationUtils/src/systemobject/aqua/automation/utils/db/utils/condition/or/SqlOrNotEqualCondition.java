package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrNotEqualCondition extends SqlOrCondition {

	public SqlOrNotEqualCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrNotEqualCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrNotEqualCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrNotEqualCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.NOT_EQUAL, field, value);
	}

	public SqlOrNotEqualCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.NOT_EQUAL, value);
	}

}
