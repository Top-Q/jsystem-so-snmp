package systemobject.aqua.misc.juniper;

import junit.framework.SystemTestCase;

public class JuniperBrasTest extends SystemTestCase {

	private JuniperERX310 bras = null;

	public void setUp() throws Exception {
		bras = (JuniperERX310) system.getSystemObject("juniper");
	}

	public void test1() throws Exception {
		System.out.println(bras.getL2cSessionTimeoutMillis());
		System.out.println(bras.sendEthernetOamLoopback("0000.c0a9.160a",
				"192.168.21.21", "55", 55, 55, 55));

		BrasDiscoveryTableEntry[] arr = bras
				.getCurrentL2cDiscoveryTableEntries("0000.c0a9.160a");
		for (BrasDiscoveryTableEntry i : arr) {
			System.out.println(i);
		}

	}

}
