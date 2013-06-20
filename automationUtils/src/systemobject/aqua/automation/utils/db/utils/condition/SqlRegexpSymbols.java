package systemobject.aqua.automation.utils.db.utils.condition;

/**
 * @author Itzhak.Hovav
 */
public enum SqlRegexpSymbols {

	START_OF_TEXT("^"), END_OF_TEXT("$"), START_OF_WORD("[[:<:]]"), END_OF_WORD(
			"[[:>:]]"), REGEXP_OR("|"), REGEXP_RANGE("-");

	private String symbol;

	private SqlRegexpSymbols(String symbol) {
		this.symbol = symbol;
	}

	public String toString() {
		return this.symbol;
	}

}
