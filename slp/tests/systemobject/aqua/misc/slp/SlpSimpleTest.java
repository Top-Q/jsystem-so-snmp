package systemobject.aqua.misc.slp;

import junit.framework.SystemTestCase;

public class SlpSimpleTest extends SystemTestCase {

	@SuppressWarnings("unused")
	private Slp slp = null;

	@Override
	protected void setUp() throws Exception {

		super.setUp();

	}

	public void testSetup() throws Exception {
		slp = (Slp) system.getSystemObject("slp");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

	}
}
