package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndLikeCondition extends SqlAndCondition {

	public SqlAndLikeCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLikeCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLikeCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndLikeCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LIKE, field, value);
	}

	public SqlAndLikeCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LIKE, value);
	}

}
