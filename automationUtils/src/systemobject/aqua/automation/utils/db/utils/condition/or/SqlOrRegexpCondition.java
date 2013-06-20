package systemobject.aqua.automation.utils.db.utils.condition.or;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlOrRegexpCondition extends SqlOrCondition {

	public SqlOrRegexpCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlOrRegexpCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlOrRegexpCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlOrRegexpCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.REGEXP, field, value);
	}

	public SqlOrRegexpCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.REGEXP, value);
	}

}
