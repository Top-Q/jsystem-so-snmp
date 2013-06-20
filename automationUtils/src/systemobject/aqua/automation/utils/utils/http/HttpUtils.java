package systemobject.aqua.automation.utils.utils.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.system.SystemObjectImpl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import systemobject.aqua.automation.utils.utils.file.FileUtils;
import systemobject.aqua.automation.utils.utils.time.TimeUtils;

/**
 * @author Uri.Koaz
 */
public class HttpUtils extends SystemObjectImpl {

	private static String lastResponse;

	/**
	 * @author Uri.Koaz
	 */
	public enum EnumHttpMethodType {
		GET, POST;
	}

	/**
	 * @author Uri.Koaz
	 */
	public enum EnumHttpAction {
		HTTP, SOAP;
	}

	public static String sendRequest(String url) throws IOException {
		URL yahoo = new URL(url);
		URLConnection yc = yahoo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;

		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
			sb.append(inputLine);
		in.close();

		return sb.toString().trim();
	}

	public static long timeRequestTook = 0;

	public static void sendSoapRequest(String host, String actionName,
			String path, String data) throws Exception {
		int port = 8080;
		StringBuilder sb = new StringBuilder();
		sb.append("SOAP Request to ");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(" , Method Type: POST, Action Name: ");
		sb.append(actionName);

		ListenerstManager.getInstance().startLevel(sb.toString(),
				EnumReportLevel.CurrentPlace);

		File xmlFile = new File(System.getProperty("user.dir"),
				ListenerstManager.getInstance().getCurrentTestFolder()
						+ "/soapRequest"
						+ String.valueOf(System.currentTimeMillis()) + ".xml");

		xmlFile.createNewFile();

		FileUtils.writeStringToFile(xmlFile, data);

		String[] temp = ListenerstManager.getInstance().getCurrentTestFolder()
				.split("\\\\");
		String currentTest = temp[temp.length - 1];

		ListenerstManager.getInstance().addLink("Request Content",
				currentTest + "/" + xmlFile.getName());

		String url = "http://" + host + ":" + port
				+ (path.startsWith("/") ? "" : "/") + path;

		PostMethod method = null;

		try {
			method = new PostMethod(url);
			method.setRequestEntity(new StringRequestEntity(data, "text/xml",
					"UTF-8"));
			method.setRequestHeader("SOAPAction", actionName);

			/**
			 * Send message
			 */

			HttpClient client = new HttpClient();
			// Execute request

			long startTime = System.currentTimeMillis();
			client.executeMethod(method);
			long endTime = System.currentTimeMillis();

			/**
			 * Get Response
			 */
			InputStream is = method.getResponseBodyAsStream();
			int b;

			sb = new StringBuilder();

			while ((b = is.read()) != -1)
				sb.append((char) b);

			HttpUtils.lastResponse = sb.toString();

			xmlFile = new File(System.getProperty("user.dir"),
					ListenerstManager.getInstance().getCurrentTestFolder()
							+ "/soapResponse"
							+ String.valueOf(System.currentTimeMillis())
							+ ".xml");

			xmlFile.createNewFile();

			FileUtils.writeStringToFile(xmlFile, HttpUtils.lastResponse);

			ListenerstManager.getInstance().addLink("Response Content",
					currentTest + "/" + xmlFile.getName());

			ListenerstManager
					.getInstance()
					.report("Request Took: "
							+ TimeUtils
									.fromMilliToTimeFormat((endTime - startTime))
							+ " Seconds");

			timeRequestTook = endTime - startTime;

		} finally {
			if (method != null)
				method.releaseConnection();

			ListenerstManager.getInstance().stopLevel();
		}
	}

	public static String getLastResponse() {
		return lastResponse;
	}
}