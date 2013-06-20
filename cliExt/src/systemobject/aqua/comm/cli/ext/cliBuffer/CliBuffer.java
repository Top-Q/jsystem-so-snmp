package systemobject.aqua.comm.cli.ext.cliBuffer;

import java.io.IOException;
import java.util.ArrayList;

import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.aqua.comm.cli.ext.cliListener.CliListener;
import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;
import systemobject.terminal.BufferInputStream;
import systemobject.terminal.InOutInputStream;

/**
 * This class represent an extension of the default CLI buffer with capturing
 * and triggering mechanisms
 * 
 * @author Itzhak.Hovav
 */
public class CliBuffer extends BufferInputStream {

	/**
	 * CLI Listener with capture and triggering mechanisms
	 */
	private CliListener listener = null;

	/**
	 * CTOR
	 * 
	 * @param cli
	 *            owner CLI object
	 * @param triggers
	 *            start triggers for the listener
	 */
	public CliBuffer(CliTelnet cli, Trigger[] triggers) {
		this();
		startCapture(triggers, cli);
	}

	/**
	 * CTOR
	 * 
	 * @param old
	 *            old CliBuffer, it will use its listener for the new buffer
	 */
	public CliBuffer(CliTelnet cli, CliBuffer old) {
		this(cli, old.listener);
	}

	/**
	 * CTOR
	 * 
	 * @param listener
	 *            CLI Listener to add to the buffer instead of creating a new
	 *            one
	 */
	public CliBuffer(CliTelnet cli, CliListener listener) {
		this();
		this.listener = listener;
		this.listener.setOwner(cli);
	}

	/**
	 * DEFULT CTOR, creates no CLI Listener
	 */
	public CliBuffer() {
		super();
	}

	public InOutInputStream getInputStream() {
		if (in instanceof InOutInputStream) {
			return (InOutInputStream) in;
		}
		return null;
	}

	/**
	 * set owner CLI connection
	 * 
	 * @param cli
	 *            owner CLI object
	 */
	public void setOwner(CliTelnet cli) {
		if (listener != null) {
			listener.setOwner(cli);
		}
	}

	/**
	 * returns the current triggers in use or "null" oif no triggers found
	 * 
	 * @return current triggers in use
	 */
	public ArrayList<Trigger> getTriggers() {
		if (listener != null) {
			return listener.getTriggers();
		}
		return null;
	}

	/**
	 * set the new triggers to use
	 * 
	 * @param triggers
	 *            triggers to use from now on
	 */
	public void setTriggers(Trigger[] triggers) {
		if (listener != null) {
			listener.setTriggers(triggers);
		}
	}

	/**
	 * return a single trigger by its representing string
	 * 
	 * @param trigger
	 *            string to look the trigger by
	 * @return the requested trigger or null if not found
	 */
	public Trigger getTrigger(String trigger) {
		if (listener != null) {
			return listener.getTrigger(trigger);
		}
		return null;
	}

	/**
	 * add multiple triggers to the current triggers
	 * 
	 * @param triggers
	 *            triggers to add
	 */
	public void addTriggers(Trigger[] triggers) {
		if (listener != null) {
			listener.addTriggers(triggers);
		}
	}

	/**
	 * add a single trigger to the existing ones
	 * 
	 * @param trigger
	 *            trigger to add
	 */
	public void addTrigger(Trigger trigger) {
		addTriggers(new Trigger[] { trigger });
	}

	/**
	 * remove the requested trigger if found
	 * 
	 * @param trigger
	 *            string representing the trigger to remove
	 */
	public void removeTrigger(String trigger) {
		if (listener != null) {
			listener.removeTrigger(trigger);
		}
	}

	/**
	 * remove a trigger by a matching trigger
	 * 
	 * @param trigger
	 *            matching trigger to find and remove the trigger by
	 */
	public void removeTrigger(Trigger trigger) {
		if (listener != null) {
			listener.removeTrigger(trigger);
		}
	}

	@Override
	protected void addToBuffer(char c) {
		super.addToBuffer(c);
		if (listener != null) {
			try {
				listener.putChar(c);
			} catch (IOException e) {
				setIoExp(e);
				return;
			}
		}
	}

	/**
	 * sets the given listener to be the current listener and by that - start
	 * capturing with it
	 * 
	 * @param listener
	 *            listener to set as the current listener
	 */
	protected void startCapture(CliListener listener) {
		if (this.listener != null) {
			try {
				this.listener.stop();
			} catch (IOException e) {
				setIoExp(e);
			}
		}
		this.listener = listener;
	}

	/**
	 * start a new capture
	 * 
	 * @param triggers
	 *            triggers to add to the listener
	 * @param cli
	 *            owner CLI object
	 */
	public void startCapture(Trigger[] triggers, CliTelnet cli) {
		if (this.listener != null) {
			try {
				this.listener.stop();
			} catch (IOException e) {
				setIoExp(e);
			}
		}
		listener = new CliListener(cli, triggers);
	}

	/**
	 * stops the capture, closes the capture file and add a link to it in the
	 * report
	 * 
	 * @throws IOException
	 */
	public void stopCapture() throws IOException {
		stopCapture(true);
	}

	/**
	 * stops the capture
	 * 
	 * @param closeFile
	 *            true to close the file
	 * @throws IOException
	 */
	public void stopCapture(boolean closeFile) throws IOException {
		if (listener != null && closeFile) {
			listener.stop();
		}
		listener = null;
	}

	/**
	 * close the exist capture file with or without link in the report and open
	 * a new capture file
	 * 
	 * @param addLink
	 */
	public void restartCapture(boolean addLink) {
		if (listener != null) {
			listener.restartCapture(addLink);
		}
	}

	public void setListener(CliListener listener) {
		this.listener = listener;
	}

	/**
	 * add link to the capture file in the report
	 */
	public void addCaptureLink() {
		if (listener != null) {
			listener.addLink();
		}
	}

	/**
	 * stop the capture, stops the listener, add link to the report and close
	 * the buffer
	 */
	public void close() throws IOException {
		stopCapture();
		try {
			super.close();
		} catch (IOException e) {

		}
	}

	/**
	 * check if is in capture mode
	 * 
	 * @return true if in capture mode
	 */
	public boolean isCapture() {
		return ((listener != null) && (listener.isCupture()));
	}

	/**
	 * returns the current listener object
	 * 
	 * @return current listener or null if no listener exist
	 */
	public CliListener getListener() {
		return listener;
	}

	/**
	 * add a CLI tag to the capture file
	 * 
	 * @param cliTag
	 *            CLI tag as String
	 */
	public void addCliTag(String cliTag) {
		if (listener != null) {
			listener.setCliTag(cliTag);
		}
	}

}
