package systemobject.aqua.automation.utils.db;

/**
 * @author Itzhak.Hovav
 */
public enum SqlOperator {

	AND, OR, XOR, NOT, IS, IS_NOT {
		@Override
		public String toString() {
			return "IS NOT";
		}
	};
}
