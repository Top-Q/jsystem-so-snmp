package systemobject.aqua.automation.utils.utils.capture;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * @author Uri.Koaz
 */
public class ImageCaptureUtils {

	static BufferedImage scaleImage(BufferedImage sourceImage, int scaledWidth) {
		float scale = scaledWidth / (float) sourceImage.getWidth();
		int scaledHeight = (int) (sourceImage.getHeight() * scale);
		Image scaledImage = sourceImage.getScaledInstance(scaledWidth,
				scaledHeight, Image.SCALE_AREA_AVERAGING);

		BufferedImage bufferedImage = new BufferedImage(
				scaledImage.getWidth(null), scaledImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(scaledImage, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}

	/**
	 * @param captureWidth
	 *            the width of the image produced (is used to make the bitmap
	 *            size smaller)
	 * @param compressionQuelity
	 *            default is .75f for lower quelity 0.1f for higher quelity
	 *            write .9f
	 * @return byte[] of the image to write
	 * @throws AWTException
	 * @throws IOException
	 */
	public static byte[] screenCapture(int captureWidth,
			float compressionQuelity) throws AWTException, IOException {

		BufferedImage screencapture = new Robot()
				.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit()
						.getScreenSize()));
		if (captureWidth != -1) {
			screencapture = scaleImage(screencapture, captureWidth);
		}
		Iterator<ImageWriter> iter = ImageIO
				.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(compressionQuelity);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageOutputStream output = ImageIO.createImageOutputStream(out);
		writer.setOutput(output);
		IIOImage image = new IIOImage(screencapture, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		return out.toByteArray();
	}

	/**
	 * @param captureWidth
	 *            the width of the image produced (is used to make the bitmap
	 *            size smaller)
	 * @param compressionQuelity
	 *            default is .75f for lower quelity 0.1f for higher quelity
	 *            write .9f
	 * @return byte[] of the image to write
	 * @throws AWTException
	 * @throws IOException
	 */
	public static byte[] screenCapture(int x, int y, int width, int height,
			int captureWidth, float compressionQuelity) throws AWTException,
			IOException {
		BufferedImage screencapture = new Robot()
				.createScreenCapture(new Rectangle(x, y, width, height));

		if (captureWidth != -1) {
			screencapture = scaleImage(screencapture, captureWidth);
		}
		Iterator<?> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = (ImageWriter) iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(compressionQuelity);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageOutputStream output = ImageIO.createImageOutputStream(out);
		writer.setOutput(output);
		IIOImage image = new IIOImage(screencapture, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		return out.toByteArray();
	}

	public static void main(String args[]) {
		File f = new File(System.getProperty("user.dir") + "/hello world.jpg");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(screenCapture(700, .9f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
