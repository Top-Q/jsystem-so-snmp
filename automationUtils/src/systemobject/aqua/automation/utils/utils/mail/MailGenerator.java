package systemobject.aqua.automation.utils.utils.mail;

import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Uri.Koaz
 */
public abstract class MailGenerator {

	public static String smtpServer;

	public static String subject;

	public static String body;

	public static String from;

	public static void postHtmlMail(String recipients[], String[] cc,
			String[] bcc) throws Exception {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = getInternetAdressArray(recipients);

		msg.setRecipients(Message.RecipientType.TO, addressTo);

		if (cc != null) {
			InternetAddress[] adressCC = getInternetAdressArray(cc);
			msg.setRecipients(Message.RecipientType.CC, adressCC);
		}

		if (bcc != null) {
			InternetAddress[] adressBCC = getInternetAdressArray(bcc);
			msg.setRecipients(Message.RecipientType.BCC, adressBCC);
		}

		msg.addHeader("MyHeaderName", "myHeaderValue");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(body, "text/html");

		Transport.send(msg);
	}

	public static void postHtmlMail(String recipients[]) throws Exception {
		postHtmlMail(recipients, null);
	}

	public static void postHtmlMail(String recipients[], String[] cc)
			throws Exception {
		postHtmlMail(recipients, cc, null);
	}

	private static InternetAddress[] getInternetAdressArray(String[] recipients)
			throws AddressException {
		Vector<String> v = new Vector<String>();

		for (int i = 0; i < recipients.length; i++) {
			try {
				new InternetAddress(recipients[i]).validate();
				v.add(recipients[i]);
			} catch (Exception e) {
			}
		}

		InternetAddress[] addressTo = new InternetAddress[v.size()];

		for (int i = 0; i < addressTo.length; i++) {
			addressTo[i] = new InternetAddress(v.get(i));
		}

		return addressTo;
	}
}
