package systemobject.aqua.automation.utils.utils.mail;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

/**
 * @author Uri.Koaz
 */
public class MailUtils {

	public static boolean sendMail(String title, String msg,
			String defaultMailDomain, String senderName,
			String smtpServerAddress, String[] recipients, String[] cc)
			throws MessagingException {

		for (int i = 0; i < recipients.length; i++) {
			if (!recipients[i].contains("@")) {
				recipients[i] = recipients[i].replace(" ", ".");
				recipients[i] += ("@" + defaultMailDomain);
			}
		}

		MailGenerator.body = msg.replaceAll(enter(), "<br>");
		MailGenerator.from = senderName;
		MailGenerator.subject = title;
		MailGenerator.smtpServer = smtpServerAddress;

		try {
			MailGenerator.postHtmlMail(recipients, cc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String enter(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	private static String enter() {
		return enter(1);
	}

}
