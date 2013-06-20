package systemobject.aqua.comm.snmp.constant;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.Variable;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum SnmpRowStatus {

	ACTIVE(1, new Integer32(1)), NOT_IN_SERVICE(2, new Integer32(2)), NOT_READY(
			3, new Integer32(3)), CREATE_AND_GO(4, ACTIVE.variable()), CREATE_AND_WAIT(
			5, NOT_READY.variable()), DESTROY(6, new Null() {
		private static final long serialVersionUID = 9161687847852580201L;

		@Override
		public int getSyntax() {
			return SnmpError.NO_SUCH_INSTANCE.value();
		}
	});

	private int value;

	private Variable expected;

	SnmpRowStatus(int value, Variable expected) {
		this.value = value;
		this.expected = expected;
	}

	public int value() {
		return value;
	}

	public Variable variable() {
		return new Integer32(value());
	}

	public Variable expected() {
		return (Variable) expected.clone();
	}

	public static SnmpRowStatus get(int value) {
		SnmpRowStatus[] arr = values();
		for (int i = 0; i < arr.length; i++) {
			if (value == arr[i].value()) {
				return arr[i];
			}
		}
		return null;
	}
}
