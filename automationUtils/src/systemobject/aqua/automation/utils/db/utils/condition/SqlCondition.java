package systemobject.aqua.automation.utils.db.utils.condition;

/**
 * @author Itzhak.Hovav
 */
public class SqlCondition {

	private SqlConditionType firstType = SqlConditionType.AND;

	private SqlConditionType type = SqlConditionType.AND;

	private SqlConditionValueCompare valCompare = SqlConditionValueCompare.EQUAL;

	private Object[] value = null;

	private Object field = null;

	public SqlCondition(SqlConditionType firstType, SqlConditionType type,
			SqlConditionValueCompare valCompare, SqlCondition... value) {
		this(firstType, type, valCompare, (Object) null, (Object[]) value);
		for (Object o : getValue()) {
			((SqlCondition) o).setFirstType(null);
		}
	}

	public SqlCondition(SqlConditionType firstType, SqlConditionType type,
			SqlConditionValueCompare valCompare, Object field, Integer... value) {
		this(firstType, type, valCompare, field, (Object[]) value);
	}

	public SqlCondition(SqlConditionType firstType, SqlConditionType type,
			SqlConditionValueCompare valCompare, Object field, Long... value) {
		this(firstType, type, valCompare, field, (Object[]) value);
	}

	public SqlCondition(SqlConditionType firstType, SqlConditionType type,
			SqlConditionValueCompare valCompare, Object field, String... value) {
		this(firstType, type, valCompare, field, (Object[]) value);
	}

	public SqlCondition(SqlConditionType firstType, SqlConditionType type,
			SqlConditionValueCompare valCompare, Object field, Object... value) {
		setFirstType(firstType);
		setType(type);
		setValCompare(valCompare);
		setValue(value);
		setField(field);
	}

	public static String getWhereConditions(SqlCondition... conditions) {
		StringBuilder sb = new StringBuilder();
		if (conditions != null && conditions.length > 0) {
			conditions[0].setFirstType(null);
			sb.append("WHERE");
			for (SqlCondition c : conditions) {
				sb.append(" ");
				sb.append(c.toString());
			}
		}
		String str = sb.toString();
		if (str.trim().equals("WHERE")) {
			str = "";
		}
		return str;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		Object[] obj = getValue();
		if (obj == null) {
			obj = new Object[] { (Object) null };
		}

		if (obj.length > 0) {

			if (getFirstType() != null) {
				sb.append(getFirstType().toString());
				sb.append(" ");
			}
			sb.append("(");
			for (Object o : obj) {
				if (getField() != null) {
					sb.append(getField().toString());
					sb.append(" ");
				}
				sb.append(getValCompare().value(o));
				sb.append(" ");
				sb.append(getType().toString());
				sb.append(" ");
			}
			sb.delete(sb.length() - (getType().toString().length() + 2),
					sb.length());
			sb.append(")");

		}
		return sb.toString();
	}

	public SqlConditionType getType() {
		return type;
	}

	public void setType(SqlConditionType type) {
		this.type = type;
	}

	public SqlConditionValueCompare getValCompare() {
		return valCompare;
	}

	public void setValCompare(SqlConditionValueCompare valCompare) {
		this.valCompare = valCompare;
	}

	public Object[] getValue() {
		return value;
	}

	public void setValue(Object... value) {
		this.value = value;
	}

	public Object getField() {
		return field;
	}

	public void setField(Object field) {
		this.field = field;
	}

	public SqlConditionType getFirstType() {
		return firstType;
	}

	public void setFirstType(SqlConditionType firstType) {
		this.firstType = firstType;
	}

}
