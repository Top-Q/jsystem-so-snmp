package systemobject.aqua.comm.cli.ext;

public interface ConnectChecker {

	public boolean isConnected(CliTelnet conn) throws Exception;
}
