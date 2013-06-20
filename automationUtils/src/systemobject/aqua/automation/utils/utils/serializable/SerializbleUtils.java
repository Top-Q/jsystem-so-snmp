package systemobject.aqua.automation.utils.utils.serializable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import systemobject.aqua.automation.utils.utils.publish.DbProperties;

/**
 * @author Uri.Koaz
 */
public class SerializbleUtils {

	public static void writeObject(Object obj, String fileName)
			throws Exception {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(fileName);
		out = new ObjectOutputStream(fos);
		out.writeObject(obj);
		out.close();
	}

	public static Object getSerlaizedObject(String fileName) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;

		fis = new FileInputStream(fileName);
		in = new ObjectInputStream(fis);

		Object obj = in.readObject();
		in.close();

		return obj;
	}

	public static void main(String[] args) throws Exception {
		String ip = DbProperties.DB_HOST.getValue();
		writeObject(ip, "./bla.txt");

		System.out.println(getSerlaizedObject("./bla.txt"));
	}
}