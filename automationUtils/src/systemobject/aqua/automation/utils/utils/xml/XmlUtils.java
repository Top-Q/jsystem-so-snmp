package systemobject.aqua.automation.utils.utils.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import systemobject.aqua.automation.utils.utils.file.FileUtils;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Uri.Koaz
 */
public abstract class XmlUtils {

	public static void updateAllXmlVersions(String directory,
			boolean withSubFolders, String version) {
		File file = new File(directory);
		if (file.exists()) {
			List<File> list = FileUtils.listFiles(file, withSubFolders);
			for (File f : list) {
				updateXmlVersion(f, version);
			}
		}
	}

	public static boolean updateXmlVersion(String file, String version) {
		return updateXmlVersion(new File(file), version);
	}

	public static boolean updateXmlVersion(File file, String version) {
		return updateXmlVersion(file, version, false);
	}

	public static boolean updateXmlVersion(String file, String version,
			boolean allowRestrictedChars) {
		return updateXmlVersion(new File(file), version, false);
	}

	public static boolean updateXmlVersion(File file, String version,
			boolean allowRestrictedChars) {
		boolean status = false;
		String fileName = (file == null ? "" : file.getName());
		if (fileName.endsWith(".xml")) {
			try {
				String content = FileUtils.readFileAsString(file
						.getAbsolutePath());
				int tmpIndex = content.toLowerCase().indexOf("<?xml");
				if (tmpIndex >= 0) {
					tmpIndex = content.indexOf('>', tmpIndex);
					if (tmpIndex >= 0) {
						Matcher m = Pattern.compile(
								"version=\\\"\\d\\.\\d\\\"",
								Pattern.CASE_INSENSITIVE).matcher(content);
						if (m.find()) {
							String group = m.group();
							int index = content.indexOf(group);
							if (index < tmpIndex) {
								StringBuilder sb = new StringBuilder(
										content.substring(0, index));
								sb.append("version=\"" + version + "\"");
								sb.append(content.substring(index
										+ group.length()));
								/**
								 * take out every non valid XML character
								 */
								content = sb.toString();
								index = -2; // set start to '-2' in order to
											// start from '0'
								// since it adds '2' in the 'from' parameter of
								// the 'indexOf' method of the string
								while ((index = content
										.indexOf("&#", index + 2)) >= 0) {
									String charVal = content.substring(
											index + 2,
											content.indexOf(';', index));
									if (!isLegalXmlCharacter(charVal, version,
											allowRestrictedChars)) {
										content = content.replace("&#"
												+ charVal + ";", "");
									}
								}
								FileUtils.writeStringToFile(file, content);
								status = true;
							}
						}
					}
				}
			} catch (Exception e) {
				status = false;
			}
		}
		return status;
	}

	public static boolean isLegalXmlCharacter(String charVal,
			String xmlVersion, boolean allowRestrictedChars) {
		boolean status = false;
		int base = 10;
		char c = Character.toLowerCase(charVal.charAt(0));
		if (c == 'x') {
			base = 16;
		} else if (c == 'o' || (c == '0' && charVal.length() > 1)) {
			base = 8;
		}
		int val = Integer.parseInt(charVal.substring(base == 10 ? 0 : 1), base);
		if ("1.0".equals(xmlVersion)) {
			status = !(val != 0x09 && val != 0x0A && val != 0x0D
					&& (val < 0x20 || val > 0xD7FF)
					&& (val < 0xE000 || val > 0xFFFD) && (val < 0x10000 || val > 0x10FFFF));
		} else if ("1.1".equals(xmlVersion)) {
			status = !((val < 0x01 || val > 0xD7FF)
					&& (val < 0xE000 || val > 0xFFFD) && (val < 0x10000 || val > 0x10FFFF));
			if (!allowRestrictedChars) {
				status = status
						& !((val >= 0x001 && val <= 0x08)
								|| (val >= 0x0B && val <= 0x0C)
								|| (val >= 0x0E && val <= 0x1F)
								|| (val >= 0x7F && val <= 0x84) || (val >= 0x86 && val <= 0x9F));
			}
		}
		return status;
	}

	public static void saveDocumentToFile(Document doc, String version,
			File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		Source source = new DOMSource(doc);
		Result result = new StreamResult(fos);
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty(OutputKeys.VERSION, version);
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"4");
		xformer.transform(source, result);
		fos.flush();
		fos.close();
	}

	/**
	 * returns the given tag value within the given XML file as String
	 * 
	 * @param xmlName
	 *            full path and file name of the XML file
	 * @param mainElement
	 *            name of the XML's main element
	 * @param tagName
	 *            requested XML tag name within the XML main element
	 * @return the requested tag's value or "" if error occurred
	 * @throws Exception
	 */
	public static String getTextValueFromTag(String xmlName,
			String mainElement, String tagName) throws Exception {

		File f = new File(xmlName);

		if (f.exists()) {
			return getTextValueFromTag(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(f), mainElement, tagName);
		} else {
			return getTextValueFromTag((Document) null, mainElement, tagName);
		}
	}

	/**
	 * returns the given tag value within the given XML file as String
	 * 
	 * @param doc
	 *            initiated Document from the user
	 * @param mainElement
	 *            name of the XML's main element
	 * @param tagName
	 *            requested XML tag name within the XML main element
	 * @return the requested tag's value or "" if error occurred
	 * @throws Exception
	 */
	public static String getTextValueFromTag(Document doc, String mainElement,
			String tagName) throws Exception {
		if (doc != null) {
			Element e = (Element) XPathAPI.selectSingleNode(doc, "//"
					+ mainElement + "/" + tagName);
			if (e == null) {
				return "";
			}
			return e.getTextContent();
		}
		return "";
	}

	public static String getAttributeValue(String xmlName, String xpath,
			String attributeName) throws Exception {
		File f = new File(xmlName);
		if (f.exists()) {
			return getAttributeValue(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(f), xpath, attributeName);
		} else {
			return getAttributeValue((Document) null, xpath, attributeName);
		}
	}

	public static String getAttributeValue(Document doc, String xpath,
			String attributeName) throws Exception {
		if (doc != null) {
			Element e = (Element) XPathAPI.selectSingleNode(doc, xpath);
			if (e != null) {
				return e.getAttribute(attributeName);
			}
		}
		return null;
	}

	public static NodeList getNodeList(String xmlName, String xpath)
			throws Exception {

		File f = new File(xmlName);
		if (f.exists()) {
			return getNodeList(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(f), xpath);
		} else {
			return getNodeList((Document) null, xpath);
		}

	}

	public static NodeList getNodeList(Document doc, String xpath)
			throws Exception {
		if (doc != null) {
			return XPathAPI.selectNodeList(doc, xpath);
		}
		return null;
	}

	public static NodeList getNodeList(Node mainNode, String xpath)
			throws Exception {

		NodeList nl = XPathAPI.selectNodeList(mainNode, xpath);

		return nl;

	}

	public static Element getSingleNode(Node node, String xpath)
			throws Exception {
		if (node != null) {
			return (Element) XPathAPI.selectSingleNode(node, xpath);
		}
		return null;
	}

	public static String getXmlFileContent(String xmlFileName)
			throws IOException {
		File f = new File(xmlFileName);
		if (!f.exists()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void addTag(String xmlName, String mainElement,
			String tagName, String textValue, String... attributesPairs)
			throws Exception {
		File f = new File(xmlName);

		Document doc = null;

		if (!f.exists()) {

			f.createNewFile();

			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();

			Element e = doc.createElement(mainElement);

			doc.appendChild(e);

		} else {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(f);
		}

		Element e = (Element) XPathAPI.selectSingleNode(doc, mainElement);

		Element newNode = doc.createElement(tagName);
		newNode.setTextContent(textValue);
		if (attributesPairs != null) {
			for (int i = 0; i < attributesPairs.length; i = i + 2) {
				newNode.setAttribute(attributesPairs[i], attributesPairs[i + 1]);
			}
		}
		e.appendChild(newNode);

		Source source = new DOMSource(doc);

		FileOutputStream out = new FileOutputStream(f);

		Result result = new StreamResult(out);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();

		xformer.transform(source, result);

		out.close();

	}

	public static void addTag(String xmlName, String mainElement,
			String tagName, String textValue) throws Exception {
		String[] x = null;
		addTag(xmlName, mainElement, tagName, textValue, x);

	}

	/**
	 * Parse XML to Document from String
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document parseXmlString(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);

		return doc;
	}

	public static String getStringXml(Document doc) {
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

}
