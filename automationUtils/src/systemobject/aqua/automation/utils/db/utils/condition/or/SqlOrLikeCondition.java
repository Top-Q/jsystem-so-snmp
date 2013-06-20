package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrLikeCondition extends SqlOrCondition {

	public SqlOrLikeCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLikeCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLikeCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrLikeCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.LIKE, field, value);
	}

	public SqlOrLikeCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.LIKE, value);
	}

}
