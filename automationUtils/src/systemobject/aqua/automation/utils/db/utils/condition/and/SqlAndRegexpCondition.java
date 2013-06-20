package systemobject.aqua.automation.utils.db.utils.condition.and;

import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;
import systemobject.aqua.automation.utils.db.utils.condition.SqlConditionValueCompare;

/**
 * @author Itzhak.Hovav
 */
public class SqlAndRegexpCondition extends SqlAndCondition {

	public SqlAndRegexpCondition(Object field, Integer... value) {
		this(field, (Object[]) value);
	}

	public SqlAndRegexpCondition(Object field, Long... value) {
		this(field, (Object[]) value);
	}

	public SqlAndRegexpCondition(Object field, String... value) {
		this(field, (Object[]) value);
	}

	public SqlAndRegexpCondition(Object field, Object... value) {
		super(SqlConditionValueCompare.REGEXP, field, value);
	}

	public SqlAndRegexpCondition(SqlCondition... value) {
		super(SqlConditionValueCompare.REGEXP, value);
	}

}
