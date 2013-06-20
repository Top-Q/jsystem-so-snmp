package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndNotLikeCondition extends SqlAndCondition {

	public SqlAndNotLikeCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotLikeCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotLikeCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndNotLikeCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.NOT_LIKE, field, value);
	}

	public SqlAndNotLikeCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.NOT_LIKE, value);
	}

}
