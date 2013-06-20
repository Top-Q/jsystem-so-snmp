package systemobject.aqua.comm.cli.ext.cliTrigger.listener;

import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;

public interface TriggerListener {

	public void triggerNotify(Trigger trigger);

	public String listenerName();
}
